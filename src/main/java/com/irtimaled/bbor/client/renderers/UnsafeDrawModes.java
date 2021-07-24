package com.irtimaled.bbor.client.renderers;

import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL11;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeDrawModes {

    public static final VertexFormat.DrawMode LINE_LOOP = newDrawMode("LINE_LOOP", RenderHelper.LINE_LOOP, 2, 2);
    public static final VertexFormat.DrawMode POINTS = newDrawMode("POINTS", RenderHelper.POINTS, 2, 2);

    private static VertexFormat.DrawMode newDrawMode(String name, int mode, int vertexCount, int size) {
        try {
            synchronized (VertexFormat.DrawMode.class) {
                // allocate new instance
                final VertexFormat.DrawMode instance = (VertexFormat.DrawMode) getUnsafe().allocateInstance(VertexFormat.DrawMode.class);

                // semi-initializer
                final int nextOrdinal = VertexFormat.DrawMode.values().length;
                accessible(Enum.class.getDeclaredField("ordinal")).set(instance, nextOrdinal);
                accessible(Enum.class.getDeclaredField("name")).set(instance, name);
                accessible(VertexFormat.DrawMode.class.getDeclaredField("mode")).set(instance, mode);
                accessible(VertexFormat.DrawMode.class.getDeclaredField("vertexCount")).set(instance, vertexCount);
                accessible(VertexFormat.DrawMode.class.getDeclaredField("size")).set(instance, size);

                // register value to enum
                final Field valuesField = accessible(findValuesField(VertexFormat.DrawMode.class));
                final VertexFormat.DrawMode[] oldValues = (VertexFormat.DrawMode[]) valuesField.get(null);
                final VertexFormat.DrawMode[] newValues = new VertexFormat.DrawMode[oldValues.length + 1];
                System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
                newValues[oldValues.length] = instance;
                valuesField.set(null, newValues);
                accessible(Class.class.getDeclaredField("enumConstants")).set(VertexFormat.DrawMode.class, null); // prevent desync
                accessible(Class.class.getDeclaredField("enumConstantDirectory")).set(VertexFormat.DrawMode.class, null); // prevent desync
                return instance;
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            return (Unsafe) accessible(Unsafe.class.getDeclaredField("theUnsafe")).get(null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static Field accessible(Field field) {
        field.setAccessible(true);
        return field;
    }

    private static Field findValuesField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isArray() && field.getType().getComponentType() == clazz && field.isSynthetic()) {
                return field;
            }
        }
        throw new IllegalArgumentException();
    }

}
