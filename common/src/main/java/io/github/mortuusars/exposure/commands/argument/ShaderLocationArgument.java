package io.github.mortuusars.exposure.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ShaderLocationArgument extends ResourceLocationArgument {
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(getShaderLocations(), builder);
    }

    private static Stream<ResourceLocation> getShaderLocations() {
        return Minecraft.getInstance().getResourceManager()
                .listResources("post_effect", ShaderLocationArgument::filterLocations)
                .keySet()
                .stream()
                .map(resourceLocation -> resourceLocation.withPath(path -> path.substring("post_chain//".length(), path.indexOf(".json"))));
    }

    private static boolean filterLocations(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().endsWith(".json")
                && !resourceLocation.getPath().contains("shaders/program")
                && !resourceLocation.getPath().contains("shaders/core");
    }
}
