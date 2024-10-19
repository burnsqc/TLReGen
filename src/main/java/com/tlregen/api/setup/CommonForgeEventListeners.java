package com.tlregen.api.setup;

import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tlregen.util.TextUtil;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonForgeEventListeners {
	protected String modMarker;
	protected static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	protected static final Marker SETUP = MarkerManager.getMarker("SETUP");

	Supplier<List<LiteralArgumentBuilder<CommandSourceStack>>> commands;
	Supplier<List<ItemListing>> butcherTrades;
	Supplier<List<ItemListing>> farmerTrades;
	Supplier<List<ItemListing>> genericTrades;

	CommonForgeEventListeners(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public final void onRegisterCommandsEvent(final RegisterCommandsEvent event) {
		int before = event.getDispatcher().getRoot().getChildren().size();
		commands.get().forEach((command) -> event.getDispatcher().register(command));
		int after = event.getDispatcher().getRoot().getChildren().size();
		LOGGER.info(SETUP, modMarker + " COMMANDS REGISTERED " + (after - before));
	}

	@SubscribeEvent
	protected final void onVillagerTradesEvent(final VillagerTradesEvent event) {
		int before = event.getTrades().size();
		if (event.getType() == VillagerProfession.BUTCHER) {
			butcherTrades.get().forEach((trade) -> event.getTrades().get(5).add(trade));
		} else if (event.getType() == VillagerProfession.FARMER) {
			farmerTrades.get().forEach((trade) -> event.getTrades().get(1).add(trade));
		}
		int after = event.getTrades().size();
		LOGGER.info(SETUP, modMarker + " TRADES ADDED TO VILLAGER TRADES " + (after - before));
	}

	@SubscribeEvent
	protected final void onWandererTradesEvent(final WandererTradesEvent event) {
		int before = event.getGenericTrades().size();
		genericTrades.get().forEach((trade) -> event.getGenericTrades().add(trade));
		int after = event.getGenericTrades().size();
		LOGGER.info(SETUP, modMarker + " GENERIC TRADES ADDED TO WANDERING TRADER TRADES " + (after - before));
	}
}
