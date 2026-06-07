package com.giamatravel.event;

import java.util.Set;
import java.util.UUID;

import com.giamatravel.content.horse.HorseshoeItem;
import com.giamatravel.registry.ModAttachments;
import com.giamatravel.registry.ModComponents;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Right-click interactions on transport entities and with transport-related items. */
public final class ModInteractions {
	private ModInteractions() {
	}

	public static void register() {
		// Right-click a horse: equip a horseshoe, or bind a goat horn to it.
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (entity instanceof net.minecraft.world.entity.vehicle.boat.AbstractBoat boat
					&& stack.getItem() instanceof net.minecraft.world.item.BannerItem) {
				return addBoatBanner(player, boat, stack);
			}
			if (!(entity instanceof AbstractHorse horse)) {
				return InteractionResult.PASS;
			}
			if (stack.getItem() instanceof HorseshoeItem) {
				return equipHorseshoe(player, horse, stack);
			}
			if (stack.is(Items.ELYTRA)) {
				return equipElytra(player, horse, stack);
			}
			if (stack.getItem() instanceof net.minecraft.world.item.BannerItem) {
				return equipBanner(player, horse, stack);
			}
			if (stack.is(Items.GOAT_HORN)) {
				return bindGoatHorn(horse, stack);
			}
			return InteractionResult.PASS;
		});

		// Blow a bound goat horn: summon the horse.
		UseItemCallback.EVENT.register((player, level, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.is(Items.GOAT_HORN) && stack.has(ModComponents.BOUND_HORSE)
					&& !level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
				summonHorse((net.minecraft.server.level.ServerPlayer) player, stack);
			}
			// PASS so the horn still blows its note as usual.
			return InteractionResult.PASS;
		});
	}

	private static InteractionResult equipHorseshoe(Player player, AbstractHorse horse, ItemStack stack) {
		if (!horse.isTamed() || horse.isBaby()) {
			return InteractionResult.PASS;
		}
		if (horse.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		AttachmentTarget target = (AttachmentTarget) horse;
		ItemStack current = target.getAttachedOrElse(ModAttachments.HORSESHOE, ItemStack.EMPTY);
		if (!current.isEmpty()) {
			player.getInventory().placeItemBackInInventory(current.copy());
		}

		target.setAttached(ModAttachments.HORSESHOE, stack.copyWithCount(1));
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		horse.level().playSound(null, horse.blockPosition(), SoundEvents.HORSE_ARMOR.value(), SoundSource.NEUTRAL, 0.6F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult equipElytra(Player player, AbstractHorse horse, ItemStack stack) {
		if (!horse.isTamed() || horse.isBaby()) {
			return InteractionResult.PASS;
		}
		if (horse.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		AttachmentTarget target = (AttachmentTarget) horse;
		ItemStack current = target.getAttachedOrElse(ModAttachments.ELYTRA, ItemStack.EMPTY);
		if (!current.isEmpty()) {
			player.getInventory().placeItemBackInInventory(current.copy());
		}

		target.setAttached(ModAttachments.ELYTRA, stack.copyWithCount(1));
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		horse.level().playSound(null, horse.blockPosition(), SoundEvents.ARMOR_EQUIP_ELYTRA.value(), SoundSource.NEUTRAL, 0.8F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult addBoatBanner(Player player, net.minecraft.world.entity.vehicle.boat.AbstractBoat boat, ItemStack stack) {
		if (boat.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		AttachmentTarget target = (AttachmentTarget) boat;
		int capacity = target.getAttachedOrElse(ModAttachments.BOAT_SIZE, 1);
		java.util.List<ItemStack> banners = new java.util.ArrayList<>(target.getAttachedOrElse(ModAttachments.BOAT_BANNERS, java.util.List.of()));
		if (banners.size() >= capacity) {
			return InteractionResult.PASS;
		}
		banners.add(stack.copyWithCount(1));
		target.setAttached(ModAttachments.BOAT_BANNERS, banners);
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		boat.level().playSound(null, boat.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.NEUTRAL, 0.8F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult equipBanner(Player player, AbstractHorse horse, ItemStack stack) {
		if (!horse.isTamed() || horse.isBaby()) {
			return InteractionResult.PASS;
		}
		if (horse.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		AttachmentTarget target = (AttachmentTarget) horse;
		ItemStack current = target.getAttachedOrElse(ModAttachments.BANNER, ItemStack.EMPTY);
		if (!current.isEmpty()) {
			player.getInventory().placeItemBackInInventory(current.copy());
		}

		target.setAttached(ModAttachments.BANNER, stack.copyWithCount(1));
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		horse.level().playSound(null, horse.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.NEUTRAL, 0.8F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult bindGoatHorn(AbstractHorse horse, ItemStack stack) {
		if (!horse.isTamed()) {
			return InteractionResult.PASS;
		}
		if (horse.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		stack.set(ModComponents.BOUND_HORSE, horse.getUUID());
		horse.level().playSound(null, horse.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.NEUTRAL, 0.8F, 1.2F);
		return InteractionResult.SUCCESS;
	}

	private static void summonHorse(net.minecraft.server.level.ServerPlayer player, ItemStack stack) {
		UUID id = stack.get(ModComponents.BOUND_HORSE);
		if (id == null) {
			return;
		}
		ServerLevel playerLevel = player.level();
		Entity found = playerLevel.getServer().overworld().getEntityInAnyDimension(id);
		if (!(found instanceof AbstractHorse horse) || !horse.isAlive() || !horse.isTamed()) {
			return;
		}
		if (horse.isVehicle()) {
			return;
		}
		horse.stopRiding();
		horse.teleportTo(playerLevel, player.getX(), player.getY(), player.getZ(), Set.of(), player.getYRot(), 0.0F, false);
		playerLevel.playSound(null, player.blockPosition(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 0.8F, 1.0F);
	}
}
