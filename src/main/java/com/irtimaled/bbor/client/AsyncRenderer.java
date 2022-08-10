package com.irtimaled.bbor.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.RenderHelper;
import com.irtimaled.bbor.client.renderers.RenderingContext;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncRenderer {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("BBOR Building Thread").setDaemon(true).build()
    );

    private static final RenderingContext DEFAULT = new RenderingContext();

    private static final RenderingContext[] asyncContexts = new RenderingContext[] {
            new RenderingContext(),
            new RenderingContext(),
    };
    private static int currentAsyncContext = -1;
    private static CompletableFuture<Void> buildingFuture = null;
    private static long lastBuildTime = System.currentTimeMillis();
    private static AtomicLong lastDurationNanos = new AtomicLong(0L);
    private static DimensionId lastDimID = null;
    private static RenderingContext lastCtx;

    static void render(MatrixStack matrixStack, DimensionId dimensionId) {
        long startTime = System.nanoTime();
        RenderHelper.beforeRender();

        final Boolean useAsync = ConfigManager.asyncBuilding.get();
        if (useAsync) {
            final int i = getNextAsyncContext(dimensionId);
            if (i != -1) {
                final RenderingContext ctx = asyncContexts[i];
                matrixStack.push();
                matrixStack.translate(
                        Camera.getX() - ctx.getBaseX(),
                        Camera.getY() - ctx.getBaseY(),
                        Camera.getZ() - ctx.getBaseZ()
                );
                ctx.doDrawing(matrixStack);
                matrixStack.pop();
                lastCtx = ctx;
            } else {
                lastCtx = null;
            }
        } else {
            // invalidate async things
            currentAsyncContext = -1;
            buildingFuture = null;

            final RenderingContext ctx = DEFAULT;

            build0(dimensionId, ctx);

            RenderHelper.beforeRender();
            matrixStack.push();
            matrixStack.translate(
                    Camera.getX() - ctx.getBaseX(),
                    Camera.getY() - ctx.getBaseY(),
                    Camera.getZ() - ctx.getBaseZ()
            );
            ctx.doDrawing(matrixStack);
            matrixStack.pop();
            RenderHelper.afterRender();

            RenderCulling.flushRendering();

            lastCtx = ctx;
        }
        RenderHelper.afterRender();

        lastDurationNanos.set(System.nanoTime() - startTime);
    }

    private static int getNextAsyncContext(DimensionId dimensionId) {
        if (dimensionId != lastDimID) {
            currentAsyncContext = -1;
            buildingFuture = null;
            lastDimID = dimensionId;
        }
        if (buildingFuture != null && buildingFuture.isDone()) {
            currentAsyncContext = (currentAsyncContext + 1) % asyncContexts.length;
            buildingFuture = null;
        }
        if ((buildingFuture == null || buildingFuture.isDone()) && lastBuildTime + 2000 < System.currentTimeMillis()) {
            lastBuildTime = System.currentTimeMillis();
            RenderingContext ctx = asyncContexts[(currentAsyncContext + 1) % asyncContexts.length];
            buildingFuture = CompletableFuture.runAsync(() -> build0(dimensionId, ctx), EXECUTOR);
        }
        return currentAsyncContext;
    }

    private static void build0(DimensionId dimensionId, RenderingContext ctx) {
        ctx.reset();
        ctx.beginBatch();

        final List<AbstractBoundingBox> boundingBoxes = ClientRenderer.getBoundingBoxes(dimensionId);
        RenderCulling.flushPreRendering();
        for (AbstractBoundingBox key : boundingBoxes) {
            AbstractRenderer renderer = key.getRenderer();
            if (renderer != null) renderer.render(ctx, key);
        }
        ctx.endBatch();
    }

    public static long getLastDurationNanos() {
        return lastDurationNanos.get();
    }

    public static String contextDebugString() {
        final RenderingContext ctx = lastCtx;
        if (ctx == null) {
            return "[BBOR] Preparing rendering...";
        } else {
            return ctx.debugString();
        }
    }

}
