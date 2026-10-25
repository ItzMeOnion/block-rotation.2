package com.onxon.antiblockrotation.mixin;

import com.onxon.antiblockrotation.SidebarEditor;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// High priority number = runs after other mods' hooks, so our text gets the final say.
@Mixin(value = Team.class, priority = 5000)
public class TeamMixin {
    @Inject(method = "decorateName", at = @At("RETURN"), cancellable = true)
    private static void abr$line(CallbackInfoReturnable<MutableText> cir) {
        cir.setReturnValue(SidebarEditor.line(cir.getReturnValue()));
    }
}
