package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class BoundingBoxFlowerForest extends AbstractBoundingBox {
    private final Coords coords;
    private final Setting<HexColor> colorSetting;

    public BoundingBoxFlowerForest(Coords coords, Setting<HexColor> colorSetting) {
        super(BoundingBoxType.FlowerForest);
        this.coords = coords;
        this.colorSetting = colorSetting;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return coords.getX() >= minX && coords.getZ() >= minZ && coords.getX() <= maxX && coords.getZ() <= maxZ;
    }

    public Color getColor() {
        return ColorHelper.getColor(colorSetting);
    }

    public Coords getCoords() {
        return coords;
    }

    @Override
    public double getDistanceX(double x) {
        return x - coords.getX();
    }

    @Override
    public double getDistanceY(double y) {
        return y - coords.getY();
    }

    @Override
    public double getDistanceZ(double z) {
        return z - coords.getZ();
    }
}
