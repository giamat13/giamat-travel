package com.giamatravel.registry;

import java.util.function.Function;

import com.giamatravel.Giamatravel;
import com.giamatravel.content.minecart.CopperRailBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	public static final CopperRailBlock COPPER_RAIL = register(
			"copper_rail",
			CopperRailBlock::new,
			BlockBehaviour.Properties.of().noCollision().strength(0.7F).sound(SoundType.METAL));

	private ModBlocks() {
	}

	public static void register() {
		// Static init registers the blocks above.
	}

	private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, name));
		T block = factory.apply(properties.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, name));
		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		ModItems.addCreativeItem(item);
		return block;
	}
}
