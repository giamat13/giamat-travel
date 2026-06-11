package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.client.render.BannerCartState;
import com.giamatravel.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

@Mixin(AbstractMinecartRenderer.class)
public abstract class AbstractMinecartRendererMixin {
	@Unique
	private BannerFlagModel giamatravel$flagModel;

	@Unique
	private SpriteGetter giamatravel$sprites;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void giamatravel$initBanner(EntityRendererProvider.Context context, net.minecraft.client.model.geom.ModelLayerLocation model, CallbackInfo ci) {
		this.giamatravel$flagModel = new BannerFlagModel(context.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
		this.giamatravel$sprites = context.getSprites();
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void giamatravel$extractBanner(AbstractMinecart entity, MinecartRenderState state, float partialTicks, CallbackInfo ci) {
		((BannerCartState) state).giamatravel$setBanner(
				((AttachmentTarget) entity).getAttachedOrElse(ModAttachments.BANNER, ItemStack.EMPTY).copy());
	}

	@Inject(method = "submit", at = @At("TAIL"))
	private void giamatravel$drawBanner(MinecartRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera, CallbackInfo ci) {
		ItemStack banner = ((BannerCartState) state).giamatravel$banner();
		if (!(banner.getItem() instanceof BannerItem bannerItem)) {
			return;
		}
		BannerPatternLayers patterns = banner.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
		float phase = state.ageInTicks * 0.01F;
		poseStack.pushPose();
		// Stand the banner up in the cart. Eyeballed; tune translate/scale if needed.
		poseStack.translate(0.0F, 0.55F, 0.0F);
		poseStack.scale(0.4F, 0.4F, 0.4F);
		this.giamatravel$flagModel.setupAnim(phase);
		BannerRenderer.submitPatterns(this.giamatravel$sprites, poseStack, submitNodeCollector, state.lightCoords,
				OverlayTexture.NO_OVERLAY, this.giamatravel$flagModel, phase, true, bannerItem.getColor(), patterns, null);
		poseStack.popPose();
	}
}
