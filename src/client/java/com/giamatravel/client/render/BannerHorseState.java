package com.giamatravel.client.render;

import net.minecraft.world.item.ItemStack;

/** Implemented (via mixin) by {@code HorseRenderState} to carry the banner draped on the horse. */
public interface BannerHorseState {
	ItemStack giamatravel$banner();

	void giamatravel$setBanner(ItemStack banner);
}
