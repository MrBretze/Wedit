package fr.bretzel.wedit.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class WeditMixin
{

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info)
    {
        System.out.println("Start of Wedit 2.0 Init ! (with Mixin API)");
    }

}
