package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class NMSFieldGetter {

    private final Field field;
    private final Class<?> nmsClass;

    public NMSFieldGetter(@NotNull NMSFieldDescribe describe) throws NoSuchFieldException {
        this.nmsClass = NMSHelper.getNMSClass(describe.className());
        this.field = getField(describe);
        this.field.setAccessible(true);
    }

    @NotNull
    private static Field getField(@NotNull NMSFieldDescribe describe) throws NoSuchFieldException {
        Class<?> clazz = NMSHelper.getNMSClass(describe.className());

        do {
            try {
                return clazz.getDeclaredField(describe.fieldName());
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz.getSuperclass() != null);

        throw new NoSuchFieldException(describe.fieldName());
    }

    @Nullable
    public Object get(Object obj) {
        try {
            return field.get(nmsClass.cast(obj));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
