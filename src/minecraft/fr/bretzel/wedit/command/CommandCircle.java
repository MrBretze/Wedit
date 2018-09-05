package fr.bretzel.wedit.command;

import java.util.ArrayList;

import fr.bretzel.wedit.command.api.IWeditCommand;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;

public class CommandCircle extends IWeditCommand {

    private boolean hollow ;

    public CommandCircle(boolean hollow)
    {
        this.hollow = hollow;
    }
	
	@Override
	public boolean execute(EntityPlayerSP sender, String label, String[] args) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        
        int rSquared = radius * radius;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    BlockPos pos = new BlockPos(x, cy, z);
                    circleBlocks.add(pos);
                }
            }
        }
		return false;
	}

	public boolean isHollow() {
		return hollow;
	}
	
	private ArrayList<BlockPos> generateCircle(BlockPos center, int radius, boolean hollow)
	{
		ArrayList<BlockPos> circleBlocks = new ArrayList<>();
		
		return null;
	}
	
	@Override
	public String getUsage() {
		return isHollow() ? "Usage: #hcircle <block> <radius> <skeep>" : "Usage: #circle <block> <radius> <skeep>";
	}

}
