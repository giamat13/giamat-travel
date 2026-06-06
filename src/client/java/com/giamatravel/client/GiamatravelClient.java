package com.giamatravel.client;

import com.giamatravel.registry.ModItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class GiamatravelClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register(output -> ModItems.creativeItems().forEach(output::accept));
	}
}
