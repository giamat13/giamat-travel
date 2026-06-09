package com.giamatravel.client.render;

import java.util.List;

import net.minecraft.world.item.ItemStack;

/** Implemented (via mixin) by {@code BoatRenderState} to carry the boat's banners. */
public interface BannerBoatState {
	List<ItemStack> giamatravel$banners();

	void giamatravel$setBanners(List<ItemStack> banners);
}
