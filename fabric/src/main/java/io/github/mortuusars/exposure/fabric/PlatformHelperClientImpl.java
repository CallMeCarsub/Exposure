package io.github.mortuusars.exposure.fabric;

import io.github.mortuusars.exposure.client.util.Minecrft;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class PlatformHelperClientImpl {
    public static BlockStateModel getModel(Identifier model) {
        // Fabric adds it's "fabric_resource" to id. Forge uses model location as is.
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(BuiltInRegistries.BLOCK.get(model).get().value().defaultBlockState());
    }
}
