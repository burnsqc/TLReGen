package com.tlregen.api.resourcegen.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.assets.TLReGenBlockstates;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;

public class TLReGenVariantBlockStateBuilder implements IGeneratedBlockState {

	private final Block owner;
	private final Map<PartialBlockstate, TLReGenBlockstates.ConfiguredModelList> models = new LinkedHashMap<>();
	private final Set<BlockState> coveredStates = new HashSet<>();

	public TLReGenVariantBlockStateBuilder(Block owner) {
		this.owner = owner;
	}

	public Map<PartialBlockstate, TLReGenBlockstates.ConfiguredModelList> getModels() {
		return models;
	}

	public Block getOwner() {
		return owner;
	}

	@Override
	public JsonObject toJson() {
		List<BlockState> missingStates = Lists.newArrayList(owner.getStateDefinition().getPossibleStates());
		missingStates.removeAll(coveredStates);
		Preconditions.checkState(missingStates.isEmpty(), "Blockstate for block %s does not cover all states. Missing: %s", owner, missingStates);
		JsonObject variants = new JsonObject();
		getModels().entrySet().stream().sorted(Entry.comparingByKey(PartialBlockstate.comparingByProperties())).forEach(entry -> variants.add(entry.getKey().toString(), entry.getValue().toJSON()));
		JsonObject main = new JsonObject();
		main.add("variants", variants);
		return main;
	}

	public TLReGenVariantBlockStateBuilder addModels(TLReGenVariantBlockStateBuilder.PartialBlockstate state, TLReGenConfiguredModel... models) {
		Preconditions.checkNotNull(state, "state must not be null");
		Preconditions.checkArgument(models.length > 0, "Cannot set models to empty array");
		Preconditions.checkArgument(state.getOwner() == owner, "Cannot set models for a different block. Found: %s, Current: %s", state.getOwner(), owner);
		if (!this.models.containsKey(state)) {
			Preconditions.checkArgument(disjointToAll(state), "Cannot set models for a state for which a partial match has already been configured");
			this.models.put(state, new TLReGenBlockstates.ConfiguredModelList(models));
			for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
				if (state.test(fullState)) {
					coveredStates.add(fullState);
				}
			}
		} else {
			this.models.compute(state, ($, cml) -> cml.append(models));
		}
		return this;
	}

	public TLReGenVariantBlockStateBuilder setModels(TLReGenVariantBlockStateBuilder.PartialBlockstate state, TLReGenConfiguredModel... model) {
		Preconditions.checkArgument(!models.containsKey(state), "Cannot set models for a state that has already been configured: %s", state);
		addModels(state, model);
		return this;
	}

	private boolean disjointToAll(TLReGenVariantBlockStateBuilder.PartialBlockstate newState) {
		return coveredStates.stream().noneMatch(newState);
	}

	public PartialBlockstate partialState() {
		return new PartialBlockstate(owner, this);
	}

	public TLReGenVariantBlockStateBuilder forAllStates(Function<BlockState, TLReGenConfiguredModel[]> mapper) {
		return forAllStatesExcept(mapper);
	}

	public TLReGenVariantBlockStateBuilder forAllStatesExcept(Function<BlockState, TLReGenConfiguredModel[]> mapper, Property<?>... ignored) {
		Set<PartialBlockstate> seen = new HashSet<>();
		for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
			Map<Property<?>, Comparable<?>> propertyValues = Maps.newLinkedHashMap(fullState.getValues());
			for (Property<?> p : ignored) {
				propertyValues.remove(p);
			}
			PartialBlockstate partialState = new PartialBlockstate(owner, propertyValues, this);
			if (seen.add(partialState)) {
				setModels(partialState, mapper.apply(fullState));
			}
		}
		return this;
	}

	public static class PartialBlockstate implements Predicate<BlockState> {
		private final Block owner;
		private final SortedMap<Property<?>, Comparable<?>> setStates;
		@Nullable
		private final TLReGenVariantBlockStateBuilder outerBuilder;

		PartialBlockstate(Block owner, @Nullable TLReGenVariantBlockStateBuilder outerBuilder) {
			this(owner, ImmutableMap.of(), outerBuilder);
		}

		PartialBlockstate(Block owner, Map<Property<?>, Comparable<?>> setStates, @Nullable TLReGenVariantBlockStateBuilder outerBuilder) {
			this.owner = owner;
			this.outerBuilder = outerBuilder;
			for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
				Property<?> prop = entry.getKey();
				Comparable<?> value = entry.getValue();
				Preconditions.checkArgument(owner.getStateDefinition().getProperties().contains(prop), "Property %s not found on block %s", entry, this.owner);
				Preconditions.checkArgument(prop.getPossibleValues().contains(value), "%s is not a valid value for %s", value, prop);
			}
			this.setStates = Maps.newTreeMap(Comparator.comparing(Property::getName));
			this.setStates.putAll(setStates);
		}

		public <T extends Comparable<T>> PartialBlockstate with(Property<T> prop, T value) {
			Preconditions.checkArgument(!setStates.containsKey(prop), "Property %s has already been set", prop);
			Map<Property<?>, Comparable<?>> newState = new HashMap<>(setStates);
			newState.put(prop, value);
			return new PartialBlockstate(owner, newState, outerBuilder);
		}

		private void checkValidOwner() {
			Preconditions.checkNotNull(outerBuilder, "Partial blockstate must have a valid owner to perform this action");
		}

		public TLReGenConfiguredModel.Builder<TLReGenVariantBlockStateBuilder> modelForState() {
			checkValidOwner();
			return TLReGenConfiguredModel.builder(outerBuilder, this);
		}

		public PartialBlockstate addModels(TLReGenConfiguredModel... models) {
			checkValidOwner();
			outerBuilder.addModels(this, models);
			return this;
		}

		public TLReGenVariantBlockStateBuilder setModels(TLReGenConfiguredModel... models) {
			checkValidOwner();
			return outerBuilder.setModels(this, models);
		}

		public PartialBlockstate partialState() {
			checkValidOwner();
			return outerBuilder.partialState();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			PartialBlockstate that = (PartialBlockstate) o;
			return owner.equals(that.owner) &&
				setStates.equals(that.setStates);
		}

		@Override
		public int hashCode() {
			return Objects.hash(owner, setStates);
		}

		public Block getOwner() {
			return owner;
		}

		public SortedMap<Property<?>, Comparable<?>> getSetStates() {
			return setStates;
		}

		@Override
		public boolean test(BlockState blockState) {
			if (blockState.getBlock() != getOwner()) {
				return false;
			}
			for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
				if (blockState.getValue(entry.getKey()) != entry.getValue()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
				if (ret.length() > 0) {
					ret.append(',');
				}
				ret.append(entry.getKey().getName()).append('=').append(((Property) entry.getKey()).getName(entry.getValue()));
			}
			return ret.toString();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Comparator<PartialBlockstate> comparingByProperties() {
			return (s1, s2) -> {
				SortedSet<Property<?>> propUniverse = new TreeSet<>(s1.getSetStates().comparator().reversed());
				propUniverse.addAll(s1.getSetStates().keySet());
				propUniverse.addAll(s2.getSetStates().keySet());
				for (Property<?> prop : propUniverse) {
					Comparable val1 = s1.getSetStates().get(prop);
					Comparable val2 = s2.getSetStates().get(prop);
					if (val1 == null && val2 != null) {
						return -1;
					} else if (val2 == null && val1 != null) {
						return 1;
					} else if (val1 != null && val2 != null) {
						int cmp = val1.compareTo(val2);
						if (cmp != 0) {
							return cmp;
						}
					}
				}
				return 0;
			};
		}
	}
}
