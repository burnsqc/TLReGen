package com.tlregen.api.resourcegen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TLReGenRegistrySetBuilder {
	private final List<TLReGenRegistrySetBuilder.RegistryStub<?>> entries = new ArrayList<>();

	static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> p_255625_) {
		return new TLReGenRegistrySetBuilder.EmptyTagLookup<T>(p_255625_) {
			@Override
			public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255765_) {
				return p_255625_.get(p_255765_);
			}
		};
	}

	public <T> TLReGenRegistrySetBuilder add(ResourceKey<? extends Registry<T>> registry, TLReGenRegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
		this.entries.add(new TLReGenRegistrySetBuilder.RegistryStub<>(registry, Lifecycle.stable(), bootstrap));
		return this;
	}

	public List<? extends ResourceKey<? extends Registry<?>>> getEntryKeys() {
		return this.entries.stream().map(TLReGenRegistrySetBuilder.RegistryStub::key).toList();
	}

	private TLReGenRegistrySetBuilder.BuildState createState(RegistryAccess p_256400_) {
		TLReGenRegistrySetBuilder.BuildState registrysetbuilder$buildstate = TLReGenRegistrySetBuilder.BuildState.create(p_256400_, this.entries.stream().map(TLReGenRegistrySetBuilder.RegistryStub::key));
		this.entries.forEach((p_255629_) -> {
			p_255629_.apply(registrysetbuilder$buildstate);
		});
		return registrysetbuilder$buildstate;
	}

	public HolderLookup.Provider build(RegistryAccess p_256112_) {
		TLReGenRegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(p_256112_);
		Stream<HolderLookup.RegistryLookup<?>> stream = p_256112_.registries().map((p_258195_) -> {
			return p_258195_.value().asLookup();
		});
		Stream<HolderLookup.RegistryLookup<?>> stream1 = this.entries.stream().map((p_255700_) -> {
			return p_255700_.collectChanges(registrysetbuilder$buildstate).buildAsLookup();
		});
		HolderLookup.Provider holderlookup$provider = HolderLookup.Provider.create(Stream.concat(stream, stream1.peek(registrysetbuilder$buildstate::addOwner)));
		registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
		registrysetbuilder$buildstate.throwOnError();
		return holderlookup$provider;
	}

	public HolderLookup.Provider buildPatch(RegistryAccess registryAccess, HolderLookup.Provider provider) {
		TLReGenRegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(registryAccess);
		Map<ResourceKey<? extends Registry<?>>, TLReGenRegistrySetBuilder.RegistryContents<?>> map = new HashMap<>();
		registrysetbuilder$buildstate.collectReferencedRegistries().forEach((p_272339_) -> {
			map.put(p_272339_.key, p_272339_);
		});
		this.entries.stream().map((p_272337_) -> {
			return p_272337_.collectChanges(registrysetbuilder$buildstate);
		}).forEach((p_272341_) -> {
			map.put(p_272341_.key, p_272341_);
		});
		Stream<HolderLookup.RegistryLookup<?>> stream = registryAccess.registries().map((p_258194_) -> {
			return p_258194_.value().asLookup();
		});
		HolderLookup.Provider holderlookup$provider = HolderLookup.Provider.create(Stream.concat(stream, map.values().stream().map(TLReGenRegistrySetBuilder.RegistryContents::buildAsLookup).peek(registrysetbuilder$buildstate::addOwner)));
		registrysetbuilder$buildstate.fillMissingHolders(provider);
		// registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
		registrysetbuilder$buildstate.throwOnError();
		return holderlookup$provider;
	}

	public static record BuildState(TLReGenRegistrySetBuilder.CompositeOwner owner, TLReGenRegistrySetBuilder.UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, TLReGenRegistrySetBuilder.RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
		public static TLReGenRegistrySetBuilder.BuildState create(RegistryAccess p_255995_, Stream<ResourceKey<? extends Registry<?>>> p_256495_) {
			TLReGenRegistrySetBuilder.CompositeOwner registrysetbuilder$compositeowner = new TLReGenRegistrySetBuilder.CompositeOwner();
			List<RuntimeException> list = new ArrayList<>();
			TLReGenRegistrySetBuilder.UniversalLookup registrysetbuilder$universallookup = new TLReGenRegistrySetBuilder.UniversalLookup(registrysetbuilder$compositeowner);
			ImmutableMap.Builder<ResourceLocation, HolderGetter<?>> builder = ImmutableMap.builder();
			p_255995_.registries().forEach((p_258197_) -> {
				builder.put(p_258197_.key().location(), net.minecraftforge.common.ForgeHooks.wrapRegistryLookup(p_258197_.value().asLookup()));
			});
			p_256495_.forEach((p_256603_) -> {
				builder.put(p_256603_.location(), registrysetbuilder$universallookup);
			});
			return new TLReGenRegistrySetBuilder.BuildState(registrysetbuilder$compositeowner, registrysetbuilder$universallookup, builder.build(), new HashMap<>(), list);
		}

		public <T> BootstapContext<T> bootstapContext() {
			return new BootstapContext<T>() {
				@Override
				public Holder.Reference<T> register(ResourceKey<T> p_256176_, T p_256422_, Lifecycle p_255924_) {
					TLReGenRegistrySetBuilder.RegisteredValue<?> registeredvalue = BuildState.this.registeredValues.put(p_256176_, new TLReGenRegistrySetBuilder.RegisteredValue(p_256422_, p_255924_));
					if (registeredvalue != null) {
						BuildState.this.errors.add(new IllegalStateException("Duplicate registration for " + p_256176_ + ", new=" + p_256422_ + ", old=" + registeredvalue.value));
					}

					return BuildState.this.lookup.getOrCreate(p_256176_);
				}

				@Override
				public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> p_255961_) {
					return (HolderGetter<S>) BuildState.this.registries.getOrDefault(p_255961_.location(), BuildState.this.lookup);
				}

				@Override
				public <S> Optional<HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry) {
					return Optional.ofNullable((HolderLookup.RegistryLookup<S>) BuildState.this.registries.get(registry.location()));
				}
			};
		}

		public void reportRemainingUnreferencedValues() {
			for (ResourceKey<Object> resourcekey : this.lookup.holders.keySet()) {
				this.errors.add(new IllegalStateException("Unreferenced key: " + resourcekey));
			}

			this.registeredValues.forEach((p_256143_, p_256662_) -> {
				this.errors.add(new IllegalStateException("Orpaned value " + p_256662_.value + " for key " + p_256143_));
			});
		}

		public void throwOnError() {
			if (!this.errors.isEmpty()) {
				IllegalStateException illegalstateexception = new IllegalStateException("Errors during registry creation");

				for (RuntimeException runtimeexception : this.errors) {
					illegalstateexception.addSuppressed(runtimeexception);
				}

				throw illegalstateexception;
			}
		}

		public void addOwner(HolderOwner<?> p_256407_) {
			this.owner.add(p_256407_);
		}

		public void fillMissingHolders(HolderLookup.Provider provider) {
			Map<ResourceLocation, Optional<? extends HolderLookup<Object>>> map = new HashMap<>();
			Iterator<Map.Entry<ResourceKey<Object>, Holder.Reference<Object>>> iterator = this.lookup.holders.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<ResourceKey<Object>, Holder.Reference<Object>> entry = iterator.next();
				ResourceKey<Object> resourcekey = entry.getKey();
				Holder.Reference<Object> reference = entry.getValue();
				map.computeIfAbsent(resourcekey.registry(), (resourceLocation) -> {
					return provider.lookup(ResourceKey.createRegistryKey(resourceLocation));
				}).flatMap((p_256068_) -> {
					return p_256068_.get(resourcekey);
				}).ifPresent((p_256030_) -> {
					reference.bindValue(p_256030_.value());
					iterator.remove();
				});
			}

		}

		public Stream<TLReGenRegistrySetBuilder.RegistryContents<?>> collectReferencedRegistries() {
			return this.lookup.holders.keySet().stream().map(ResourceKey::registry).distinct().map((p_272342_) -> {
				return new TLReGenRegistrySetBuilder.RegistryContents(ResourceKey.createRegistryKey(p_272342_), Lifecycle.stable(), Map.of());
			});
		}
	}

	public static class CompositeOwner implements HolderOwner<Object> {
		private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

		@Override
		public boolean canSerializeIn(HolderOwner<Object> p_256333_) {
			return this.owners.contains(p_256333_);
		}

		public void add(HolderOwner<?> p_256361_) {
			this.owners.add(p_256361_);
		}
	}

	abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
		protected final HolderOwner<T> owner;

		protected EmptyTagLookup(HolderOwner<T> p_256166_) {
			this.owner = p_256166_;
		}

		@Override
		public Optional<HolderSet.Named<T>> get(TagKey<T> p_256664_) {
			return Optional.of(HolderSet.emptyNamed(this.owner, p_256664_));
		}
	}

	static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
	}

	@FunctionalInterface
	public interface RegistryBootstrap<T> {
		void run(BootstapContext<T> p_255783_);
	}

	static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, TLReGenRegistrySetBuilder.ValueAndHolder<T>> values) {
		public HolderLookup.RegistryLookup<T> buildAsLookup() {
			return new HolderLookup.RegistryLookup<T>() {
				private final Map<ResourceKey<T>, Holder.Reference<T>> entries = RegistryContents.this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (p_256193_) -> {
					TLReGenRegistrySetBuilder.ValueAndHolder<T> valueandholder = p_256193_.getValue();
					Holder.Reference<T> reference = valueandholder.holder().orElseGet(() -> {
						return Holder.Reference.createStandAlone(this, p_256193_.getKey());
					});
					reference.bindValue(valueandholder.value().value());
					return reference;
				}));

				@Override
				public ResourceKey<? extends Registry<? extends T>> key() {
					return RegistryContents.this.key;
				}

				@Override
				public Lifecycle registryLifecycle() {
					return RegistryContents.this.lifecycle;
				}

				@Override
				public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255760_) {
					return Optional.ofNullable(this.entries.get(p_255760_));
				}

				@Override
				public Stream<Holder.Reference<T>> listElements() {
					return this.entries.values().stream();
				}

				@Override
				public Optional<HolderSet.Named<T>> get(TagKey<T> p_255810_) {
					return Optional.empty();
				}

				@Override
				public Stream<HolderSet.Named<T>> listTags() {
					return Stream.empty();
				}
			};
		}
	}

	static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, TLReGenRegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
		void apply(TLReGenRegistrySetBuilder.BuildState p_256272_) {
			this.bootstrap.run(p_256272_.bootstapContext());
		}

		public TLReGenRegistrySetBuilder.RegistryContents<T> collectChanges(TLReGenRegistrySetBuilder.BuildState p_256416_) {
			Map<ResourceKey<T>, TLReGenRegistrySetBuilder.ValueAndHolder<T>> map = new HashMap<>();
			Iterator<Map.Entry<ResourceKey<?>, TLReGenRegistrySetBuilder.RegisteredValue<?>>> iterator = p_256416_.registeredValues.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<ResourceKey<?>, TLReGenRegistrySetBuilder.RegisteredValue<?>> entry = iterator.next();
				ResourceKey<?> resourcekey = entry.getKey();
				if (resourcekey.isFor(this.key)) {
					TLReGenRegistrySetBuilder.RegisteredValue<T> registeredvalue = (TLReGenRegistrySetBuilder.RegisteredValue<T>) entry.getValue();
					Holder.Reference<T> reference = (Holder.Reference<T>) p_256416_.lookup.holders.remove(resourcekey);
					map.put((ResourceKey<T>) resourcekey, new TLReGenRegistrySetBuilder.ValueAndHolder<>(registeredvalue, Optional.ofNullable(reference)));
					iterator.remove();
				}
			}

			return new TLReGenRegistrySetBuilder.RegistryContents<>(this.key, this.lifecycle, map);
		}
	}

	public static class UniversalLookup extends TLReGenRegistrySetBuilder.EmptyTagLookup<Object> {
		final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap<>();

		public UniversalLookup(HolderOwner<Object> p_256629_) {
			super(p_256629_);
		}

		@Override
		public Optional<Holder.Reference<Object>> get(ResourceKey<Object> p_256303_) {
			return Optional.of(this.getOrCreate(p_256303_));
		}

		public <T> Holder.Reference<T> getOrCreate(ResourceKey<T> p_256298_) {
			return (Holder.Reference<T>) this.holders.computeIfAbsent((ResourceKey<Object>) p_256298_, (p_256154_) -> {
				return Holder.Reference.createStandAlone(this.owner, p_256154_);
			});
		}
	}

	static record ValueAndHolder<T>(TLReGenRegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
	}
}
