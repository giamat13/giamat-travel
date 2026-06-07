package com.giamatravel.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.giamatravel.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin {
	/** Bigger boats seat more passengers (size + 1). */
	@ModifyReturnValue(method = "getMaxPassengers", at = @At("RETURN"))
	private int giamatravel$maxPassengers(int original) {
		int size = giamatravel$boatSize();
		return size > 1 ? size + 1 : original;
	}

	/** Boats are generally faster, and faster still the more banners they fly. */
	@ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.04F))
	private float giamatravel$faster(float acceleration) {
		int banners = ((AttachmentTarget) this).getAttachedOrElse(ModAttachments.BOAT_BANNERS, java.util.List.of()).size();
		return acceleration * 1.6F * (1.0F + 0.12F * banners);
	}

	/** Bigger boats turn more slowly: scale the per-tick rotation step. */
	@ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 1.0F))
	private float giamatravel$slowerTurn(float step) {
		int size = giamatravel$boatSize();
		return size > 1 ? step / (1.0F + 0.5F * (size - 1)) : step;
	}

	/** Spread passengers along the length of a bigger boat so they don't stack. */
	@ModifyReturnValue(method = "getPassengerAttachmentPoint", at = @At("RETURN"))
	private Vec3 giamatravel$seatPositions(Vec3 original, Entity passenger, EntityDimensions dimensions, float scale) {
		int size = giamatravel$boatSize();
		if (size <= 1) {
			return original;
		}
		AbstractBoat self = (AbstractBoat) (Object) this;
		List<Entity> passengers = self.getPassengers();
		int index = passengers.indexOf(passenger);
		int count = passengers.size();
		double z = (index - (count - 1) / 2.0) * 0.7;
		return new Vec3(0.0, original.y, z).yRot(-self.getYRot() * ((float) Math.PI / 180.0F));
	}

	private int giamatravel$boatSize() {
		return ((AttachmentTarget) this).getAttachedOrElse(ModAttachments.BOAT_SIZE, 1);
	}
}
