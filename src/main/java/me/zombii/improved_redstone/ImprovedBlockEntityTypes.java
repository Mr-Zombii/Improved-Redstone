package me.zombii.improved_redstone;

import com.mojang.datafixers.types.Type;
import me.zombii.improved_redstone.blocks.entities.ImprovedComparatorBlockEntity;
import me.zombii.improved_redstone.blocks.entities.RGBPanelBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static me.zombii.improved_redstone.ImprovedRedstone.LOGGER;

public class ImprovedBlockEntityTypes {

    public static BlockEntityType<ImprovedComparatorBlockEntity> IMRPOVED_COMPARATOR;
    public static BlockEntityType<RGBPanelBlockEntity> RGB_PANEL;

    public static void register() {
        IMRPOVED_COMPARATOR = create("comparator", BlockEntityType.Builder.create(ImprovedComparatorBlockEntity::new, ImprovedBlocks.IMPROVED_REDSTONE_COMPARATOR));
        RGB_PANEL = create("rgb_panel", BlockEntityType.Builder.create(RGBPanelBlockEntity::new, ImprovedBlocks.RGB_PANEL));
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        if (builder.blocks.isEmpty()) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }

        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return (BlockEntityType)Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ImprovedRedstone.MOD_ID, id), builder.build(type));
    }


}
