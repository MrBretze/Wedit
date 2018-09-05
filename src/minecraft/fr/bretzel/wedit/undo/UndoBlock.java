package fr.bretzel.wedit.undo;

import fr.bretzel.wedit.util.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UndoBlock
{
    private NBTTagCompound tag;
    private BlockPos position;
    private Material material;

    public UndoBlock(BlockPos pos)
    {
        World world =  Minecraft.getMinecraft().player.getEntityWorld();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        this.position = pos;
        this.material = Material.getMaterialOfBlock(block);
        /*if (material.isNbtSavable())
        {
            TileEntity tile = world.getTileEntity(pos);
            NBTTagCompound compound = tile.writeToNBT(new NBTTagCompound());

            if (tile instanceof TileEntityCommandBlock)
            {
                Minecraft.getMinecraft().player.sendChatMessage("/blockdata %x %y %z {}".replaceAll("%x", String.valueOf(pos.getX())).replaceAll("%y", String.valueOf(pos.getY())).replaceAll("%z", String.valueOf(pos.getZ())));
            }

            System.out.println(compound.toString());
            this.tag = compound;
        }*/
    }

    public String getBlockName()
    {
        return getMaterial().getBlockName();
    }

    public BlockPos getPosition()
    {
        return position;
    }

    public Material getMaterial()
    {
        return material;
    }

    public String getJsonSaveNbt()
    {
        return tag == null ? "" : tag.toString();
    }
}
