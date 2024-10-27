package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;
import com.tlregen.util.ResourceLocationHelper;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TLReGenModelsItem extends TLReGenAssetProvider {
	private final Map<ResourceLocation, ItemModelBuilder> resources = new HashMap<>();
	private BiFunction<ResourceLocation, ExistingFileHelper, ItemModelBuilder> bifunc = ItemModelBuilder::new;

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		resources.clear();
		populate();
		if (resources.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			resources.forEach((key, value) -> {
				JsonObject json = value.toJson();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "models").json(key)));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".models.item";
	}

	/*
	 * HELPER METHODS
	 */

	private ItemModelBuilder getBuilder(String path) {
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modID, path));
		helper.trackGenerated(outputLoc, new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models"));
		return resources.computeIfAbsent(outputLoc, loc -> bifunc.apply(loc, helper));
	}

	private ResourceLocation extendWithFolder(ResourceLocation rl) {
		if (rl.getPath().contains("/")) {
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath());
	}

	/*
	 * KEEP
	 */
	public void item2D(Item item) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
	}

	public void item2DModel(Item item, ModelFile model) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		getBuilder(location.getPath()).parent(model).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
	}

	public void item2DModelRenderType(Block block, ModelFile model, String renderType) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		getBuilder(location.getPath()).parent(model).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath())).renderType(renderType);
	}

	/*
	 * KEEP
	 */
	public void item2DSpawnEgg(Item item) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		this.getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
	}

	/*
	 * KEEP
	 */
	public void item2DGlint(Item item) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		this.getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).guiLight(GuiLight.FRONT).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
		this.getBuilder(location.getPath() + "_base").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
	}

	public void excitedItem(Item item) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		this.getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).guiLight(GuiLight.FRONT).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
		this.getBuilder(location.getPath() + "_base").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(location.getNamespace(), "item/" + location.getPath()));
	}

	/*
	 * KEEP
	 */
	public void item3D(Item item) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
		this.getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(location.getNamespace(), "block/" + location.getPath()));
	}

	/*
	 * KEEP
	 */
	public void item3DModel(Block block, ModelFile model) {
		getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).parent(model);
	}

	/*
	 * KEEP
	 */
	public ResourceLocation blockTexture(Block block) {
		ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
		return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
	}

	public void signItem(Block block, ModelFile model, String renderType) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		getBuilder(location.getPath()).parent(model).texture("layer0", ResourceLocationHelper.replace(ResourceLocationHelper.replace(blockTexture(block), "_wall", ""), "block", "item")).renderType(renderType);
	}

	public String name(Block block) {
		return key(block).getPath();
	}

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}
}
