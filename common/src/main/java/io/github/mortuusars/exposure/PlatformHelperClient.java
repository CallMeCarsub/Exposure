package io.github.mortuusars.exposure;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.resources.Identifier;

public class PlatformHelperClient {
    @ExpectPlatform
    public static BlockStateModel getModel(Identifier model) {
        throw new AssertionError();
    }
}
