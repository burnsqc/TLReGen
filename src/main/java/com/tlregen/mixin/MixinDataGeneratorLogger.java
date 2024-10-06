package com.tlregen.mixin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.base.Stopwatch;
import com.tlregen.TLReGen;

import net.minecraft.WorldVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

@Mixin(DataGenerator.class)
public abstract class MixinDataGeneratorLogger {
	@Shadow
	Map<String, DataProvider> providersToRun;
	@Shadow
	boolean alwaysGenerate;
	@Shadow
	Path rootOutputFolder;
	@Shadow
	Set<String> allProviderIds;
	@Shadow
	WorldVersion version;
	@Unique
	private int previousWrites;
	@Unique
	private long priorCount;
	@Unique
	private int finalCount;
	@Unique
	private int created;
	@Unique
	private int deleted;
	@Unique
	private int changed;

	/**
	 * STOP FLUID PUSHING
	 * 
	 * @author burnsqc
	 * @reason cuz
	 * @return
	 */
	@Overwrite
	public void run() throws IOException {
		HashCache hashcache = new HashCache(this.rootOutputFolder, this.allProviderIds, this.version);
		Stopwatch stopwatch = Stopwatch.createStarted();
		Stopwatch stopwatch1 = Stopwatch.createUnstarted();
		String format = "%1$-60s|%2$-7s|%3$-7s|%4$-7s|%5$-7s|%6$-7s|%7$-7s";
		TLReGen.LOGGER.info(String.format(format, "PATH", " PRIOR ", " FINAL ", "CREATED", "DELETED", "CHANGED", "TIME"));
		TLReGen.LOGGER.info("------------------------------------------------------------+-------+-------+-------+-------+-------+-------");

		this.providersToRun.forEach((path, provider) -> {
			if (!this.alwaysGenerate && !hashcache.shouldRunInThisVersion(path)) {
				TLReGen.LOGGER.debug("Generator {} already run for version {}", path, this.version.getName());
			} else {
				net.minecraftforge.fml.StartupMessageManager.addModMessage("Generating: " + path);

				priorCount = hashcache.caches.get(path).count();

				stopwatch1.start();
				hashcache.applyUpdate(hashcache.generateUpdate(path, provider::run).join());
				stopwatch1.stop();

				finalCount = hashcache.caches.get(path).count();

				created = (int) Math.max(finalCount - priorCount, 0);
				deleted = (int) Math.max(priorCount - finalCount, 0);
				changed = hashcache.writes - previousWrites - created;

				TLReGen.LOGGER.info(String.format(format, path, priorCount, finalCount, created, deleted, changed, stopwatch1.elapsed(TimeUnit.MILLISECONDS) + "ms"));
				previousWrites = hashcache.writes;

				// previousWrites = hashcache.initialCount;
				stopwatch1.reset();
			}
		});
		TLReGen.LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
		hashcache.purgeStaleAndWrite();
	}
}
