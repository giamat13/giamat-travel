package com.giamatravel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.client.render.BannerHorseState;
import com.giamatravel.client.render.ElytraHorseState;
import com.giamatravel.client.render.HorseBannerLayer;
import com.giamatravel.client.render.HorseElytraLayer;
import com.giamatravel.registry.ModAttachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(HorseRenderer.class)
public abstract class HorseRendererMixin {
	@SuppressWarnings("unchecked")
	@Inject(method = "<init>", at = @At("TAIL"))
	private void giamatravel$addLayers(EntityRendererProvider.Context context, CallbackInfo ci) {
		LivingEntityRendererInvoker self = (LivingEntityRendererInvoker) this;
		RenderLayerParent<HorseRenderState, HorseModel> parent =
				(RenderLayerParent<HorseRenderState, HorseModel>) (Object) this;
		self.giamatravel$addLayer(new HorseElytraLayer(parent, context.bakeLayer(ModelLayers.ELYTRA)));
		self.giamatravel$addLayer(new HorseBannerLayer(
				parent, context.bakeLayer(ModelLayers.STANDING_BANNER_FLAG), context.getSprites()));
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void giamatravel$extractEquip(Horse entity, HorseRenderState state, float partialTicks, CallbackInfo ci) {
		AttachmentTarget data = (AttachmentTarget) entity;
		((ElytraHorseState) state).giamatravel$setHasElytra(
				data.getAttachedOrElse(ModAttachments.ELYTRA, ItemStack.EMPTY).is(Items.ELYTRA));
		((BannerHorseState) state).giamatravel$setBanner(
				data.getAttachedOrElse(ModAttachments.BANNER, ItemStack.EMPTY).copy());
	}
}
