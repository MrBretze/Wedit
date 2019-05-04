package fr.bretzel.wedit.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeditBlock
{
    private Block block;
    private BlockState blockState;
    private World world;
    private BlockPos position;

    public WeditBlock(Block block, World world, BlockPos position)
    {
        this(block, block.getDefaultState(), world, position);
    }

    public WeditBlock(Block block, BlockState state, World world, BlockPos position)
    {
        this.block = block;
        this.blockState = state;
        this.world = world;
        this.position = position;
    }

    public World getWorld()
    {
        return world;
    }

    public Block getBlock()
    {
        return block;
    }

    public BlockPos getPosition()
    {
        return position;
    }

    public BlockState getBlockState()
    {
        return blockState;
    }
}
