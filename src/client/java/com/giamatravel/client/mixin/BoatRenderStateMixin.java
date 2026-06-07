package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.giamatravel.client.render.SizedBoatState;

import net.minecraft.client.renderer.entity.state.BoatRenderState;

/** Carries the boat size on the render state so the renderer can scale the model. */
@Mixin(BoatRenderState.class)
public class BoatRenderStateMixin implements SizedBoatState {
	@Unique
	private int giamatravel$boatSize = 1;

	@Override
	public int giamatravel$boatSize() {
		return this.giamatravel$boatSize;
	}

	@Override
	public void giamatravel$setBoatSize(int size) {
		this.giamatravel$boatSize = size;
	}
}
