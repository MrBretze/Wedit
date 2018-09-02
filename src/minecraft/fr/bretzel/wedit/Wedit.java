package fr.bretzel.wedit;

import java.lang.reflect.Field;
import java.util.Map;

import fr.bretzel.wedit.render.BlockRender;
import fr.bretzel.wedit.undo.UndoBlock;
import fr.bretzel.wedit.util.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Wedit
{
    //This is the selected block.
    //this is set in PlayerControllerMP
    private static BlockPos POS_1, POS_2;

    //Return to the player
    private static EntityPlayerSP localPlayer = Minecraft.getMinecraft().player;

    //Basic method for a setblock
    public static void setBlock(BlockPos pos, String block)
    {
        setBlock(pos, block, 0);
    }

    //Complex method for a setblock
    public static void setBlock(BlockPos pos, Material material, String json, int data)
    {
        if (isReplaceBlock(pos, material.getBlockName(), data))
        {
            String command = "/setblock %x %y %z %block %data replace %json";
            command = command.replace("%x", String.valueOf(pos.getX()));
            command = command.replace("%y", String.valueOf(pos.getY()));
            command = command.replace("%z", String.valueOf(pos.getZ()));
            command = command.replace("%block", material.getBlockName());
            command = command.replace("%data", String.valueOf(data));
            command = command.replace("%json", json);
            //Remplace all value and send command
            broadcastCommand(command);
        }
    }

    //Basic method for a setblock
    public static void setBlock(BlockPos pos, String block, int data)
    {
        setBlock(pos, Material.getMaterialOfBlock(block), "", data);
    }

    //Basic method for a setblock used UndoBlock class
    public static void setBlock(UndoBlock block)
    {
        setBlock(block.getPosition(), block.getMaterial(), block.getJsonSaveNbt(), block.getData());
    }

    //if the block is already placed
    private static boolean isReplaceBlock(BlockPos pos, String blockName, int data)
    {
        IBlockState actual_blocksate = Minecraft.getMinecraft().world.getBlockState(pos);
        Block actual_block = actual_blocksate.getBlock();
        Material actu_mat = Material.getMaterialOfBlock(actual_block);
        Material new_mat = Material.getMaterialOfBlock(blockName);
        int actual_data = actual_block.getMetaFromState(actual_blocksate);
        return (actual_data != data) || (actu_mat != new_mat);
    }

    //Basic utility to send a message
    public static void sendMessage(String message)
    {
        sendMessage(localPlayer, message);
    }

    //Basic utility to send a message to a player
    public static void sendMessage(EntityPlayer player, String message)
    {
        player.addChatMessage(new TextComponentString(message));
    }

    //Set the gamerule command feed back to your value
    public static void setCommandFeedBack(boolean value)
    {
        broadcastCommand("/gamerule sendCommandFeedback " + String.valueOf(value).toLowerCase());
    }

    //Utility to run a command
    public static void broadcastCommand(String command)
    {
        if (!command.startsWith("/"))
        {
            command = "/" + command;
        }

        localPlayer.sendChatMessage(command);
    }

    public static BlockPos getFirstPosition()
    {
        return POS_1;
    }

    public static BlockPos getSecondPosition()
    {
        return POS_2;
    }

    public static boolean setFirstPosition(BlockPos pos)
    {
        if (POS_1 == null || !pos.toString().equalsIgnoreCase(POS_1.toString()))
        {
            POS_1 = pos;
            BlockRender.renderSelectedBlockAt(pos);
            Wedit.sendMessage(TextFormatting.AQUA + "You are selected the first point.");
            return true;
        }

        return false;
    }

    public static boolean setSecondPosition(BlockPos pos)
    {
        if (POS_2 == null || !pos.toString().equalsIgnoreCase(POS_2.toString()))
        {
            POS_2 = pos;
            BlockRender.renderSelectedBlockAt(pos);
            Wedit.sendMessage(TextFormatting.AQUA + "You are selected the second point.");
            return true;
        }

        return false;
    }
}
