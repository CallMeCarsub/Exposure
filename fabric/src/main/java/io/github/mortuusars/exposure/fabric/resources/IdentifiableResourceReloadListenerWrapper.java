package io.github.mortuusars.exposure.fabric.resources;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class IdentifiableResourceReloadListenerWrapper implements IdentifiableResourceReloadListener {
	private final ResourceLocation fabricId;
	private final PreparableReloadListener listener;

	public IdentifiableResourceReloadListenerWrapper(ResourceLocation id, PreparableReloadListener listener) {
		this.fabricId = id;
		this.listener = listener;
	}

	@Override
	public ResourceLocation getFabricId() {
		return this.fabricId;
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
		return listener.reload(preparationBarrier, resourceManager, executor, executor2);
	}
}
