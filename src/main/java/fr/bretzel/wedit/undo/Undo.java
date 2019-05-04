package fr.bretzel.wedit.undo;

import fr.bretzel.wedit.selection.Selection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class Undo
{
    private Selection selection;
    private World world;

    /**
     * Store all affected block
     */
    private List<BlockState> blocksList = new LinkedList<>();

    public Undo(BlockPos pos1, BlockPos pos2, World world)
    {
        this(new Selection(pos1, pos2), world);
    }

    public Undo(Selection selection, World world)
    {
        this.selection = selection;
        this.world = world;

        for (int z = selection.getMinZ(); z <= selection.getMaxZ(); z++)
        {
            for (int x = selection.getMinX(); x <= selection.getMaxX(); x++)
            {
                for (int y = selection.getMaxY(); y >= selection.getMinY(); y--)
                {
                    getBlocksList().add(world.getBlockState(new BlockPos(x, y, z)));
                }
            }
        }
    }

    /**
     * @return the first location
     */
    public BlockPos getPos1()
    {
        return selection.getLocationOne();
    }

    /**
     * @return the second location
     */
    public BlockPos getPos2()
    {
        return selection.getLocationTwo();
    }

    public Selection getSelection()
    {
        return selection;
    }

    /**
     * @return the world of undo
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * List of all block
     *
     * @return all block in selection
     */
    public List<BlockState> getBlocksList()
    {
        return blocksList;
    }
}
