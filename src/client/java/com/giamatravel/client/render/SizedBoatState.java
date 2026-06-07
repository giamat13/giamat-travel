package com.giamatravel.client.render;

/** Implemented (via mixin) by {@code BoatRenderState} to carry the boat's size for scaling. */
public interface SizedBoatState {
	int giamatravel$boatSize();

	void giamatravel$setBoatSize(int size);
}
