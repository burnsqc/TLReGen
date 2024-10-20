package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tlregen.util.TextUtil;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
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

	public void addSkullModels(Supplier<Map<SkullBlock.Type, ModelLayerLocation>> skullModels) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.skullModels = skullModels;
		}
	}

	public void addLayerDefinitions(Supplier<Map<ModelLayerLocation, LayerDefinition>> layerDefinitions) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.layerDefinitions = layerDefinitions;
		}
	}

	public void registerEntityRenderers(Supplier<Map<EntityType<? extends Entity>, EntityRendererProvider<Entity>>> entityRenderers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.entityRenderers = entityRenderers;
		}
	}

	public void registerBlockEntityRenderers(Supplier<Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<BlockEntity>>> blockEntityRenderers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.blockEntityRenderers = blockEntityRenderers;
		}
	}

	public void registerAdditionalModels(Supplier<List<ResourceLocation>> additionalModels) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.additionalModels = additionalModels;
		}
	}

	public void registerColorHandlersBlock(Supplier<Map<Integer, Block>> blockColorHandlers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.blockColorHandlers = blockColorHandlers;
		}
	}

	public void registerDimensionSpecialEffects(Supplier<Map<ResourceLocation, DimensionSpecialEffects>> dimensionSpecialEffects) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.dimensionSpecialEffects = dimensionSpecialEffects;
		}
	}

	public void registerParticleProvidersSprites(Supplier<Map<ParticleType<ParticleOptions>, ParticleProvider.Sprite<ParticleOptions>>> particleSprites) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.particleSprites = particleSprites;
		}
	}

	public void registerParticleProvidersSpriteSets(Supplier<Map<ParticleType<ParticleOptions>, ParticleEngine.SpriteParticleRegistration<ParticleOptions>>> particleSpriteSets) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.particleSpriteSets = particleSpriteSets;
		}
	}

	public void registerScreens(Supplier<Map<MenuType<? extends AbstractContainerMenu>, MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>>> screens) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.screens = screens;
		}
	}

	public void registerSkullTextures(Supplier<Map<SkullBlock.Type, ResourceLocation>> skullTextures) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.skullTextures = skullTextures;
		}
	}

	public void setFluidRenderTypes(Supplier<Map<Fluid, RenderType>> fluidRenderTypes) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.fluidRenderTypes = fluidRenderTypes;
		}
	}

	public void addRenderTypes(Supplier<SortedMap<RenderType, BufferBuilder>> renderTypes) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.renderTypes = renderTypes;
		}
	}
}
