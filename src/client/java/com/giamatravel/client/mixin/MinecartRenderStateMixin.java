package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.giamatravel.client.render.BannerCartState;

import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.item.ItemStack;

/** Carries a banner on the minecart render state for rendering. */
@Mixin(MinecartRenderState.class)
public class MinecartRenderStateMixin implements BannerCartState {
	@Unique
	private ItemStack giamatravel$banner = ItemStack.EMPTY;

	@Override
	public ItemStack giamatravel$banner() {
		return this.giamatravel$banner;
	}

	@Override
	public void giamatravel$setBanner(ItemStack banner) {
		this.giamatravel$banner = banner;
	}
}
