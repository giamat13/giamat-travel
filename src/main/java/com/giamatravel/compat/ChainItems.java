package com.giamatravel.compat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The set of items that can couple minecarts: vanilla iron / copper chains, plus the blocky13
 * chain variants when that mod is installed.
 */
public final class ChainItems {
	private static final List<String> VANILLA_CHAINS = List.of(
			"iron_chain", "copper_chain", "exposed_copper_chain", "weathered_copper_chain", "oxidized_copper_chain",
			"waxed_copper_chain", "waxed_exposed_copper_chain", "waxed_weathered_copper_chain", "waxed_oxidized_copper_chain");

	private static final List<String> BLOCKY13_CHAINS = List.of(
			"dirt_chain", "iron_block_chain", "coal_block_chain", "copper_block_chain", "gold_block_chain",
			"redstone_block_chain", "emerald_block_chain", "lapis_block_chain", "diamond_block_chain",
			"netherite_block_chain", "raw_iron_block_chain", "raw_copper_block_chain", "raw_gold_block_chain",
			"quartz_block_chain", "amethyst_block_chain");

	private static Set<Item> chains;

	private ChainItems() {
	}

	public static boolean isChain(ItemStack stack) {
		return chains().contains(stack.getItem());
	}

	/** The item dropped when a coupling snaps. */
	public static Item dropItem() {
		return Items.IRON_CHAIN;
	}

	private static Set<Item> chains() {
		if (chains == null) {
			Set<Item> set = new HashSet<>();
			add(set, "minecraft", VANILLA_CHAINS);
			if (FabricLoader.getInstance().isModLoaded("blocky13")) {
				add(set, "blocky13", BLOCKY13_CHAINS);
			}
			chains = set;
		}
		return chains;
	}

	private static void add(Set<Item> set, String namespace, List<String> paths) {
		for (String path : paths) {
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(namespace, path));
			if (item != null && item != Items.AIR) {
				set.add(item);
			}
		}
	}
}
