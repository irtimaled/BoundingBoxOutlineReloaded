package com.ostlerdev.bbreloaded;

import java.awt.*;

public class StructureType {
    private static final int JUNGLE_TEMPLE = 1;
    private static final int DESERT_TEMPLE = 2;
    private static final int WITCH_HUT = 3;
    private static final int OCEAN_MONUMENT = 4;
    private static final int STRONGHOLD = 5;
    private static final int MINE_SHAFT = 6;
    private static final int NETHER_FORTRESS = 7;

    public final static StructureType JungleTemple = new StructureType(JUNGLE_TEMPLE);
    public final static StructureType DesertTemple = new StructureType(DESERT_TEMPLE);
    public final static StructureType WitchHut = new StructureType(WITCH_HUT);
    public final static StructureType OceanMonument = new StructureType(OCEAN_MONUMENT);
    public final static StructureType Stronghold = new StructureType(STRONGHOLD);
    public final static StructureType MineShaft = new StructureType(MINE_SHAFT);
    public final static StructureType NetherFortress = new StructureType(NETHER_FORTRESS);


    private final int type;

    private StructureType(int type) {

        this.type = type;
    }

    public Color getColor() {
        switch (type) {
            case DESERT_TEMPLE:
                return Color.ORANGE;
            case JUNGLE_TEMPLE:
                return Color.GREEN;
            case WITCH_HUT:
                return Color.BLUE;
            case MINE_SHAFT:
                return Color.LIGHT_GRAY;
            case NETHER_FORTRESS:
                return Color.RED;
            case OCEAN_MONUMENT:
                return Color.CYAN;
            case STRONGHOLD:
                return Color.YELLOW;
        }
        return Color.WHITE;
    }
}
