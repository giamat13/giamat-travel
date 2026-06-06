package com.giamatravel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/** Draws a banner draped over a horse's back when one is equipped. */
public class HorseBannerLayer extends RenderLayer<HorseRenderState, HorseModel> {
	private final BannerFlagModel flagModel;
	private final SpriteGetter sprites;

	public HorseBannerLayer(RenderLayerParent<HorseRenderState, HorseModel> renderer, ModelPart flagRoot, SpriteGetter sprites) {
		super(renderer);
		this.flagModel = new BannerFlagModel(flagRoot);
		this.sprites = sprites;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
			HorseRenderState state, float yRot, float xRot) {
		if (state.isInvisible || !(state instanceof BannerHorseState holder)) {
			return;
		}
		ItemStack banner = holder.giamatravel$banner();
		if (!(banner.getItem() instanceof BannerItem bannerItem)) {
			return;
		}

		BannerPatternLayers patterns = banner.getOrDefault(
				net.minecraft.core.component.DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
		float phase = state.ageInTicks * 0.01F;

		poseStack.pushPose();
		// Lay the banner along the horse's back. Tuned by eye; adjust as needed.
		poseStack.translate(0.0F, -1.0F, 0.55F);
		poseStack.scale(0.45F, 0.45F, 0.45F);
		this.flagModel.setupAnim(phase);
		BannerRenderer.submitPatterns(
				this.sprites,
				poseStack,
				submitNodeCollector,
				lightCoords,
				OverlayTexture.NO_OVERLAY,
				this.flagModel,
				phase,
				true,
				bannerItem.getColor(),
				patterns,
				null);
		poseStack.popPose();
	}
}
