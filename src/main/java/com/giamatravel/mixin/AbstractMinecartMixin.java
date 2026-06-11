package com.giamatravel.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.content.minecart.CopperRailBlock;
import com.giamatravel.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
	/** Copper rails set the cart's top speed (when powered); vanilla powered rails act like copper @60. */
	@ModifyReturnValue(method = "getMaxSpeed", at = @At("RETURN"))
	private double giamatravel$railSpeed(double original) {
		AbstractMinecart self = (AbstractMinecart) (Object) this;
		BlockPos pos = self.getCurrentBlockPosOrRailBelow();
		BlockState state = self.level().getBlockState(pos);
		if (state.getBlock() instanceof CopperRailBlock copper) {
			return state.getValue(PoweredRailBlock.POWERED) ? copper.speedValue(state) / 20.0 : original;
		}
		if (state.is(Blocks.POWERED_RAIL) && state.getValue(PoweredRailBlock.POWERED)) {
			return 60.0 / 20.0;
		}
		// A cart coupled (directly or via the train) to a fuelled furnace minecart runs at full speed.
		if (giamatravel$coupledToLocomotive(self)) {
			return Math.max(original, 4.0);
		}
		return original;
	}

	private static boolean giamatravel$coupledToLocomotive(AbstractMinecart cart) {
		ServerLevel level = (ServerLevel) cart.level();
		AbstractMinecart current = cart;
		for (int i = 0; i < 16; i++) {
			java.util.UUID partnerId = ((AttachmentTarget) current).getAttached(ModAttachments.COUPLED_TO);
			if (partnerId == null) {
				return false;
			}
			Entity partner = level.getEntity(partnerId);
			if (!(partner instanceof AbstractMinecart partnerCart)) {
				return false;
			}
			if (partner instanceof com.giamatravel.content.minecart.FurnaceFuelAccess fuel && fuel.giamatravel$hasFuel()) {
				return true;
			}
			current = partnerCart;
		}
		return false;
	}

	/** Pull this cart toward the one it's coupled to, so chained carts trail each other. */
	@Inject(method = "tick", at = @At("TAIL"))
	private void giamatravel$coupling(CallbackInfo ci) {
		AbstractMinecart self = (AbstractMinecart) (Object) this;
		if (self.level().isClientSide()) {
			return;
		}
		AttachmentTarget data = (AttachmentTarget) self;
		UUID partnerId = data.getAttached(ModAttachments.COUPLED_TO);
		if (partnerId == null) {
			return;
		}

		Entity partner = ((ServerLevel) self.level()).getEntity(partnerId);
		if (!(partner instanceof AbstractMinecart) || !partner.isAlive()) {
			data.removeAttached(ModAttachments.COUPLED_TO);
			return;
		}

		Vec3 toPartner = partner.position().subtract(self.position());
		double distance = toPartner.length();
		if (distance > 8.0) {
			// Stretched too far — the coupling snaps.
			data.removeAttached(ModAttachments.COUPLED_TO);
			self.spawnAtLocation((ServerLevel) self.level(), com.giamatravel.compat.ChainItems.dropItem());
			return;
		}

		double desired = 1.6;
		if (distance > desired) {
			Vec3 pull = toPartner.normalize().scale(Math.min((distance - desired) * 0.5, 0.5));
			self.setDeltaMovement(self.getDeltaMovement().add(pull.x, 0.0, pull.z));
		}
	}
}
