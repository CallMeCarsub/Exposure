package io.github.mortuusars.exposure.neoforge.event;

import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.ExposureClientReloadListener;
import io.github.mortuusars.exposure.client.gui.tooltip.CameraStandTooltip;
import io.github.mortuusars.exposure.client.input.KeyboardHandler;
import io.github.mortuusars.exposure.client.gui.tooltip.PhotographClientTooltip;
import io.github.mortuusars.exposure.client.gui.screen.ItemRenameScreen;
import io.github.mortuusars.exposure.client.gui.screen.album.AlbumScreen;
import io.github.mortuusars.exposure.client.gui.screen.album.LecternAlbumScreen;
import io.github.mortuusars.exposure.client.gui.screen.camera.CameraAttachmentsScreen;
import io.github.mortuusars.exposure.client.gui.screen.LightroomScreen;
import io.github.mortuusars.exposure.client.render.CameraStandEntityRenderer;
import io.github.mortuusars.exposure.client.render.GlassPhotographFrameEntityRenderer;
import io.github.mortuusars.exposure.client.render.PhotographFrameEntityRenderer;
import io.github.mortuusars.exposure.client.render.item.GlassTint;
import io.github.mortuusars.exposure.client.render.item.conditional.ProjectorActive;
import io.github.mortuusars.exposure.client.render.item.range.AlbumPhotos;
import io.github.mortuusars.exposure.client.render.item.range.Channels;
import io.github.mortuusars.exposure.client.render.item.range.Count;
import io.github.mortuusars.exposure.client.render.item.select.StackCameraAttachments;
import io.github.mortuusars.exposure.client.render.item.select.StackCameraStatus;
import io.github.mortuusars.exposure.world.inventory.tooltip.PhotographTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@SuppressWarnings("unused")
public class NeoForgeClientEvents {
    @EventBusSubscriber(modid = Exposure.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ExposureClient::init);
        }

        @SubscribeEvent
        public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
            event.register(Exposure.resource("glass_tint"), GlassTint.MAP_CODEC);
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(Exposure.MenuTypes.CAMERA_IN_HAND.get(), CameraAttachmentsScreen::new);
            event.register(Exposure.MenuTypes.CAMERA_ON_STAND.get(), CameraAttachmentsScreen::new);
            event.register(Exposure.MenuTypes.ALBUM.get(), AlbumScreen::new);
            event.register(Exposure.MenuTypes.LECTERN_ALBUM.get(), LecternAlbumScreen::new);
            event.register(Exposure.MenuTypes.LIGHTROOM.get(), LightroomScreen::new);
            event.register(Exposure.MenuTypes.ITEM_RENAME.get(), ItemRenameScreen::new);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Exposure.EntityTypes.PHOTOGRAPH_FRAME.get(), PhotographFrameEntityRenderer::new);
            event.registerEntityRenderer(Exposure.EntityTypes.CLEAR_PHOTOGRAPH_FRAME.get(), GlassPhotographFrameEntityRenderer::new);
            event.registerEntityRenderer(Exposure.EntityTypes.CAMERA_STAND.get(), CameraStandEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(PhotographTooltip.class, PhotographClientTooltip::new);
        }

        @SubscribeEvent
        public static void addClientReloadListeners(AddClientReloadListenersEvent event) {
            event.addListener(Exposure.resource("reload_listener"), new ExposureClientReloadListener());
        }

        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional event) {
            event.register(ExposureClient.Models.PHOTOGRAPH_FRAME_SMALL.id());
            event.register(ExposureClient.Models.PHOTOGRAPH_FRAME_MEDIUM.id());
            event.register(ExposureClient.Models.PHOTOGRAPH_FRAME_LARGE.id());
            event.register(ExposureClient.Models.CLEAR_PHOTOGRAPH_FRAME_SMALL.id());
            event.register(ExposureClient.Models.CLEAR_PHOTOGRAPH_FRAME_MEDIUM.id());
            event.register(ExposureClient.Models.CLEAR_PHOTOGRAPH_FRAME_LARGE.id());
            event.register(ExposureClient.Models.CAMERA_STAND.id());
            event.register(ExposureClient.Models.CAMERA_STAND_MOUNT.id());
        }

        @SubscribeEvent
        public static void registerSelectProperties(RegisterSelectItemModelPropertyEvent event) {
            event.register(ExposureClient.SelectProperties.CAMERA_STATUS, StackCameraStatus.TYPE);
            event.register(ExposureClient.SelectProperties.CAMERA_ATTACHMENTS, StackCameraAttachments.TYPE);
        }

        @SubscribeEvent
        public static void registerRangeSelectProperties(RegisterRangeSelectItemModelPropertyEvent event) {
            event.register(ExposureClient.RangeSelectProperties.ALBUM_PHOTOS, AlbumPhotos.MAP_CODEC);
            event.register(ExposureClient.RangeSelectProperties.CHANNELS, Channels.MAP_CODEC);
            event.register(ExposureClient.RangeSelectProperties.COUNT, Count.MAP_CODEC);
        }

        @SubscribeEvent
        public static void registerConditionalSelectProperties(RegisterConditionalItemModelPropertyEvent event) {
            event.register(ExposureClient.ConditionalProperties.PROJECTOR_ACTIVE, ProjectorActive.MAP_CODEC);
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            KeyboardHandler.registerKeymappings(key -> {
                event.register(key);
                return key;
            });
        }
    }

    @EventBusSubscriber(modid = Exposure.ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            CameraStandTooltip.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
