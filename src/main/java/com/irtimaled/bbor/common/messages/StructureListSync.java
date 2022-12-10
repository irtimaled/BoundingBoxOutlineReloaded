package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.StructureProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class StructureListSync {

    public static final String NAME = "bbor:structure_list_sync_v1";

    public static @NotNull PayloadBuilder getPayload() {
        final PayloadBuilder builder = PayloadBuilder.clientBound(NAME);
        final Set<String> supportedStructureIds = StructureProcessor.supportedStructureIds;
        builder.writeVarInt(supportedStructureIds.size());
        for (String structureId : supportedStructureIds) {
            builder.writeString(structureId);
        }
        return builder;
    }
}
