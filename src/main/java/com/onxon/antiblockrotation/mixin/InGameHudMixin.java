package com.onxon.antiblockrotation.mixin;

import com.onxon.antiblockrotation.SidebarEditor;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 5000)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"))
    private void abr$begin(CallbackInfo ci) {
        SidebarEditor.begin();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
    private void abr$end(CallbackInfo ci) {
        SidebarEditor.end();
    }
}
