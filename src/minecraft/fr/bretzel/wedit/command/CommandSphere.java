package fr.bretzel.wedit.command;

import java.util.ArrayList;
import java.util.List;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.command.api.IWeditCommand;
import fr.bretzel.wedit.undo.Undo;
import fr.bretzel.wedit.util.Material;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandSphere extends IWeditCommand
{
	private boolean hollow ;

	public CommandSphere(boolean hollow)
	{
		this.hollow = hollow;
	}

	@Override
	public boolean execute(EntityPlayerSP sender, String label, String[] args)
	{
		BlockPos center = sender.getPosition();

		if (Wedit.getFirstPosition() == null)
		{
			Wedit.sendMessage(TextFormatting.RED + "Please set the first point !");
			return false;
		} else
		{
			center = Wedit.getFirstPosition();
		}
		
		if (args.length <= 2)
		{
			Wedit.sendMessage(TextFormatting.RED + "Syntax: #sphere <block> <radius> <skeep>");
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

		if (args.length >= 3)
		{
			try
			{
				speed = Integer.valueOf(args[2]);
			}
			catch (Exception e)
			{
				speed = 0;
			}
		}

		int radius = 1;

		if (args.length >= 2)
		{
			try
			{
				radius = Integer.valueOf(args[1]);
			}
			catch (Exception e)
			{
				radius = 1;
			}
		}

		Undo undo = new Undo(center, null, sender);

		String block = args[0];
		int data = 0;

		if (block.indexOf(':') >= 0)
		{
			String[] argument = block.split(":");
			block = argument[0];
			data = Integer.valueOf(argument[1]);
		}
		
		ArrayList<BlockPos> normal = new ArrayList<>();
		ArrayList<BlockPos> priority = new ArrayList<>();
		
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

		for (int z = cz - radius; z <= cz + radius; z++)
		{
			for (int x = cx - radius; x <= cx + radius; x++)
			{
				for (int y = cy + radius; y >= cy - radius; y--)
				{
					double distance = ((cx - x) * (cx - x) + ((cz - z) * (cz - z)) + ((cy - y) * (cy - y)));

					if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1))))
					{
						BlockPos pos = new BlockPos(x, y, z);
						
						undo.addUndo(pos);
						
						if (Wedit.isPriorityPos(Material.getMaterialOfBlock(sender.getEntityWorld().getBlockState(pos).getBlock())))
						{
							priority.add(pos);
						} else
						{
							normal.add(pos);
						}
					}
				}
			}
		}
		
		for(BlockPos pos : priority)
		{
			Wedit.setBlock(pos, block, data);
			
            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                }
                catch (InterruptedException e)
                {
                }
            }
		}

		for(BlockPos pos : normal)
		{
			Wedit.setBlock(pos, block, data);
			
            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                }
                catch (InterruptedException e)
                {
                }
            }
		}
		
		Undo.addUndo(undo);
		
		Wedit.sendMessage(TextFormatting.AQUA + "Your are set " + TextFormatting.BLUE + (priority.size() + normal.size()) + TextFormatting.AQUA + " block of " + TextFormatting.BLUE + block.toUpperCase());
		return true;
	}

	public boolean isHollow() {
		return hollow;
	}

	@Override
	public String getUsage() {
		return isHollow() ? "Usage: #hsphere <block> <radius> <skeep>" : "Usage: #sphere <block> <radius> <skeep>";
	}
}
