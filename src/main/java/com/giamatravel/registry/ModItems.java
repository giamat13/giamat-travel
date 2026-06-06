package com.giamatravel.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.giamatravel.Giamatravel;
import com.giamatravel.content.horse.HorseshoeItem;
import com.giamatravel.content.horse.HorseshoeTier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
	/** Items added to the creative menu (in insertion order). Populated on the client. */
	private static final List<Item> CREATIVE_ITEMS = new ArrayList<>();

	public static final Map<HorseshoeTier, HorseshoeItem> HORSESHOES = new EnumMap<>(HorseshoeTier.class);

	private ModItems() {
	}

	public static void register() {
		for (HorseshoeTier tier : HorseshoeTier.values()) {
			HorseshoeItem item = register(
					tier.itemName(),
					props -> new HorseshoeItem(tier, props.stacksTo(1).enchantable(tier.enchantmentValue())));
			HORSESHOES.put(tier, item);
		}
	}

	/** All mod items, in registration order — used to populate the creative tab (client side). */
	public static List<Item> creativeItems() {
		return Collections.unmodifiableList(CREATIVE_ITEMS);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, name));
		T item = factory.apply(new Item.Properties().setId(key));
		Registry.register(BuiltInRegistries.ITEM, key, item);
		CREATIVE_ITEMS.add(item);
		return item;
	}
}
