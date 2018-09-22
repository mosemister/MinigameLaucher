package org.minigame.plugin.command.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class CommandAdditions {

    String[] aliance;
    CommandCallable spec;

    public CommandAdditions(CommandCallable spec, String... aliance){
        this.aliance = aliance;
        this.spec = spec;
    }

    public String[] getAliances(){
        return this.aliance;
    }

    public <T extends CommandCallable> T getSpec(){
        return (T)this.spec;
    }
}
