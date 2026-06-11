package com.giamatravel.client.render;

import net.minecraft.world.item.ItemStack;

/** Implemented (via mixin) by {@code MinecartRenderState} to carry a banner shown on the cart. */
public interface BannerCartState {
	ItemStack giamatravel$banner();

	void giamatravel$setBanner(ItemStack banner);
}
