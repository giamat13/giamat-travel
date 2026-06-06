package com.giamatravel.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/** Draws elytra wings on a horse that has an elytra equipped. */
public class HorseElytraLayer extends RenderLayer<HorseRenderState, HorseModel> {
	private static final Identifier ELYTRA_TEXTURE =
			Identifier.withDefaultNamespace("textures/entity/equipment/wings/elytra.png");

	private final HorseElytraModel model;

	public HorseElytraLayer(RenderLayerParent<HorseRenderState, HorseModel> renderer, ModelPart elytraRoot) {
		super(renderer);
		this.model = new HorseElytraModel(elytraRoot);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
			HorseRenderState state, float yRot, float xRot) {
		if (state.isInvisible || !(state instanceof ElytraHorseState elytra) || !elytra.giamatravel$hasElytra()) {
			return;
		}

		poseStack.pushPose();
		// Sit the wings on the horse's back. Tuned by eye; adjust translate/scale if needed.
		poseStack.translate(0.0F, -0.55F, 0.1F);
		poseStack.scale(1.2F, 1.2F, 1.2F);
		this.model.setupAnim(state);
		submitNodeCollector.order(2).submitModel(
				this.model,
				state,
				poseStack,
				RenderTypes.armorCutoutNoCull(ELYTRA_TEXTURE),
				lightCoords,
				OverlayTexture.NO_OVERLAY,
				state.outlineColor,
				null);
		poseStack.popPose();
	}
}
