package com.giamatravel.registry;

import java.util.UUID;

import com.giamatravel.Giamatravel;

import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/** Custom data components attached to item stacks. */
public final class ModComponents {
	/** The UUID of the horse a goat horn is bound to (set by right-clicking a tamed horse). */
	public static final DataComponentType<UUID> BOUND_HORSE = register(
			"bound_horse",
			DataComponentType.<UUID>builder()
					.persistent(UUIDUtil.CODEC)
					.networkSynchronized(UUIDUtil.STREAM_CODEC)
					.build());

	private ModComponents() {
	}

	public static void register() {
		// Static init registers the components above.
	}

	private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Giamatravel.MOD_ID, name), type);
	}
}
