package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.StructureProcessor;

public class StructureUtil {

    public static BoundingBoxType registerStructureIfNeeded(String structureId) {
        final String name = "structure:" + structureId;
        if (!BoundingBoxType.isRegistered(name)) {
            System.out.println("Registering structure: %s".formatted(structureId));
            final BoundingBoxType boundingBoxType = BoundingBoxType.register(name);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
            BoundingBoxTypeHelper.registerType(boundingBoxType, ConfigManager.structureShouldRender(structureId), ConfigManager.structureColor(structureId));
            return boundingBoxType;
        }
        return BoundingBoxType.register(name);
    }

}
