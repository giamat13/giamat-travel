package com.giamatravel.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.giamatravel.content.minecart.FurnaceFuelAccess;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;

/**
 * Turns the furnace minecart into a blast-furnace locomotive: while fuelled it drives at full speed
 * even on plain rails (and, via coupling, drags its whole train along — see AbstractMinecartMixin).
 */
@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin implements FurnaceFuelAccess {
	@Shadow
	protected abstract boolean hasFuel();

	@Override
	public boolean giamatravel$hasFuel() {
		return this.hasFuel();
	}

	@ModifyReturnValue(method = "getMaxSpeed", at = @At("RETURN"))
	private double giamatravel$locomotiveSpeed(double original) {
		return this.hasFuel() ? Math.max(original, 4.0) : original;
	}
}
