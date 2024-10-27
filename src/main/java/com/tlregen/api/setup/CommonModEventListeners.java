package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;
import com.tlregen.util.ValidationLevel;

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

class CommonModEventListeners {
	private String modID;
	private String modMarker;
	protected ValidationLevel validationLevel = ValidationLevel.MAX;
	private int faults;

	Supplier<Map<EntityType<? extends LivingEntity>, AttributeSupplier>> entityAttributes;
	List<Class<?>> capabilities;
	Supplier<Map<Item, Float>> compostables;
	Supplier<Map<Block, Block>> plants;
	Supplier<Set<Item>> wantedItems;

	CommonModEventListeners(String modID) {
		this.modID = modID;
		this.modMarker = "(" + TextUtil.stringToAllCapsName(modID) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	protected final void onEntityAttributeCreationEvent(final EntityAttributeCreationEvent event) {
		if (entityAttributes != null) {
			long required = ForgeRegistries.ENTITY_TYPES.getEntries().stream().filter((entityType) -> entityType.getValue().getCategory() != MobCategory.MISC).filter((entityType) -> entityType.getKey().location().getNamespace() == modID).count();
			entityAttributes.get().forEach((entityType, attributeSupplier) -> event.put(entityType, attributeSupplier));
			long completed = ForgeRegistries.ENTITY_TYPES.getEntries().stream().filter((entityType) -> entityType.getValue().getCategory() != MobCategory.MISC).filter((entityType) -> entityType.getKey().location().getNamespace() == modID).filter((entityType) -> DefaultAttributes.hasSupplier(entityType.getValue())).count();
			TLReGen.LOGGER.debug(MasterSetupExecutor.SETUP, modMarker + " ENTITY ATTRIBUTES CREATED " + completed + " OF " + required);
		}

		if (validationLevel != ValidationLevel.MIN) {
			faults = 0;
			ForgeRegistries.ENTITY_TYPES.getEntries().stream().filter((entityType) -> entityType.getValue().getCategory() != MobCategory.MISC).filter((entityType) -> entityType.getKey().location().getNamespace() == modID).filter((entityType) -> !DefaultAttributes.hasSupplier(entityType.getValue())).forEach((entityType) -> {
				TLReGen.LOGGER.error(MasterSetupExecutor.SETUP, modMarker + " MISSING ENTITY ATTRIBUTES FOR " + entityType.getKey().location());
				faults++;
			});

			if (validationLevel == ValidationLevel.MAX) {
				try {
					if (faults > 0) {
						throw new SetupException(faults + " ENTITIES ARE MISSING ATTRIBUTES");
					}
				} catch (SetupException e) {
					throw new IllegalStateException("SETUP EXCEPTION", e);
				}
			}
		}
	}

	@SubscribeEvent
	protected final void onRegisterCapabilitiesEvent(final RegisterCapabilitiesEvent event) {
		if (capabilities != null) {
			int required = capabilities.size();
			capabilities.forEach((capability) -> event.register(capability));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " CAPABILITIES REGISTERED " + required);
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	protected final void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			int before = ComposterBlock.COMPOSTABLES.size();
			compostables.get().forEach((k, v) -> ComposterBlock.COMPOSTABLES.put(() -> k.asItem(), v));
			int after = ComposterBlock.COMPOSTABLES.size();
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " COMPOSTABLES ADDED TO COMPOSTER BLOCK " + (after - before));

			int before2 = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().size();
			plants.get().forEach((k, v) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ForgeRegistries.BLOCKS.getKey(k), () -> v));
			int after2 = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().size();
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " PLANTS ADDED TO FLOWER POT BLOCK " + (after2 - before2));

			int before3 = Villager.WANTED_ITEMS.size();
			Villager.WANTED_ITEMS = Sets.union(Villager.WANTED_ITEMS, wantedItems.get());
			int after3 = Villager.WANTED_ITEMS.size();
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " WANTED ITEMS ADDED TO VILLAGER ENTITY " + (after3 - before3));
		});
	}
}
