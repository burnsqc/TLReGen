package com.tlregen.api.resourcegen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;

public abstract class TLReGenResourceGenerator implements DataProvider {
	protected final String modID;
	private final String type;
	private final String subType;
	protected PathProvider pathProvider;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	protected TLReGenResourceGenerator(String modID, Types type, PackOutput packOutput) {
		this.modID = modID;
		this.type = type.target.directory;
		this.subType = type.subType;
		this.pathProvider = packOutput.createPathProvider(type.target, type.path);
	}

	@Override
	public String getName() {
		return type + "." + modID + "." + subType;
	}

	public enum Types {
		ATLAS(PackOutput.Target.RESOURCE_PACK, "atlases", "atlases"), 
		BIOME(PackOutput.Target.DATA_PACK, "worldgen.biome", "worldgen/biome"), 
		BIOME_MODIFIER(PackOutput.Target.DATA_PACK, "forge.biome_modifier", "forge/biome_modifier"), 
		BLOCK_MODEL(PackOutput.Target.RESOURCE_PACK, "models.block", "models/block"), 
		BLOCKSTATE(PackOutput.Target.RESOURCE_PACK, "blockstates", "blockstates"), 
		CONFIGURED_FEATURE(PackOutput.Target.DATA_PACK, "worldgen.configured_feature", "worldgen/configured_feature"), 
		DAMAGE_TYPE(PackOutput.Target.DATA_PACK, "damage_type", "damage_type"), 
		DENSITY_FUNCTION(PackOutput.Target.DATA_PACK, "worldgen.density_function", "worldgen/density_function"), 
		DIMENSION(PackOutput.Target.DATA_PACK, "dimension", "dimension"), 
		DIMENSION_TYPE(PackOutput.Target.DATA_PACK, "dimension_type", "dimension_type"), 
		FONT(PackOutput.Target.RESOURCE_PACK, "font", "font"), 
		ITEM_MODEL(PackOutput.Target.RESOURCE_PACK, "models.item", "models/item"), 
		LANG(PackOutput.Target.RESOURCE_PACK, "lang", "lang"),
		NOISE(PackOutput.Target.DATA_PACK, "worldgen.noise", "worldgen/noise"), 
		NOISE_SETTING(PackOutput.Target.DATA_PACK, "worldgen.noise_settings", "worldgen/noise_settings"), 
		PARTICLE(PackOutput.Target.RESOURCE_PACK, "particles", "particles"), 
		PLACED_FEATURE(PackOutput.Target.DATA_PACK, "worldgen.placed_feature", "worldgen/placed_feature"), 
		SOUND(PackOutput.Target.RESOURCE_PACK, "sounds", ""),
		STRUCTURE(PackOutput.Target.DATA_PACK, "worldgen.structure", "worldgen/structure"), 
		STRUCTURE_SET(PackOutput.Target.DATA_PACK, "worldgen.structure_set", "worldgen/structure_set"), 
		STRUCTURE_TEMPLATE_POOL(PackOutput.Target.DATA_PACK, "worldgen.template_pool", "worldgen/template_pool");

		final PackOutput.Target target;
		final String subType;
		final String path;

		private Types(PackOutput.Target target, String type, String subType) {
			this.target = target;
			this.subType = type;
			this.path = subType;
		}
	}
}
