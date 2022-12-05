package com.irtimaled.bbor.common.messages.servux;

import com.google.common.base.Charsets;
import com.irtimaled.bbor.client.events.AddBoundingBoxReceived;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.PayloadBuilder;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.StructureUtil;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashSet;
import java.util.Set;

public class ServuxStructurePackets {

    public static final Identifier CHANNEL = new Identifier("servux:structures");

    public static final int PROTOCOL_VERSION = 1;
    public static final int PACKET_S2C_METADATA = 1;
    public static final int PACKET_S2C_STRUCTURE_DATA = 2;

    private static boolean registered = false;
    private static int timeout = Integer.MAX_VALUE;

    public static PayloadBuilder subscribe() {
        return PayloadBuilder.serverBound("minecraft:register")
                .writeBytes(CHANNEL.toString().getBytes(Charsets.UTF_8));
    }

    public static void markUnregistered() {
        registered = false;
    }

    public static void handleEvent(PayloadReader reader) {
        int id = reader.readVarInt();
        switch (id) {
            case PACKET_S2C_METADATA -> {
                final NbtCompound nbt = reader.handle().readNbt();
                registered = nbt != null && nbt.getInt("version") == PROTOCOL_VERSION &&
                        nbt.getString("id").equals(CHANNEL.toString());
                if (registered) timeout = nbt.getInt("timeout");
            }
            case PACKET_S2C_STRUCTURE_DATA -> {
                if (!registered) {
                    System.err.println("Received structure data on servux channel when not registered");
                    break;
                }
                if (MinecraftClient.getInstance().world == null) {
                    System.err.println("Received structure data on servux channel when not in a world ???");
                    break;
                }
                final NbtCompound nbt = reader.handle().readNbt();
                if (nbt == null) {
                    System.err.println("Received invalid structure data on servux channel");
                    break;
                }
                NbtList structures = nbt.getList("Structures", NbtElement.COMPOUND_TYPE);
                if (structures == null) {
                    System.err.println("Received invalid structure data on servux channel");
                    break;
                }
                for (NbtElement element : structures) {
                    if (element instanceof NbtCompound compound) {
                        final AddBoundingBoxReceived received = parseBoundingBox(compound);
                        if (received != null) EventBus.publish(received);
                    }
                }
            }
            default -> {
                System.err.println("Received unknown packet id on servux channel: " + id);
            }
        }
    }

    private static AddBoundingBoxReceived parseBoundingBox(NbtCompound nbt) {
        final String structureId = nbt.getString("id");

        final ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        Structure structure = null;
        try {
            structure = world.getRegistryManager().get(Registry.STRUCTURE_KEY).get(Identifier.tryParse(structureId));
        } catch (Throwable t) {
            System.err.println("Failed to resolve structure %s, outer box may be inaccurate".formatted(structureId));
            t.printStackTrace(System.err);
        }

        final BoundingBoxType boundingBoxType = StructureUtil.registerStructureIfNeeded(structureId);
//        System.out.println("Received %s from servux".formatted(structureId));

        Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
        BlockBox outerBox = null;

        NbtList pieces = nbt.getList("Children", NbtElement.COMPOUND_TYPE);
        for (NbtElement piece : pieces) {
            if (piece instanceof NbtCompound pieceCompound) {
                BlockBox blockBox = createBlockBox(pieceCompound.getIntArray("BB"));
                if (outerBox == null) outerBox = blockBox;
                else outerBox.encompass(blockBox);
                boundingBoxes.add(
                        BoundingBoxCuboid.from(
                                new Coords(blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ()),
                                new Coords(blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()),
                                boundingBoxType
                        )
                );
            }
        }

        if (structure != null) outerBox = structure.expandBoxIfShouldAdaptNoise(outerBox);

        if (boundingBoxes.size() == 0) {
            return null;
        } else {
            return new AddBoundingBoxReceived(
                    BoundingBoxCache.Type.REMOTE_SERVUX,
                    DimensionId.from(world.getRegistryKey()),
                    BoundingBoxCuboid.from(
                            new Coords(outerBox.getMinX(), outerBox.getMinY(), outerBox.getMinZ()),
                            new Coords(outerBox.getMaxX(), outerBox.getMaxY(), outerBox.getMaxZ()),
                            boundingBoxType
                    ),
                    boundingBoxes
            );
        }
    }

    private static BlockBox createBlockBox(int[] compound) {
        if (compound.length == 6)
            return new BlockBox(compound[0], compound[1], compound[2], compound[3], compound[4], compound[5]);
        else
            return new BlockBox(0, 0, 0, 0, 0, 0);
    }

}
