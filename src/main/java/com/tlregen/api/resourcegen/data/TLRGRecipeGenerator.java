package com.tlregen.api.resourcegen.data;

import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

import net.minecraft.data.recipes.RecipeProvider;

public abstract class TLRGRecipeGenerator extends RecipeProvider {
	public TLRGRecipeGenerator() {
		super(TLReGenMasterResourceGenerator.packOutput);
	}
}
