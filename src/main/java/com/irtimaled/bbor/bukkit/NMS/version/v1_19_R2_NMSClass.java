package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.api.NMSClassName;

public class v1_19_R2_NMSClass extends v1_19_R1_NMSClass {

    public v1_19_R2_NMSClass() throws ClassNotFoundException {
        super();
        addClassCache(NMSClassName.Registries, "net.minecraft.core.registries.Registries");
    }
}
