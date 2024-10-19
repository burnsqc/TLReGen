package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tlregen.util.TextUtil;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class MasterSetupExecutor {
	protected String modMarker;
	protected static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	protected static final Marker SETUP = MarkerManager.getMarker("SETUP");

	private CommonForgeEventListeners commonForgeEventListeners;
	private ClientModEventListeners clientModEventListeners;
	private CommonModEventListeners commonModEventListeners;

	public MasterSetupExecutor(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";

		commonForgeEventListeners = new CommonForgeEventListeners(modid);
		commonModEventListeners = new CommonModEventListeners(modid);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners = new ClientModEventListeners(modid);
		}

		LOGGER.info(SETUP, modMarker + " NEW MASTER SETUP EXECUTOR CONSTRUCTED");
	}

	public void addEntityAttributes(Supplier<Map<EntityType<? extends LivingEntity>, AttributeSupplier>> entityAttributes) {
		commonModEventListeners.entityAttributes = entityAttributes;
	}

	public void addCapabilities(List<Class<?>> capabilities) {
		commonModEventListeners.capabilities = capabilities;
	}

	public void addCommands(Supplier<List<LiteralArgumentBuilder<CommandSourceStack>>> commands) {
		commonForgeEventListeners.commands = commands;
	}

	public void addToCompostables(Supplier<Map<Item, Float>> compostables) {
		commonModEventListeners.compostables = compostables;
	}

	public void addToFlowerPot(Supplier<Map<Block, Block>> plants) {
		commonModEventListeners.plants = plants;
	}

	public void addToVillagerWantedItems(Supplier<Set<Item>> wantedItems) {
		commonModEventListeners.wantedItems = wantedItems;
	}

	public void addToVillageButcherTrades(Supplier<List<ItemListing>> butcherTrades) {
		commonForgeEventListeners.butcherTrades = butcherTrades;
	}

	public void addToVillageFarmerTrades(Supplier<List<ItemListing>> farmerTrades) {
		commonForgeEventListeners.farmerTrades = farmerTrades;
	}

	public void addToWanderingTraderGenericTrades(Supplier<List<ItemListing>> genericTrades) {
		commonForgeEventListeners.genericTrades = genericTrades;
	}

	public void addToSkulls(Supplier<Map<SkullBlock.Type, ModelLayerLocation>> skullRenderers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.skullRenderers = skullRenderers;
		}
	}
}
