package com.giamatravel.content.horse;

import net.minecraft.world.item.Item;

/**
 * A horseshoe. Equipped onto a (tamed) horse by right-clicking it; grants the tier's speed and
 * jump bonus. Enchantable as foot armor (Feather Falling / Frost Walker / Soul Speed, etc.).
 */
public class HorseshoeItem extends Item {
	private final HorseshoeTier tier;

	public HorseshoeItem(HorseshoeTier tier, Item.Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public HorseshoeTier getTier() {
		return tier;
	}
}
