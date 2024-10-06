package com.tlregen.api.resourcegen.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TLRGLootTablesGenerator extends LootTableProvider {
	private final List<SubProviderEntry> subProviders = List.of(new LootTableProvider.SubProviderEntry(null, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(null, LootContextParamSets.ENTITY));

	public TLRGLootTablesGenerator() {
		super(TLReGenMasterResourceGenerator.packOutput, Set.of(), VanillaLootTableProvider.create(TLReGenMasterResourceGenerator.packOutput).getTables());
	}

	@Override
	public List<LootTableProvider.SubProviderEntry> getTables() {
		return subProviders;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
	}
}
