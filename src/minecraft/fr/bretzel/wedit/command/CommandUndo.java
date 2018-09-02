package fr.bretzel.wedit.command;

import java.util.ArrayList;
import java.util.List;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.undo.Undo;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandUndo extends IWeditCommand
{
    @Override
    public boolean execute(EntityPlayerSP sender, String label, String[] args)
    {
        if (Undo.getAllUndo().size() > 0)
        {
            long speed = 0;

            if (args.length >= 0)
            {
                try
                {
                    speed = Integer.valueOf(args[0]);
                }
                catch (Exception e)
                {
                    speed = 0;
                }
            }

            Undo.processLastUndo(speed);
        }
        else
        {
            Wedit.sendMessage(TextFormatting.DARK_RED + "No undo found !");
        }

        return false;
    }

    @Override
    public String[] executeTab(EntityPlayerSP sender, String label, String args)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
