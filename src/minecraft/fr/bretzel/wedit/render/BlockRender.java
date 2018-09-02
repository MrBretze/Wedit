package fr.bretzel.wedit.render;

import org.lwjgl.opengl.GL11;

import fr.bretzel.wedit.util.Vec3DCube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;

public class BlockRender
{
    public static void renderSelectedBlockAt(BlockPos newLoc)
    {
        Ordinal[] var5 = Ordinal.values();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            Ordinal ordinal = var5[var7];
            BlockPos center;

            if (ordinal != Ordinal.DOWN && ordinal != Ordinal.UP)
            {
                center = ordinal.add(clonePos(newLoc));

                for (int i = 0; i < 10; ++i)
                {
                    //(new ParticlePacket(effect, color, true)).sendTo(center, 256.0D);
                    center = center.add(0.0D, 0.1D, 0.0D);
                }
            }
            else
            {
                center = ordinal.add(clonePos(newLoc));
                BlockPos xLoc = clonePos(center);
                BlockPos zLoc = clonePos(center);
                int i;

                for (i = 0; i < 10; ++i)
                {
                    //(new ParticlePacket(effect, color, true)).sendTo(xLoc, 256.0D);
                    xLoc = xLoc.add(0.1D, 0.0D, 0.0D);
                    //(new ParticlePacket(effect, color, true)).sendTo(zLoc, 256.0D);
                    zLoc = zLoc.add(0.0D, 0.0D, 0.1D);
                }

                for (i = 0; i < 10; ++i)
                {
                    //(new ParticlePacket(effect, color, true)).sendTo(xLoc, 256.0D);
                    xLoc = xLoc.add(0.0D, 0.0D, 0.1D);
                    //(new ParticlePacket(effect, color, true)).sendTo(zLoc, 256.0D);
                    zLoc = zLoc.add(0.1D, 0.0D, 0.0D);
                }
            }
        }
    }

    private static BlockPos clonePos(BlockPos pos)
    {
        return new BlockPos(pos);
    }

    private static enum Ordinal
    {
        NORTH(0.0D, 0.0D, 0.0D),
        SOUTH(1.0D, 0.0D, 1.0D),
        WEST(0.0D, 0.0D, 1.0D),
        EAST(1.0D, 0.0D, 0.0D),
        UP(0.0D, 1.0D, 0.0D),
        DOWN(0.0D, 0.0D, 0.0D);

        private double x;
        private double y;
        private double z;

        private Ordinal(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX()
        {
            return this.x;
        }

        public double getY()
        {
            return this.y;
        }

        public double getZ()
        {
            return this.z;
        }

        public BlockPos add(BlockPos location)
        {
            return clonePos(location).add(this.getX(), this.getY(), this.getZ());
        }
    }
}
