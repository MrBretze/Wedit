package fr.bretzel.wedit.util;

public class Vec3DCube
{
    private Vec3d min, max;

    public Vec3DCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
    }

    public Vec3DCube(Vec3d min, Vec3d max)
    {
        this.min = min;
        this.max = max;
    }

    public Vec3DCube expand(double size)
    {
        min.sub(size, size, size);
        max.add(size, size, size);
        return this;
    }

    public Vec3d getMin()
    {
        return min;
    }

    public Vec3d getMax()
    {
        return max;
    }
}
