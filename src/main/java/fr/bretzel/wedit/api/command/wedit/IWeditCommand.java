package fr.bretzel.wedit.api.command.wedit;

import net.minecraft.entity.player.PlayerEntity;

public abstract class IWeditCommand
{
    public abstract boolean execute(PlayerEntity sender, String label, String[] args);

    public abstract String getUsage();
}
