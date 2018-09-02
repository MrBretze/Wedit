package fr.bretzel.wedit.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class Vec3d
{
    private double x, y, z;

    public Vec3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(TileEntity te)
    {
        this(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public Vec3d add(double x, double y, double z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3d add(Vec3i vec)
    {
        return add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vec3d sub(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3d sub(Vec3d vec)
    {
        return sub(vec.x, vec.y, vec.z);
    }

    public Vec3d mul(double x, double y, double z)
    {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vec3d mul(double multiplier)
    {
        return mul(multiplier, multiplier, multiplier);
    }

    public Vec3d mul(Vec3d v)
    {
        return mul(v.getX(), v.getY(), v.getZ());
    }

    public Vec3d div(double x, double y, double z)
    {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vec3d div(double multiplier)
    {
        return div(multiplier, multiplier, multiplier);
    }

    public double length()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3d abs()
    {
        return new Vec3d(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public double dot(Vec3d v)
    {
        return x * v.getX() + y * v.getY() + z * v.getZ();
    }

    public Vec3d getRelative(EnumFacing facing)
    {
        return clone().add(facing.getDirectionVec());
    }

    public Block getBlock(World world)
    {
        return world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public BlockPos toBlockPos()
    {
        return new BlockPos(x, y, z);
    }

    public Vec3d clone()
    {
        return new Vec3d(x, y, z);
    }
}
