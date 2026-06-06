package com.giamatravel.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * A pair of elytra wings posed for a horse's back. Reuses the vanilla elytra mesh
 * ({@code ModelLayers.ELYTRA}); the wings spread open while the horse is ridden (flying).
 */
public class HorseElytraModel extends EntityModel<HorseRenderState> {
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public HorseElytraModel(ModelPart root) {
		super(root, RenderTypes::armorCutoutNoCull);
		this.leftWing = root.getChild("left_wing");
		this.rightWing = root.getChild("right_wing");
	}

	@Override
	public void setupAnim(HorseRenderState state) {
		super.setupAnim(state);
		// Spread the wings while flying (ridden), fold them while standing.
		float spread = state.isRidden ? 1.05F : 0.2F;
		this.leftWing.xRot = 0.26F;
		this.leftWing.yRot = 0.0F;
		this.leftWing.zRot = -spread;
		this.rightWing.xRot = 0.26F;
		this.rightWing.yRot = 0.0F;
		this.rightWing.zRot = spread;
	}
}
