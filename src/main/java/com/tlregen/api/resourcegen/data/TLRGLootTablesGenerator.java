package com.tlregen.api.resourcegen.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tlregen.api.resourcegen.MasterResourceGenerator;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class TLRGLootTablesGenerator extends LootTableProvider {
	private final List<SubProviderEntry> subProviders;

	public TLRGLootTablesGenerator(List<SubProviderEntry> subProvidersIn) {
		super(MasterResourceGenerator.packOutput, Set.of(), VanillaLootTableProvider.create(MasterResourceGenerator.packOutput).getTables());
		subProviders = subProvidersIn;
	}

	@Override
	public List<LootTableProvider.SubProviderEntry> getTables() {
		return subProviders;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
	}
}
