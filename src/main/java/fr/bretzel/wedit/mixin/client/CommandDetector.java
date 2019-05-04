package fr.bretzel.wedit.mixin.client;

import fr.bretzel.wedit.command.CommandManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
public class CommandDetector
{
    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void chatDetection(String input, CallbackInfo info)
    {
        if (!input.isEmpty() && input.charAt(0) == '#')
        {
            System.out.println("Processes command !");
            CommandManager.processCommand(MinecraftClient.getInstance().player, input);
            info.cancel();
        }
    }
}
