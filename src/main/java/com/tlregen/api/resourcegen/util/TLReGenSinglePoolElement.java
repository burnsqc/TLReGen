package com.tlregen.api.resourcegen.util;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class TLReGenSinglePoolElement extends SinglePoolElement {
	public TLReGenSinglePoolElement(ResourceLocation location, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection) {
		super(Either.left(location), processors, projection);
	}
}