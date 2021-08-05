package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.ConduitRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxConduit extends BoundingBoxSphere {
    private static final AbstractRenderer<BoundingBoxConduit> RENDERER = CommonInterop.registerRenderer(BoundingBoxConduit.class, () -> new ConduitRenderer());

    private final int level;

    private BoundingBoxConduit(Coords coords, int level, int radius) {
        super(new Point(coords).offset(0.5D, 0.5D, 0.5D), radius, BoundingBoxType.Conduit);

        this.level = level;
    }

    public BoundingBoxConduit(Coords coords, int level) {
        this(coords, level, 16 * level);
    }

    @Override
    public int hashCode() {
        return TypeHelper.combineHashCodes(getType().hashCode(), getPoint().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBoxConduit other = (BoundingBoxConduit) obj;
        return getPoint().equals(other.getPoint());
    }

    public int getLevel() {
        return level;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
