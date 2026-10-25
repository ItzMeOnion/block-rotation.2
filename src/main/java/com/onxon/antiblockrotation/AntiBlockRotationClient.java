package com.onxon.antiblockrotation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AntiBlockRotationClient implements ClientModInitializer {
    public static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        Config.load();

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.anti_block_rotation.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClientScreen(null));
                }
            }
        });
    }
}
