package fr.bretzel.wedit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.command.api.IWeditCommand;
import fr.bretzel.wedit.undo.Undo;
import fr.bretzel.wedit.undo.UndoBlock;
import fr.bretzel.wedit.util.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandSet extends IWeditCommand
{
    private boolean hollow = false;

    public CommandSet(boolean hollow)
    {
        this.hollow = hollow;
    }

    @Override
    public boolean execute(EntityPlayerSP sender, String label, String[] args)
    {
        if (Wedit.getFirstPosition() == null)
        {
            Wedit.sendMessage(TextFormatting.AQUA + "Please select your first point.");
            return false;
        }
        else if (Wedit.getSecondPosition() == null)
        {
            Wedit.sendMessage(TextFormatting.AQUA + "Please select your second point.");
            return false;
        }

        if (args.length <= 0)
        {
            Wedit.sendMessage(TextFormatting.RED + "Syntax: #set <block> <skeep>");
            return false;
        }

        String[] blocks = args[0].split(":");
        Block blk = null;

        try
        {
            blk = CommandBase.getBlockByText(sender, blocks[0]);
        }
        catch (NumberInvalidException e)
        {
            e.printStackTrace();
        }

        if (blk == null)
        {
            Wedit.sendMessage(TextFormatting.RED + "Please chose a correct block name.");
            return false;
        }

        long speed = 0;

        if (args.length >= 1)
        {
            try
            {
                speed = Integer.valueOf(args[1]);
            }
            catch (Exception e)
            {
                speed = 0;
            }
        }

        ArrayList<BlockPos> priorityPos = new ArrayList<>();
        ArrayList<BlockPos> normalPos = new ArrayList<>();
        String block = args[0];
        int data = 0;
        BlockPos pos1 = Wedit.getFirstPosition();
        BlockPos pos2 = Wedit.getSecondPosition();
        int minx, maxx, miny, maxy, minz, maxz;
        minx = getMin(pos1.getX(), pos2.getX());
        miny = getMin(pos1.getY(), pos2.getY());
        minz = getMin(pos1.getZ(), pos2.getZ());
        maxx = getMax(pos1.getX(), pos2.getX());
        maxy = getMax(pos1.getY(), pos2.getY());
        maxz = getMax(pos1.getZ(), pos2.getZ());
        Undo undo = new Undo(pos1 , pos2, sender);

        if (block.indexOf(':') >= 0)
        {
            String[] argument = block.split(":");
            block = argument[0];
            data = Integer.valueOf(argument[1]);
        }

        System.out.println("Block: " + block + " Data: " + data);
        
        for (int z = minz; z <= maxz; z++)
        {
            for (int x = minx; x <= maxx; x++)
            {
                for (int y = maxy; y >= miny; y--)
                {
                    BlockPos p = new BlockPos(x, y, z);
                    IBlockState state = sender.getEntityWorld().getBlockState(p);
                    
                    if (state.getBlock().getLocalizedName().equalsIgnoreCase(block) && state.getBlock().getMetaFromState(state) == data)
                    {
                        continue;
                    }
                    
                    if(hollow)
                    {
                    	if (x == maxx || x == minx || z == maxz || z == minz ||
                            y == maxy || y == miny)
                    	{
                    		if (Wedit.isPriorityPos(Material.getMaterialOfBlock(state.getBlock())))
                            	priorityPos.add(p);
                            else
                            	normalPos.add(p);
                    		
                    		undo.addUndo(p);
                    	}
                    	else
                    		continue;
                    } else {
                    	if (Wedit.isPriorityPos(Material.getMaterialOfBlock(state.getBlock())))
                    		priorityPos.add(p);
                    	else
                    		normalPos.add(p);
                    	
                    	undo.addUndo(p);
                    }
                }
            }
        }

        Undo.addUndo(undo);
        
        priorityPos.sort(new Comparator<BlockPos>()
        	{
                @Override
                public int compare(BlockPos o1, BlockPos o2)
                {
                	if (o1.getY() == o2.getY())
                		return 0;
                	
                	if (o1.getY() > o2.getY())
                		return -1;
                	
                	if (o1.getY() < o2.getY())
                		return 1;
                	
                    return 0;
                }
            });
        
        for (BlockPos pos : priorityPos)
        {
        	Wedit.setBlock(pos, block, data);
        	
            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                }
                catch (InterruptedException e) {}
            }
        }
        
        for (BlockPos pos : normalPos)
        {
        	Wedit.setBlock(pos, block, data);
        	
            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                }
                catch (InterruptedException e) {}
            }
        }

        Wedit.sendMessage(TextFormatting.AQUA + "Your are set " + TextFormatting.BLUE + (priorityPos.size() + normalPos.size()) + TextFormatting.AQUA + " block of " + TextFormatting.BLUE + block.toUpperCase());
        return true;
    }

    public int getMax(int pos, int pos1)
    {
        return pos > pos1 ? pos : pos1;
    }

    public int getMin(int pos, int pos1)
    {
        return pos < pos1 ? pos : pos1;
    }

	@Override
	public String getUsage() {
		return "Usage: #set <block> <skeep>";
	}
}
