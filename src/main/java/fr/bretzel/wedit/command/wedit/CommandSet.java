package fr.bretzel.wedit.command.wedit;

import fr.bretzel.wedit.BlockInteract;
import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.api.command.wedit.IWeditCommand;
import fr.bretzel.wedit.selection.Selection;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class CommandSet extends IWeditCommand
{
    private boolean hollow = false;

    public CommandSet(boolean hollow)
    {
        this.hollow = hollow;
    }

    @Override
    public boolean execute(PlayerEntity sender, String label, String[] args)
    {
        Selection selection = Selection.getSelection(sender.getUuid());

        if (selection.getLocationOne() == null)
        {
            Wedit.sendMessage(TextFormat.AQUA + "Please select your first point.");
            return false;
        } else if (selection.getLocationTwo() == null)
        {
            Wedit.sendMessage(TextFormat.AQUA + "Please select your second point.");
            return false;
        }

        if (args.length <= 0)
        {
            Wedit.sendMessage(TextFormat.RED + "Syntax: #set <block> <skeep>");
            return false;
        }

        String[] blocks = args[0].split(":");

        Block blk = Registry.BLOCK.get(new Identifier(blocks[0]));

        if (blk == null)
        {
            Wedit.sendMessage(TextFormat.RED + "Please chose a correct block name.");
            return false;
        }

        long speed;


        try
        {
            speed = Integer.valueOf(args[1]);
        } catch (Exception e)
        {
            speed = 0;
        }

        if (speed == 0 && Wedit.isServer())
            speed = 1;

        ArrayList<BlockPos> priorityPos = new ArrayList<>();
        ArrayList<BlockPos> normalPos = new ArrayList<>();
        String block = args[0];

        BlockPos pos1 = selection.getLocationOne();
        BlockPos pos2 = selection.getLocationTwo();

        int minx, maxx, miny, maxy, minz, maxz;
        minx = getMin(pos1.getX(), pos2.getX());
        miny = getMin(pos1.getY(), pos2.getY());
        minz = getMin(pos1.getZ(), pos2.getZ());
        maxx = getMax(pos1.getX(), pos2.getX());
        maxy = getMax(pos1.getY(), pos2.getY());
        maxz = getMax(pos1.getZ(), pos2.getZ());

        //Undo undo = new Undo(pos1, pos2, sender);

        if (block.indexOf(':') >= 0)
        {
            String[] argument = block.split(":");
            block = argument[0];
        }

        System.out.println("Block: " + block);

        for (int z = minz; z <= maxz; z++)
        {
            for (int x = minx; x <= maxx; x++)
            {
                for (int y = maxy; y >= miny; y--)
                {
                    BlockPos p = new BlockPos(x, y, z);

                    if (hollow)
                    {
                        if (x == maxx || x == minx || z == maxz || z == minz ||
                                y == maxy || y == miny)
                        {
                            /*if (Wedit.isPriorityPos(Material.getMaterialOfBlock(state.getBlock())))
                                priorityPos.add(p);
                            else
                                normalPos.add(p);*/
                            normalPos.add(p);
                            //undo.addUndo(p);
                        } else
                            continue;
                    } else
                    {
                        /*if (Wedit.isPriorityPos(Material.getMaterialOfBlock(state.getBlock())))
                            priorityPos.add(p);
                        else
                            normalPos.add(p);

                        undo.addUndo(p);*/
                        normalPos.add(p);
                    }
                }
            }
        }

        //Undo.addUndo(undo);

        for (BlockPos pos : normalPos)
        {
            if (sender.world.getBlockState(pos).getBlock() == blk)
                continue;

            Wedit.setBlock(blk, pos, BlockInteract.REPLACE);

            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                } catch (InterruptedException e)
                {}
            }
        }

        Wedit.sendMessage(TextFormat.AQUA + "Your are set " + TextFormat.BLUE + (priorityPos.size() + normalPos.size()) + TextFormat.AQUA + " block of " + TextFormat.BLUE + block.toUpperCase());
        return true;
    }

    public int getMax(int pos, int pos1)
    {
        return pos > pos1 ? pos : pos1;
    }

    public int getMin(int pos, int pos1)
    {
        return pos < pos1 ? pos : pos1;
    }

    @Override
    public String getUsage()
    {
        if (hollow)
            return "Usage: #hset <block> <skeep>";
        else return "Usage: #set <block> <skeep>";
    }
}
