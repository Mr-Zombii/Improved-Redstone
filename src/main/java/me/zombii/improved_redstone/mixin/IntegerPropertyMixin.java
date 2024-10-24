package me.zombii.improved_redstone.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.state.State;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(State.class)
public class IntegerPropertyMixin<O, S> {

    @Shadow @Final private Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap;

    @Shadow @Final protected O owner;

    @Shadow private Map<Property<?>, S[]> withMap;

    /**
     * @author Mr_Zombii
     * @reason Fix Broken Values
     */
    @Overwrite
    public <T extends Comparable<T>, V extends T> S with(Property<T> property, V value) {
        if (property instanceof IntProperty) {
            return with2((IntProperty) property, (Integer) value);
        }
        return withi(property, value);
    }

    public <T extends Comparable<T>, V extends T> S withi(Property<T> property, V value) {
        Comparable<?> comparable = this.propertyMap.get(property);
        if (comparable == null) {
            String var10002 = String.valueOf(property);
            throw new IllegalArgumentException("Cannot set property " + var10002 + " as it does not exist in " + this.owner);
        } else {
            if (comparable.equals(value)) {
                return (S) this;
            } else {
                int i = property.ordinal(value);
                if (i < 0) {
                    String var10002 = String.valueOf(property);
                    throw new IllegalArgumentException("Cannot set property " + var10002 + " to " + value + " on " + this.owner + ", it is not an allowed value");
                } else {
                    return (S) ((Object[])this.withMap.get(property))[i];
                }
            }
        }
    }

    public <T extends Comparable<T>, V extends T> S with2(IntProperty property, Integer value) {
        Integer[] properties = property.getValues().toArray(new Integer[0]);

        if (value > property.max) {
            value = properties[properties.length-1];
        }
        if (value < property.min) {
            value = properties[0];
        }
        return withi(property, value);
    }

}
