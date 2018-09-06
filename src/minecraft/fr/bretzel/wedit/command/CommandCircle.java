package fr.bretzel.wedit.command;

import java.util.ArrayList;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.command.api.IWeditCommand;
import fr.bretzel.wedit.undo.Undo;
import fr.bretzel.wedit.util.Material;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class CommandCircle extends IWeditCommand {

    private boolean hollow ;

    public CommandCircle(boolean hollow)
    {
        this.hollow = hollow;
    }
	
	@Override
	public boolean execute(EntityPlayerSP sender, String label, String[] args) {
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
		Block blk = Material.getNameOfBlock(blocks[0]);

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

		if (block.indexOf(':') >= 0)
		{
			String[] argument = block.split(":");
			block = argument[0];
		}
		
		ArrayList<BlockPos> normal = new ArrayList<>();
		ArrayList<BlockPos> priority = new ArrayList<>();
		
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        
        int rSquared = radius * radius;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    BlockPos pos = new BlockPos(x, cy, z);
                    if (Wedit.isPriorityPos(Material.getMaterialOfBlock(sender.getEntityWorld().getBlockState(pos).getBlock())))
                    {
                    	//TODO
                    } else {
                    	//TODO
                    }
                }
            }
        }
		return false;
	}

	public boolean isHollow() {
		return hollow;
	}
	
	@Override
	public String getUsage() {
		return isHollow() ? "Usage: #hcircle <block> <radius> <skeep>" : "Usage: #circle <block> <radius> <skeep>";
	}

}
