package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

/** Exposes the protected {@code addLayer} so mixins can attach render layers to vanilla renderers. */
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererInvoker {
	@Invoker("addLayer")
	boolean giamatravel$addLayer(RenderLayer<?, ?> layer);
}
