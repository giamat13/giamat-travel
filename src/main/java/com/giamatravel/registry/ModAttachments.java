package com.giamatravel.registry;

import com.giamatravel.Giamatravel;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Persistent data attached to vanilla entities (horses, boats, minecarts) without subclassing them.
 */
public final class ModAttachments {
	/** The horseshoe currently equipped on a horse. Empty when none. */
	public static final AttachmentType<ItemStack> HORSESHOE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "horseshoe"),
			builder -> builder
					.persistent(ItemStack.CODEC)
					.initializer(() -> ItemStack.EMPTY)
					.copyOnDeath());

	/** The elytra equipped on a horse (grants flight). Synced to clients so wings can be rendered. */
	public static final AttachmentType<ItemStack> ELYTRA = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "elytra"),
			builder -> builder
					.persistent(ItemStack.CODEC)
					.initializer(() -> ItemStack.EMPTY)
					.copyOnDeath()
					.syncWith(ItemStack.STREAM_CODEC, AttachmentSyncPredicate.all()));

	/** A banner draped on a horse / boat / minecart. Synced to clients for rendering. */
	public static final AttachmentType<ItemStack> BANNER = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "banner"),
			builder -> builder
					.persistent(ItemStack.CODEC)
					.initializer(() -> ItemStack.EMPTY)
					.copyOnDeath()
					.syncWith(ItemStack.STREAM_CODEC, AttachmentSyncPredicate.all()));

	/** The size (1-9) of a boat entity. Synced so the client can scale the model. */
	public static final AttachmentType<Integer> BOAT_SIZE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "boat_size"),
			builder -> builder
					.persistent(com.mojang.serialization.Codec.intRange(1, 9))
					.initializer(() -> 1)
					.syncWith(net.minecraft.network.codec.ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all()));

	/** Banners draped on a boat (more banners = faster). Synced for rendering. */
	public static final AttachmentType<java.util.List<ItemStack>> BOAT_BANNERS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "boat_banners"),
			builder -> builder
					.persistent(ItemStack.CODEC.listOf())
					.initializer(java.util.ArrayList::new)
					.copyOnDeath()
					.syncWith(ItemStack.OPTIONAL_LIST_STREAM_CODEC, AttachmentSyncPredicate.all()));

	/** The UUID of the minecart this one is chained to and follows. */
	public static final AttachmentType<java.util.UUID> COUPLED_TO = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "coupled_to"),
			builder -> builder
					.persistent(net.minecraft.core.UUIDUtil.CODEC)
					.syncWith(net.minecraft.core.UUIDUtil.STREAM_CODEC, AttachmentSyncPredicate.all()));

	/** The minecart a player has selected as the first half of a coupling (transient). */
	public static final AttachmentType<java.util.UUID> PENDING_COUPLE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, "pending_couple"));

	private ModAttachments() {
	}

	/** Touch the class so its static fields are registered. */
	public static void register() {
	}
}
