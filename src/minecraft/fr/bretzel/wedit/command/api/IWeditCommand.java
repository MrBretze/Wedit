package fr.bretzel.wedit.command.api;

import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;

public abstract class IWeditCommand
{
    public abstract boolean execute(EntityPlayerSP sender, String label, String[] args);

    public abstract String getUsage();
}
