package me.zombii.improved_redstone;

import me.zombii.improved_redstone.blocks.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

public class ImprovedBlocks {

    public static Block IMPROVED_REDSTONE_REPEATER;
    public static Block IMPROVED_REDSTONE_COMPARATOR;

    public static Block IMPROVED_REDSTONE_TORCH;
    public static Block IMPROVED_REDSTONE_WALL_TORCH;

    public static Block IMPROVED_OBSERVER;
    public static Block IMPROVED_REDSTONE_LAMP;

    public static Block IMPROVED_REDSTONE;
    public static Block IMPROVED_REDSTONE_BLOCK;

    public static Block RGB_PANEL;

    static Block register(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = factory.apply(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));
        return Registry.register(Registries.BLOCK, id, block);
    }

    static Block register(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register(Identifier.of(ImprovedRedstone.MOD_ID, name), factory, settings);
    }

    private static AbstractBlock.Settings copyLootTable(Block block, boolean copyTranslationKey) {
        AbstractBlock.Settings settings2 = AbstractBlock.Settings.create().lootTable(block.getLootTableKey());
        if (copyTranslationKey) {
            settings2 = settings2.overrideTranslationKey(block.getTranslationKey());
        }

        return settings2;
    }

    public static void registerBlocks() {
        IMPROVED_REDSTONE_REPEATER = register(
                "improved_repeater",
                ImprovedRedstoneRepeaterBlock::new,
                AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE)
                        .pistonBehavior(PistonBehavior.DESTROY)
        );

        IMPROVED_REDSTONE_COMPARATOR = register(
                "improved_comparator",
                ImprovedComparatorBlock::new,
                AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE)
                        .pistonBehavior(PistonBehavior.DESTROY)
        );

        IMPROVED_REDSTONE_TORCH = register(
                "improved_redstone_torch",
                ImprovedRedstoneTorchBlock::new,
                AbstractBlock.Settings.create().noCollision().breakInstantly()
                        .luminance(createLightLevelFromLitBlockState(7))
                        .sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
        );
        IMPROVED_REDSTONE_WALL_TORCH = register(
                "improved_redstone_wall_torch",
                ImprovedRedstoneWallTorchBlock::new,
                copyLootTable(IMPROVED_REDSTONE_TORCH, true).noCollision().breakInstantly()
                        .luminance(createLightLevelFromLitBlockState(7))
                        .sounds(BlockSoundGroup.WOOD)
                        .pistonBehavior(PistonBehavior.DESTROY)
        );

        IMPROVED_REDSTONE_LAMP = register(
                "improved_redstone_lamp",
                ImprovedRedstoneLamp::new,
                AbstractBlock.Settings.create().luminance(createLightLevelFromLitBlockState(15))
                        .strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning(Blocks::always)
        );

        IMPROVED_OBSERVER = register(
                "improved_observer",
                ImprovedObserverBlock::new,
                AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F)
                        .solidBlock(Blocks::never)
        );

        IMPROVED_REDSTONE = register(
                "improved_redstone",
                ImprovedRedstoneWireBlock::new,
                AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY)
        );

        IMPROVED_REDSTONE_BLOCK = register(
                "improved_redstone_block",
                ImprovedRedstoneBlock::new,
                AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED)
                        .strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).solidBlock(Blocks::never)
        );

        RGB_PANEL = register(
                "rgb_panel",
                RGBPanel::new,
                AbstractBlock.Settings.create().nonOpaque().luminance((state) -> state.get(RGBPanel.LIT) ? 15 : 0)
        );
    }

}
