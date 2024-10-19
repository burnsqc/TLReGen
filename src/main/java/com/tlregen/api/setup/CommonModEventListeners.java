package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Sets;
import com.tlregen.api.registration.SetupException;
import com.tlregen.util.TextUtil;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonModEventListeners {
	private String modID;
	protected String modMarker;
	protected static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	protected static final Marker SETUP = MarkerManager.getMarker("SETUP");

	Supplier<Map<EntityType<? extends LivingEntity>, AttributeSupplier>> entityAttributes;
	List<Class<?>> capabilities;
	Supplier<Map<Item, Float>> compostables;
	Supplier<Map<Block, Block>> plants;
	Supplier<Set<Item>> wantedItems;

	CommonModEventListeners(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	protected final void onEntityAttributeCreationEvent(final EntityAttributeCreationEvent event) {
		if (entityAttributes != null) {
			entityAttributes.get().forEach((k, v) -> event.put(k, v));

			try {
				long entities = ForgeRegistries.ENTITY_TYPES.getEntries().stream().filter((entityType) -> entityType.getValue().getCategory() != MobCategory.MISC).filter((entityType) -> entityType.getKey().location().getNamespace() == modID).count();
				long attributes = ForgeRegistries.ENTITY_TYPES.getEntries().stream().filter((entityType) -> entityType.getValue().getCategory() != MobCategory.MISC).filter((entityType) -> entityType.getKey().location().getNamespace() == modID).filter((entry) -> DefaultAttributes.hasSupplier(entry.getValue())).count();
				LOGGER.info(SETUP, modMarker + " ENTITY ATTRIBUTES CREATED " + attributes + " OF " + entities);

				if (attributes < entities) {
					LOGGER.error(SETUP, "ERROR - ENTITY ATTRIBUTES - MISSING " + (entities - attributes));
					throw new SetupException("ENTITY ATTRIBUTES LESS THAN REGISTERED ENTITIES");
				} else if (attributes > entities) {
					LOGGER.error(SETUP, "ERROR - ENTITY ATTRIBUTES - EXTRANEOUS " + (entities - attributes));
					throw new SetupException("ENTITY ATTRIBUTES GREATER THAN REGISTERED ENTITIES");
				}
			} catch (SetupException e) {
				throw new IllegalStateException("SETUP ERROR", e);
			}
		}
	}

	@SubscribeEvent
	protected final void onRegisterCapabilitiesEvent(final RegisterCapabilitiesEvent event) {
		if (capabilities != null) {
			capabilities.forEach((c) -> event.register(c));
			LOGGER.info(SETUP, modMarker + " CAPABILITIES REGISTERED " + capabilities.size());
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	protected final void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			int before = ComposterBlock.COMPOSTABLES.size();
			compostables.get().forEach((k, v) -> ComposterBlock.COMPOSTABLES.put(() -> k.asItem(), v));
			int after = ComposterBlock.COMPOSTABLES.size();
			LOGGER.info(SETUP, modMarker + " COMPOSTABLES ADDED TO COMPOSTER BLOCK " + (after - before));

			int before2 = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().size();
			plants.get().forEach((k, v) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ForgeRegistries.BLOCKS.getKey(k), () -> v));
			int after2 = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().size();
			LOGGER.info(SETUP, modMarker + " PLANTS ADDED TO FLOWER POT BLOCK " + (after2 - before2));

			int before3 = Villager.WANTED_ITEMS.size();
			Villager.WANTED_ITEMS = Sets.union(Villager.WANTED_ITEMS, wantedItems.get());
			int after3 = Villager.WANTED_ITEMS.size();
			LOGGER.info(SETUP, modMarker + " WANTED ITEMS ADDED TO VILLAGER ENTITY " + (after3 - before3));
		});
	}
}
