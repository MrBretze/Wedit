package fr.bretzel.wedit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import fr.bretzel.wedit.render.BlockRender;
import fr.bretzel.wedit.undo.UndoBlock;
import fr.bretzel.wedit.util.Material;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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

    private static ArrayList<Material> priorityForSetBlock = new ArrayList<>();
    
    static 
    {
    	priorityForSetBlock.add(Material.DEAD_BUSH);
    	priorityForSetBlock.add(Material.SUGAR_CANE);
    	priorityForSetBlock.add(Material.CACTUS);
    	priorityForSetBlock.add(Material.FARMLAND);
    	priorityForSetBlock.add(Material.WATER);
    	priorityForSetBlock.add(Material.LAVA);
    	priorityForSetBlock.add(Material.CARROTS);
    	priorityForSetBlock.add(Material.BEETROOTS);
    	priorityForSetBlock.add(Material.POTATOES);
    	priorityForSetBlock.add(Material.WHEAT);
    	priorityForSetBlock.add(Material.GRASS);
    	priorityForSetBlock.add(Material.TALL_GRASS);
    	priorityForSetBlock.add(Material.LARGE_FERN);
    	priorityForSetBlock.add(Material.PEONY);
    	priorityForSetBlock.add(Material.ROSE_BUSH);
    	priorityForSetBlock.add(Material.LILAC);
    	priorityForSetBlock.add(Material.SUNFLOWER);
    	priorityForSetBlock.add(Material.SEAGRASS);
    	priorityForSetBlock.add(Material.DANDELION);
    	priorityForSetBlock.add(Material.POPPY);
    	priorityForSetBlock.add(Material.SEA_PICKLE);
    	priorityForSetBlock.add(Material.BLUE_ORCHID);
    	priorityForSetBlock.add(Material.ALLIUM);
    	priorityForSetBlock.add(Material.AZURE_BLUET);
    	priorityForSetBlock.add(Material.RED_TULIP);
    	priorityForSetBlock.add(Material.ORANGE_TULIP);
    	priorityForSetBlock.add(Material.WHITE_TULIP);
    	priorityForSetBlock.add(Material.PINK_TULIP);
    	priorityForSetBlock.add(Material.OXEYE_DAISY);
    	priorityForSetBlock.add(Material.BROWN_MUSHROOM);
    	priorityForSetBlock.add(Material.BROWN_MUSHROOM_BLOCK);
    	priorityForSetBlock.add(Material.RED_MUSHROOM);
    	priorityForSetBlock.add(Material.RED_MUSHROOM_BLOCK);
    	priorityForSetBlock.add(Material.LILY_PAD);
    	priorityForSetBlock.add(Material.TORCH);
    	priorityForSetBlock.add(Material.OAK_SAPLING);
    	priorityForSetBlock.add(Material.SPRUCE_SAPLING);
    	priorityForSetBlock.add(Material.BIRCH_SAPLING);
    	priorityForSetBlock.add(Material.JUNGLE_SAPLING);
    	priorityForSetBlock.add(Material.ACACIA_SAPLING);
    	priorityForSetBlock.add(Material.DARK_OAK_SAPLING);
    	priorityForSetBlock.add(Material.OAK_LEAVES);
    	priorityForSetBlock.add(Material.SPRUCE_LEAVES);
    	priorityForSetBlock.add(Material.BIRCH_LEAVES);
    	priorityForSetBlock.add(Material.JUNGLE_LEAVES);
    	priorityForSetBlock.add(Material.ACACIA_LEAVES);
    	priorityForSetBlock.add(Material.DARK_OAK_LEAVES);
    	priorityForSetBlock.add(Material.TALL_SEAGRASS);
    	priorityForSetBlock.add(Material.KELP_PLANT);
    	priorityForSetBlock.add(Material.KELP);
    	priorityForSetBlock.add(Material.GRAVEL);
    	priorityForSetBlock.add(Material.SAND);
    	priorityForSetBlock.add(Material.WHITE_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.ORANGE_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.MAGENTA_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.LIGHT_BLUE_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.YELLOW_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.LIME_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.PINK_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.GRAY_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.LIGHT_GRAY_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.CYAN_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.PURPLE_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.BLUE_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.BROWN_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.GREEN_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.RED_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.BLACK_CONCRETE_POWDER);
    	priorityForSetBlock.add(Material.COCOA);
    	priorityForSetBlock.add(Material.VINE);
    	priorityForSetBlock.add(Material.ANVIL);
    }
    
    //Complex method for a setblock
    public static void setBlock(BlockPos pos, Material material, String json)
    {
        if (isReplaceBlock(pos, material.getBlockName()))
        {
            String command = "/setblock %x %y %z %block replace %json";
            command = command.replace("%x", String.valueOf(pos.getX()));
            command = command.replace("%y", String.valueOf(pos.getY()));
            command = command.replace("%z", String.valueOf(pos.getZ()));
            command = command.replace("%block", material.getBlockName());
            command = command.replace("%json", json);
            //Remplace all value and send command
            broadcastCommand(command);
        }
    }

    //Basic method for a setblock
    public static void setBlock(BlockPos pos, String block)
    {
        setBlock(pos, Material.getMaterialOfBlock(block), "");
    }

    //Basic method for a setblock used UndoBlock class
    public static void setBlock(UndoBlock block)
    {
        setBlock(block.getPosition(), block.getMaterial(), block.getJsonSaveNbt());
    }

    //if the block is already placed
    private static boolean isReplaceBlock(BlockPos pos, String blockName)
    {
        IBlockState actual_blocksate = Minecraft.getMinecraft().world.getBlockState(pos);
        Block actual_block = actual_blocksate.getBlock();
        Material actu_mat = Material.getMaterialOfBlock(actual_block);
        Material new_mat = Material.getMaterialOfBlock(blockName);
        return actu_mat != new_mat;
    }

    //Basic utility to send a message
    public static void sendMessage(String message)
    {
        sendMessage(localPlayer, message);
    }

    //Basic utility to send a message to a player
    public static void sendMessage(EntityPlayer player, String message)
    {
        player.sendMessage(new TextComponentString(message));
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
    
    public static boolean isPriorityPos(Material material) 
    {
		return priorityForSetBlock.contains(material);
	}
}
