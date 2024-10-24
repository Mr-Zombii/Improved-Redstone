package me.zombii.improved_redstone;

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
        IMRPOVED_COMPARATOR = create("comparator", ImprovedComparatorBlockEntity::new, ImprovedBlocks.IMPROVED_REDSTONE_COMPARATOR);
        RGB_PANEL = create("rgb_panel", RGBPanelBlockEntity::new, ImprovedBlocks.RGB_PANEL);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntityFactory<? extends T> blockEntityFactory, Block... blocks) {
        if (blocks.length == 0) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }

        Util.getChoiceType(TypeReferences.BLOCK_ENTITY, String.valueOf(Identifier.of(ImprovedRedstone.MOD_ID, id)));
        BlockEntityType type;
        try {
            Constructor<?> constructor = BlockEntityType.class.getDeclaredConstructor(BlockEntityType.BlockEntityFactory.class, Set.class);
            constructor.setAccessible(true);
            type = (BlockEntityType) constructor.newInstance(blockEntityFactory, Set.of(blocks));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ImprovedRedstone.MOD_ID, id), type);
    }


}
