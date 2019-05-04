package fr.bretzel.wedit;

import fr.bretzel.wedit.command.CommandManager;
import fr.bretzel.wedit.command.wedit.CommandSet;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class Wedit implements ModInitializer
{

    public static Item SELECTION_WAND = Items.GOLDEN_SWORD;

    public static void setBlock(Block block, BlockPos blockPos)
    {
        setBlock(block, blockPos, BlockInteract.KEEP);
    }

    public static void sendMessage(String msg)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.player != null)
            minecraftClient.player.addChatMessage(new StringTextComponent(msg), false);
    }

    public static void setBlock(Block block, BlockPos blockPos, BlockInteract blockInteract)
    {
        Identifier id = Registry.BLOCK.getId(block);
        String blockName = id.getNamespace() + ":" + id.getPath();

        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.player != null)
        {
            ClientPlayNetworkHandler networkHandler = minecraftClient.player.networkHandler;
            networkHandler.sendPacket(new ChatMessageC2SPacket(String.format(
                    "/setblock %d %d %d %s %s",
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockName, blockInteract.name().toLowerCase())));
        }
    }

    @Override
    public void onInitialize()
    {
        System.out.println("Start of Wedit 2.0 Init ! (with Fabric API)");

        CommandManager.registerCommand(new CommandSet(false), "set");
        CommandManager.registerCommand(new CommandSet(true), "hset");

        /*

        //Register all command
        registerCommand(new CommandSet(false), "set");
        registerCommand(new CommandSet(true), "hset");
        registerCommand(new CommandSphere(false), "sphere");
        registerCommand(new CommandSphere(true), "hsphere");
        registerCommand(new CommandUndo(), "undo");
        registerCommand(new CommandPos(true), "pos1");
        registerCommand(new CommandPos(false), "pos2");
        registerCommand(new CommandCircle(false), "circle");
        registerCommand(new CommandCircle(true), "hcircle");
         */
    }

    public static boolean isServer()
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return !minecraftClient.isInSingleplayer();
    }
}
