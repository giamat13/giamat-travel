package com.giamatravel.event;

import java.util.Map;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;

/**
 * Tweaks default components of vanilla items. Makes horse armor enchantable so it can receive the
 * Protection family of enchantments (the items are also added to the {@code #enchantable/armor}
 * tags via datapack so the enchantments actually apply).
 */
public final class ModComponentTweaks {
	/** Vanilla horse armors and the enchantment power to give each. */
	private static final Map<Item, Integer> HORSE_ARMOR_ENCHANTABILITY = Map.of(
			Items.LEATHER_HORSE_ARMOR, 15,
			Items.IRON_HORSE_ARMOR, 9,
			Items.GOLDEN_HORSE_ARMOR, 22,
			Items.DIAMOND_HORSE_ARMOR, 10,
			Items.COPPER_HORSE_ARMOR, 9,
			Items.NETHERITE_HORSE_ARMOR, 10);

	private ModComponentTweaks() {
	}

	public static void register() {
		DefaultItemComponentEvents.MODIFY.register(context ->
				HORSE_ARMOR_ENCHANTABILITY.forEach((item, value) ->
						context.modify(item, builder -> builder.set(DataComponents.ENCHANTABLE, new Enchantable(value)))));
	}
}
