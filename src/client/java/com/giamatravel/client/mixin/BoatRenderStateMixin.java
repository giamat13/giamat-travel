package com.giamatravel.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.giamatravel.client.render.BannerBoatState;
import com.giamatravel.client.render.SizedBoatState;

import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.world.item.ItemStack;

/** Carries the boat size and banners on the render state for scaling and banner rendering. */
@Mixin(BoatRenderState.class)
public class BoatRenderStateMixin implements SizedBoatState, BannerBoatState {
	@Unique
	private int giamatravel$boatSize = 1;

	@Unique
	private List<ItemStack> giamatravel$banners = List.of();

	@Override
	public int giamatravel$boatSize() {
		return this.giamatravel$boatSize;
	}

	@Override
	public void giamatravel$setBoatSize(int size) {
		this.giamatravel$boatSize = size;
	}

	@Override
	public List<ItemStack> giamatravel$banners() {
		return this.giamatravel$banners;
	}

	@Override
	public void giamatravel$setBanners(List<ItemStack> banners) {
		this.giamatravel$banners = banners;
	}
}
