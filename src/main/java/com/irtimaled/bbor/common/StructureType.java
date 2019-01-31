package com.irtimaled.bbor.common;

import java.awt.*;

public class StructureType {
    public final static StructureType JungleTemple = new StructureType(Color.GREEN, "Jungle_Pyramid");
    public final static StructureType DesertTemple = new StructureType(Color.ORANGE, "Desert_Pyramid");
    public final static StructureType WitchHut = new StructureType(Color.BLUE, "Swamp_Hut");
    public final static StructureType OceanMonument = new StructureType(Color.CYAN, "Monument");
    public final static StructureType Shipwreck = new StructureType(Color.CYAN, "Shipwreck");
    public final static StructureType OceanRuin = new StructureType(Color.CYAN, "Ocean_Ruin");
    public final static StructureType BuriedTreasure = new StructureType(Color.CYAN, "Buried_Treasure");
    public final static StructureType Stronghold = new StructureType(Color.YELLOW, "Stronghold");
    public final static StructureType MineShaft = new StructureType(Color.LIGHT_GRAY, "Mineshaft");
    public final static StructureType NetherFortress = new StructureType(Color.RED, "Fortress");
    public final static StructureType EndCity = new StructureType(Color.MAGENTA, "EndCity");
    public final static StructureType Mansion = new StructureType(new Color(139, 69, 19), "Mansion");
    public final static StructureType Igloo = new StructureType(Color.WHITE, "Igloo");

    private final Color color;
    private String name;

    private StructureType(Color color, String name) {
        this.color = color;
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
