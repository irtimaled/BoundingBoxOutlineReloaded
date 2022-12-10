package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.StructureProcessor;

import java.util.Set;

public class StructureListSync {

    public static final String NAME = "bbor:structure_list_sync_v1";

    public static PayloadBuilder getPayload() {
        final PayloadBuilder builder = PayloadBuilder.clientBound(NAME);
        final Set<String> supportedStructureIds = StructureProcessor.supportedStructureIds;
        builder.writeVarInt(supportedStructureIds.size());
        for (String structureId : supportedStructureIds) {
            builder.writeString(structureId);
        }
        return builder;
    }

    public static void handleEvent(PayloadReader reader) {
        final int size = reader.readVarInt();
        for (int i = 0; i < size; i++) {
            final String structureId = reader.readString();
            StructureUtil.registerStructureIfNeeded(structureId);
        }
    }

}
