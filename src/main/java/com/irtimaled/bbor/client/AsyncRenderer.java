package com.irtimaled.bbor.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.RenderHelper;
import com.irtimaled.bbor.client.renderers.RenderingContext;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger("BBOR AsyncRenderer");

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
    private static boolean toDiscardBuild = false;
    private static long lastBuildTime = System.currentTimeMillis();
    private static AtomicLong lastDurationNanos = new AtomicLong(0L);
    private static DimensionId lastDimID = null;
    private static RenderingContext lastCtx;

    static void render(MatrixStack matrixStack, DimensionId dimensionId) {
        runCleanup();

        if (!ClientRenderer.getActive()) {
            // invalidate async things
            if (currentAsyncContext != -1) {
                currentAsyncContext = -1;
                toDiscardBuild = true;
            }
            DEFAULT.hardReset();
            if (buildingFuture == null) {
                for (RenderingContext context : asyncContexts) {
                    context.hardReset();
                }
            }
            return;
        }

        long startTime = System.nanoTime();
        RenderHelper.beforeRender();

        final Boolean useAsync = ConfigManager.asyncBuilding.get();
        if (useAsync) {
            DEFAULT.hardReset();
            final int i = getNextAsyncContext(dimensionId);
            if (i != -1) {
                final RenderingContext ctx = asyncContexts[i];
                draw(matrixStack, ctx);
                lastCtx = ctx;
            } else {
                lastCtx = null;
            }
        } else {
            // invalidate async things
            currentAsyncContext = -1;
            toDiscardBuild = true;

            final RenderingContext ctx = DEFAULT;

            build0(dimensionId, ctx);

            draw(matrixStack, ctx);

            RenderCulling.flushRendering();

            lastCtx = ctx;
        }
        RenderHelper.afterRender();

        lastDurationNanos.set(System.nanoTime() - startTime);
    }

    private static void draw(MatrixStack matrixStack, RenderingContext ctx) {
        matrixStack.push();
        matrixStack.translate(
                ctx.getBaseX() - Camera.getX(),
                ctx.getBaseY() - Camera.getY(),
                ctx.getBaseZ() - Camera.getZ()
        );
        ctx.doDrawing(matrixStack);
        matrixStack.pop();
    }

    private static int getNextAsyncContext(DimensionId dimensionId) {
        if (dimensionId != lastDimID) {
            currentAsyncContext = -1;
            lastDimID = dimensionId;
            toDiscardBuild = true;
        }
        if ((buildingFuture == null || buildingFuture.isDone()) && lastBuildTime + 2000 < System.currentTimeMillis()) {
            lastBuildTime = System.currentTimeMillis();
            RenderingContext ctx = asyncContexts[(currentAsyncContext + 1) % asyncContexts.length];
            buildingFuture = CompletableFuture.runAsync(() -> build0(dimensionId, ctx), EXECUTOR)
                    .exceptionallyAsync(throwable -> {
                        LOGGER.error("Error occurred while building buffers async", throwable);
                        MinecraftClient.getInstance().inGameHud.addChatMessage(
                                MessageType.SYSTEM,
                                new LiteralText("[BBOR] Error occurred while building buffers async, check logs and try re-enabling rendering: " + throwable.toString())
                                        .setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromFormatting(Formatting.RED))),
                                Util.NIL_UUID);
                        return null;
                    }, MinecraftClient.getInstance());
        }
        return currentAsyncContext;
    }

    private static void runCleanup() {
        if (buildingFuture != null && buildingFuture.isDone()) {
            if (!toDiscardBuild) {
                currentAsyncContext = (currentAsyncContext + 1) % asyncContexts.length;
            } else {
                toDiscardBuild = false;
            }
            buildingFuture = null;
        }
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

    public static String renderingDebugString() {
        final RenderingContext ctx = lastCtx;
        if (ctx == null) {
            return "[BBOR] Preparing rendering...";
        } else {
            return ctx.debugString();
        }
    }

}
