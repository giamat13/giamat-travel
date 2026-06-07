package com.giamatravel.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.giamatravel.registry.ModAttachments;
import com.giamatravel.registry.ModComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Transfers a boat item's crafted size onto the spawned boat entity. */
@Mixin(BoatItem.class)
public abstract class BoatItemMixin {
	@ModifyReturnValue(method = "getBoat", at = @At("RETURN"))
	private AbstractBoat giamatravel$applySize(AbstractBoat boat, Level level, HitResult hitResult, ItemStack itemStack, Player player) {
		if (boat != null) {
			Integer size = itemStack.get(ModComponents.BOAT_SIZE);
			if (size != null && size > 1) {
				((AttachmentTarget) boat).setAttached(ModAttachments.BOAT_SIZE, size);
			}
		}
		return boat;
	}
}
