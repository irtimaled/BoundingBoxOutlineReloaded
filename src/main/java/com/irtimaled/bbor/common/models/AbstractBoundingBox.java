package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

import java.awt.*;

public abstract class AbstractBoundingBox {
    private final Coords minCoords;
    private final Coords maxCoords;
    private final BoundingBoxType type;

    protected AbstractBoundingBox(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        this.minCoords = minCoords;
        this.maxCoords = maxCoords;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + minCoords.hashCode();
        result = prime * result + maxCoords.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractBoundingBox other = (AbstractBoundingBox) obj;
        return minCoords.equals(other.minCoords) && maxCoords.equals(other.maxCoords);
    }

    @Override
    public String toString() {
        return "(" + minCoords.toString() + "; " + maxCoords.toString() + ")";
    }

    public Coords getMinCoords() {
        return minCoords;
    }

    public Coords getMaxCoords() {
        return maxCoords;
    }

    public Color getColor() {
        return type.getColor();
    }

    public Boolean shouldRender() {
        return type.shouldRender();
    }

    public String getTypeName() {
        return type.getName();
    }
}
