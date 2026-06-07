package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.client.render.SizedBoatState;
import com.giamatravel.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

@Mixin(AbstractBoatRenderer.class)
public abstract class AbstractBoatRendererMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void giamatravel$extractSize(AbstractBoat entity, BoatRenderState state, float partialTicks, CallbackInfo ci) {
		((SizedBoatState) state).giamatravel$setBoatSize(
				((AttachmentTarget) entity).getAttachedOrElse(ModAttachments.BOAT_SIZE, 1));
	}

	@Inject(
			method = "submit",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER, ordinal = 0))
	private void giamatravel$scaleBySize(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera, CallbackInfo ci) {
		int size = ((SizedBoatState) state).giamatravel$boatSize();
		if (size > 1) {
			float scale = 1.0F + 0.2F * (size - 1);
			poseStack.scale(scale, scale, scale);
		}
	}
}
