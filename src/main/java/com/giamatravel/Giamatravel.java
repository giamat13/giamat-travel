package com.giamatravel;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.giamatravel.event.ModComponentTweaks;
import com.giamatravel.event.ModInteractions;
import com.giamatravel.registry.ModAttachments;
import com.giamatravel.registry.ModComponents;
import com.giamatravel.registry.ModItems;

public class Giamatravel implements ModInitializer {
	public static final String MOD_ID = "giamatravel";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModComponents.register();
		ModAttachments.register();
		ModComponentTweaks.register();
		ModInteractions.register();

		LOGGER.info("Giamatravel transport overhaul loaded.");
	}
}