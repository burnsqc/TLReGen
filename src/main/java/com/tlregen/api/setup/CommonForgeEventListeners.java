package com.tlregen.api.setup;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;
import com.tlregen.util.ValidationLevel;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class CommonForgeEventListeners {
	protected String modMarker;
	protected ValidationLevel validationLevel = ValidationLevel.MAX;
	private int faults;

	Supplier<List<LiteralArgumentBuilder<CommandSourceStack>>> commands;
	Supplier<List<ItemListing>> butcherTrades;
	Supplier<List<ItemListing>> farmerTrades;
	Supplier<List<ItemListing>> genericTrades;

	CommonForgeEventListeners(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	protected final void onRegisterCommandsEvent(final RegisterCommandsEvent event) {
		if (commands != null) {
			long required = commands.get().size();
			commands.get().forEach((command) -> event.getDispatcher().register(command));
			long completed = commands.get().stream().filter((command) -> event.getDispatcher().getRoot().getChild(command.build().getName()) != null).count();
			TLReGen.LOGGER.debug(MasterSetupExecutor.SETUP, modMarker + " COMMANDS REGISTERED " + completed + " OF " + required);
		}

		if (validationLevel != ValidationLevel.MIN) {
			faults = 0;
			commands.get().stream().filter((command) -> event.getDispatcher().getRoot().getChild(command.build().getName()) == null).forEach((command) -> {
				TLReGen.LOGGER.error(MasterSetupExecutor.SETUP, modMarker + " MISSING COMMAND NODE FOR " + command.build().getName());
				faults++;
			});

			if (validationLevel == ValidationLevel.MAX) {
				try {
					if (faults > 0) {
						throw new SetupException(faults + " COMMANDS ARE MISSING COMMAND NODES");
					}
				} catch (SetupException e) {
					throw new IllegalStateException("SETUP EXCEPTION", e);
				}
			}
		}
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
		TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " TRADES ADDED TO VILLAGER TRADES " + (after - before));
	}

	@SubscribeEvent
	protected final void onWandererTradesEvent(final WandererTradesEvent event) {
		int before = event.getGenericTrades().size();
		genericTrades.get().forEach((trade) -> event.getGenericTrades().add(trade));
		int after = event.getGenericTrades().size();
		TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " GENERIC TRADES ADDED TO WANDERING TRADER TRADES " + (after - before));
	}
}
