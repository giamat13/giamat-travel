package com.giamatravel.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.giamatravel.content.horse.HorseshoeItem;
import com.giamatravel.content.horse.HorseshoeTier;
import com.giamatravel.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
	@Shadow
	protected float playerJumpPendingScale;

	private static final Identifier SOUL_SPEED_MODIFIER_ID =
			Identifier.fromNamespaceAndPath("giamatravel", "horseshoe_soul_speed");

	/** Horses are generally faster while ridden. */
	@ModifyReturnValue(method = "getRiddenSpeed", at = @At("RETURN"))
	private float giamatravel$fasterHorse(float original) {
		return original * 1.3F;
	}

	/** Feather Falling on the equipped horseshoe reduces the horse's (and riders') fall damage. */
	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float giamatravel$featherFalling(float damageModifier) {
		AbstractHorse self = (AbstractHorse) (Object) this;
		ItemStack shoe = ((AttachmentTarget) self).getAttachedOrElse(ModAttachments.HORSESHOE, ItemStack.EMPTY);
		int level = giamatravel$enchantLevel(self, shoe, Enchantments.FEATHER_FALLING);
		if (level <= 0) {
			return damageModifier;
		}
		return damageModifier * Math.max(0.0F, 1.0F - 0.18F * level);
	}

	/** Per-tick server logic: horseshoe attributes, trample, boot-enchant effects, elytra glide. */
	@Inject(method = "tick", at = @At("TAIL"))
	private void giamatravel$horseTick(CallbackInfo ci) {
		AbstractHorse self = (AbstractHorse) (Object) this;
		if (self.level().isClientSide()) {
			return;
		}

		AttachmentTarget data = (AttachmentTarget) self;
		ItemStack shoe = data.getAttachedOrElse(ModAttachments.HORSESHOE, ItemStack.EMPTY);
		HorseshoeTier tier = shoe.getItem() instanceof HorseshoeItem item ? item.getTier() : null;

		reconcile(self.getAttribute(Attributes.MOVEMENT_SPEED), HorseshoeTier.SPEED_MODIFIER_ID,
				tier == null ? null : tier.speedModifier());
		reconcile(self.getAttribute(Attributes.JUMP_STRENGTH), HorseshoeTier.JUMP_MODIFIER_ID,
				tier == null ? null : tier.jumpModifier());

		giamatravel$soulSpeed(self, shoe);

		if (tier != null) {
			int frost = giamatravel$enchantLevel(self, shoe, Enchantments.FROST_WALKER);
			if (frost > 0) {
				giamatravel$frostWalk(self, frost);
			}
			if (self.isVehicle()) {
				giamatravel$trample(self, tier);
			}
		}

		giamatravel$elytraGlide(self, data);
	}

	/** Elytra double-jump: pressing jump while airborne on an elytra-horse boosts you upward. */
	@Inject(method = "tickRidden", at = @At("TAIL"))
	private void giamatravel$elytraAirJump(Player controller, Vec3 riddenInput, CallbackInfo ci) {
		AbstractHorse self = (AbstractHorse) (Object) this;
		if (self.level().isClientSide() || self.onGround() || this.playerJumpPendingScale <= 0.0F) {
			return;
		}
		ItemStack elytra = ((AttachmentTarget) self).getAttachedOrElse(ModAttachments.ELYTRA, ItemStack.EMPTY);
		if (!elytra.is(Items.ELYTRA)) {
			return;
		}

		float yaw = self.getYRot() * ((float) Math.PI / 180.0F);
		Vec3 m = self.getDeltaMovement();
		double forward = 0.32;
		self.setDeltaMovement(m.x - Mth.sin(yaw) * forward, 0.78, m.z + Mth.cos(yaw) * forward);
		this.playerJumpPendingScale = 0.0F;
		self.needsSync = true;
		self.level().playSound(null, self.blockPosition(), net.minecraft.sounds.SoundEvents.PHANTOM_FLAP,
				net.minecraft.sounds.SoundSource.NEUTRAL, 0.7F, 1.2F);
	}

	/** While ridden with an elytra and airborne, descend slowly (gliding) instead of falling. */
	private static void giamatravel$elytraGlide(AbstractHorse horse, AttachmentTarget data) {
		if (!horse.isVehicle() || horse.onGround()) {
			return;
		}
		if (!data.getAttachedOrElse(ModAttachments.ELYTRA, ItemStack.EMPTY).is(Items.ELYTRA)) {
			return;
		}
		Vec3 m = horse.getDeltaMovement();
		if (m.y < -0.10) {
			horse.setDeltaMovement(m.x, -0.10, m.z);
		}
	}

	/** Soul Speed: a boost while standing on soul sand / soul soil. */
	private static void giamatravel$soulSpeed(AbstractHorse horse, ItemStack shoe) {
		AttributeInstance speed = horse.getAttribute(Attributes.MOVEMENT_SPEED);
		int level = giamatravel$enchantLevel(horse, shoe, Enchantments.SOUL_SPEED);
		boolean active = level > 0
				&& horse.level().getBlockState(horse.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
		if (active) {
			reconcile(speed, SOUL_SPEED_MODIFIER_ID,
					new AttributeModifier(SOUL_SPEED_MODIFIER_ID, 0.45 + 0.15 * level, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		} else {
			reconcile(speed, SOUL_SPEED_MODIFIER_ID, null);
		}
	}

	/** Frost Walker: freeze nearby surface water into frosted ice. */
	private static void giamatravel$frostWalk(AbstractHorse horse, int level) {
		if (!horse.onGround()) {
			return;
		}
		ServerLevel level3 = (ServerLevel) horse.level();
		BlockState frost = Blocks.FROSTED_ICE.defaultBlockState();
		int radius = Math.min(level + 1, 4);
		BlockPos base = horse.blockPosition();
		for (BlockPos pos : BlockPos.betweenClosed(base.offset(-radius, -1, -radius), base.offset(radius, -1, radius))) {
			BlockState state = level3.getBlockState(pos);
			if (state.is(Blocks.WATER) && state.getFluidState().isSource()
					&& level3.getBlockState(pos.above()).isAir()) {
				level3.setBlockAndUpdate(pos.immutable(), frost);
			}
		}
	}

	/** A ridden, horseshoe-clad horse tramples hostile mobs it runs over, launching and hurting them. */
	private static void giamatravel$trample(AbstractHorse horse, HorseshoeTier tier) {
		ServerLevel level = (ServerLevel) horse.level();
		AABB box = horse.getBoundingBox().inflate(0.3, 0.1, 0.3);
		List<Entity> victims = level.getEntities(horse, box, e -> e instanceof Monster && e.isAlive());
		for (Entity victim : victims) {
			LivingEntity mob = (LivingEntity) victim;
			Vec3 away = mob.position().subtract(horse.position());
			away = (away.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 1.0, 0.0) : away.normalize());
			mob.push(away.x * 0.6, 0.7, away.z * 0.6);
			mob.hurtServer(level, horse.damageSources().mobAttack(horse), tier.trampleDamage());
		}
	}

	private static int giamatravel$enchantLevel(AbstractHorse horse, ItemStack stack, ResourceKey<Enchantment> key) {
		if (stack.isEmpty()) {
			return 0;
		}
		return EnchantmentHelper.getItemEnchantmentLevel(
				horse.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key), stack);
	}

	private static void reconcile(AttributeInstance attribute, Identifier id, AttributeModifier desired) {
		if (attribute == null) {
			return;
		}
		if (desired == null) {
			attribute.removeModifier(id);
		} else {
			attribute.addOrUpdateTransientModifier(desired);
		}
	}
}
