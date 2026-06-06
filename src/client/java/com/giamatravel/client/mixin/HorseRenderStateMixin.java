package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.giamatravel.client.render.BannerHorseState;
import com.giamatravel.client.render.ElytraHorseState;

import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.world.item.ItemStack;

/** Carries the elytra/banner equip state on the horse render state for the custom layers. */
@Mixin(HorseRenderState.class)
public class HorseRenderStateMixin implements ElytraHorseState, BannerHorseState {
	@Unique
	private boolean giamatravel$hasElytra;

	@Unique
	private ItemStack giamatravel$banner = ItemStack.EMPTY;

	@Override
	public boolean giamatravel$hasElytra() {
		return this.giamatravel$hasElytra;
	}

	@Override
	public void giamatravel$setHasElytra(boolean hasElytra) {
		this.giamatravel$hasElytra = hasElytra;
	}

	@Override
	public ItemStack giamatravel$banner() {
		return this.giamatravel$banner;
	}

	@Override
	public void giamatravel$setBanner(ItemStack banner) {
		this.giamatravel$banner = banner;
	}
}
