package com.onxon.antiblockrotation.mixin;

import com.onxon.antiblockrotation.SidebarEditor;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScoreboardObjective.class)
public class ScoreboardObjectiveMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void abr$title(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(SidebarEditor.title(cir.getReturnValue()));
    }
}
