package fr.bretzel.wedit.block;

import net.minecraft.block.*;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class BlockHelper
{
    private static LinkedHashMap<Block, Integer> priorityBlocks = new LinkedHashMap<>();

    static
    {
        Registry.BLOCK.forEach(block ->
        {
            int priority = 0;

            if (block instanceof FlowerBlock)
            {
                priority = 100;
            } else if (block instanceof PlantBlock)
            {
                priority = 90;
            } else if (block instanceof FallingBlock)
            {
                priority = 80;
            } else if (block instanceof FluidBlock)
            {
                priority = 70;
            } else if (block instanceof LeavesBlock)
            {
                priority = 60;
            } else if (block instanceof AbstractBannerBlock)
            {
                priority = 50;
            } else if (block instanceof TorchBlock)
            {
                priority = 40;
            }

            priorityBlocks.put(block, priority);
        });
    }

    public static <E extends List<WeditBlock>> ArrayList<WeditBlock> sortByPriority(E e)
    {
        e.sort((o1, o2) ->
        {
            int prio1 = priorityBlocks.get(o1.getBlock());
            int prio2 = priorityBlocks.get(o2.getBlock());

            if (prio1 < prio2)
                return 1;
            else if (prio1 == prio2)
                return 0;
            else if (prio1 > prio2)
                return -1;

            return 0;
        });

        ArrayList<WeditBlock> ret = new ArrayList<>(e);
        Collections.copy(ret, e);
        return ret;
    }
}
