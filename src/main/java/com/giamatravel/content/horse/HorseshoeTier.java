package com.giamatravel.content.horse;

import java.util.Locale;

import com.giamatravel.Giamatravel;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * The five horseshoe tiers. Each tier boosts the wearer's movement speed and jump strength
 * (applied as transient attribute modifiers while the horseshoe is equipped) and defines how
 * enchantable the item is and how hard it tramples mobs.
 *
 * Ordering (weakest to strongest): leather, iron, gold, diamond, netherite.
 */
public enum HorseshoeTier {
	// name           speed%  jump%  enchant  trample
	LEATHER("leather", 0.10F, 0.10F, 12, 1.0F),
	IRON("iron", 0.22F, 0.20F, 9, 2.0F),
	GOLD("gold", 0.40F, 0.28F, 22, 2.0F),
	DIAMOND("diamond", 0.55F, 0.45F, 10, 3.0F),
	NETHERITE("netherite", 0.75F, 0.60F, 10, 4.0F);

	/** Shared modifier ids so a new horseshoe simply replaces the previous one's bonus. */
	public static final Identifier SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "horseshoe_speed");
	public static final Identifier JUMP_MODIFIER_ID = Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "horseshoe_jump");

	private final String id;
	private final float speedBonus;
	private final float jumpBonus;
	private final int enchantmentValue;
	private final float trampleDamage;

	HorseshoeTier(String id, float speedBonus, float jumpBonus, int enchantmentValue, float trampleDamage) {
		this.id = id;
		this.speedBonus = speedBonus;
		this.jumpBonus = jumpBonus;
		this.enchantmentValue = enchantmentValue;
		this.trampleDamage = trampleDamage;
	}

	public String id() {
		return id;
	}

	/** "iron_horseshoe", used as the registry path. */
	public String itemName() {
		return id + "_horseshoe";
	}

	public int enchantmentValue() {
		return enchantmentValue;
	}

	public float trampleDamage() {
		return trampleDamage;
	}

	public AttributeModifier speedModifier() {
		return new AttributeModifier(SPEED_MODIFIER_ID, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	public AttributeModifier jumpModifier() {
		return new AttributeModifier(JUMP_MODIFIER_ID, jumpBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	public static HorseshoeTier byName(String name) {
		return valueOf(name.toUpperCase(Locale.ROOT));
	}
}
