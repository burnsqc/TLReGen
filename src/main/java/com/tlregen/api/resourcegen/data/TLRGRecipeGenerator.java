package com.tlregen.api.resourcegen.data;

import com.tlregen.api.resourcegen.MasterResourceGenerator;

import net.minecraft.data.recipes.RecipeProvider;

public abstract class TLRGRecipeGenerator extends RecipeProvider {
	public TLRGRecipeGenerator() {
		super(MasterResourceGenerator.packOutput);
	}

	@Override
	public final String getName() {
		return "data." + "modID" + ".recipes";
	}
}
