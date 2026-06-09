package com.giamatravel.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.client.render.BannerBoatState;
import com.giamatravel.client.render.SizedBoatState;
import com.giamatravel.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

@Mixin(AbstractBoatRenderer.class)
public abstract class AbstractBoatRendererMixin {
	@Unique
	private BannerFlagModel giamatravel$flagModel;

	@Unique
	private SpriteGetter giamatravel$sprites;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void giamatravel$initBanner(EntityRendererProvider.Context context, Identifier texture, CallbackInfo ci) {
		this.giamatravel$flagModel = new BannerFlagModel(context.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
		this.giamatravel$sprites = context.getSprites();
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void giamatravel$extractState(AbstractBoat entity, BoatRenderState state, float partialTicks, CallbackInfo ci) {
		AttachmentTarget data = (AttachmentTarget) entity;
		((SizedBoatState) state).giamatravel$setBoatSize(data.getAttachedOrElse(ModAttachments.BOAT_SIZE, 1));
		List<ItemStack> banners = data.getAttached(ModAttachments.BOAT_BANNERS);
		((BannerBoatState) state).giamatravel$setBanners(banners == null ? List.of() : List.copyOf(banners));
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

	@Inject(
			method = "submit",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/AbstractBoatRenderer;submitTypeAdditions(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", shift = At.Shift.AFTER))
	private void giamatravel$drawBanners(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera, CallbackInfo ci) {
		List<ItemStack> banners = ((BannerBoatState) state).giamatravel$banners();
		if (banners.isEmpty()) {
			return;
		}
		float phase = state.ageInTicks * 0.01F;
		int count = banners.size();
		for (int i = 0; i < count; i++) {
			ItemStack banner = banners.get(i);
			if (!(banner.getItem() instanceof BannerItem bannerItem)) {
				continue;
			}
			BannerPatternLayers patterns = banner.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
			poseStack.pushPose();
			// Stand banners along the boat's length. Eyeballed; tune translate/scale/rotation if needed.
			poseStack.translate(0.55F, 0.55F, (i - (count - 1) / 2.0F) * 0.45F);
			poseStack.scale(0.4F, 0.4F, 0.4F);
			this.giamatravel$flagModel.setupAnim(phase);
			BannerRenderer.submitPatterns(this.giamatravel$sprites, poseStack, submitNodeCollector, state.lightCoords,
					OverlayTexture.NO_OVERLAY, this.giamatravel$flagModel, phase, true, bannerItem.getColor(), patterns, null);
			poseStack.popPose();
		}
	}
}
