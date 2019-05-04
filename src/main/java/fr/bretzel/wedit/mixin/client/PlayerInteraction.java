package fr.bretzel.wedit.mixin.client;

import fr.bretzel.wedit.BlockInteract;
import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.selection.Selection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlayerInteraction
{
    @Shadow
    private GameMode gameMode;

    @Shadow
    public abstract void cancelBlockBreaking();

    @Inject(at = @At(value = "HEAD"), method = "attackBlock", cancellable = true)
    private void rightClickDetection(BlockPos blockPos, Direction direction_1, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null)
        {
            ItemStack stack = player.getStackInHand(Hand.MAIN);
            Selection selection = Selection.getSelection(player.getUuid());

            if (this.gameMode == GameMode.CREATIVE && stack != null && stack.getItem() == Wedit.SELECTION_WAND && !selection.isLocationOne(blockPos))
            {
                System.out.println("[Wedit] Selected the first point");

                World world = player.world;
                BlockState blockState = world.getBlockState(blockPos);

                Executors.newSingleThreadScheduledExecutor().schedule(() ->
                {
                    if (world.getBlockState(blockPos).getBlock() != blockState.getBlock())
                        Wedit.setBlock(blockState.getBlock(), blockPos, BlockInteract.REPLACE);

                }, 500, TimeUnit.MILLISECONDS);

                player.addChatMessage(new StringTextComponent(TextFormat.AQUA + "You are selected the first point"), true);
                Selection.getSelection(player.getUuid()).setLocationOne(blockPos);

                callbackInfo.cancel();
                this.cancelBlockBreaking();
                callbackInfo.setReturnValue(false);
            }
        } else
        {
            System.out.println("Player is null");
        }
    }

    @Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
    private void leftClickDetection(PlayerEntity playerEntity, World world, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo)
    {
        if (playerEntity.getStackInHand(Hand.MAIN).getItem() == Wedit.SELECTION_WAND)
        {
            HitResult blockRayTrace = playerEntity.rayTrace(4D, 0F, true);

            BlockPos blockPos = null;

            if (blockRayTrace.getType() == HitResult.Type.BLOCK)
            {
                blockPos = ((BlockHitResult) blockRayTrace).getBlockPos();
            }

            if (blockRayTrace.getType() == HitResult.Type.BLOCK)
            {
                blockPos = ((BlockHitResult) blockRayTrace).getBlockPos();
            }

            Selection selection = Selection.getSelection(playerEntity.getUuid());

            if (blockPos != null && world.getBlockState(blockPos).getBlock() != Blocks.AIR && !selection.isLocationTwo(blockPos))
            {
                System.out.println("[Wedit] Selected the second point");
                Selection.getSelection(playerEntity.getUuid()).setLocationTwo(blockPos);
                playerEntity.addChatMessage(new StringTextComponent(TextFormat.AQUA + "You are selected the second point"), true);
                callbackInfo.setReturnValue(ActionResult.PASS);
                callbackInfo.cancel();
            }
        }
    }
}
