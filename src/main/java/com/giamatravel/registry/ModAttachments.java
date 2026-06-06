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

	private ModAttachments() {
	}

	/** Touch the class so its static fields are registered. */
	public static void register() {
	}
}
