package org.minigame.plugin.command.arguments.lobby;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningGame;
import org.minigame.running.lobby.PublicLobby;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AcceptableLobbyMapArgument extends CommandElement {

    public AcceptableLobbyMapArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected MapGamemode parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        if(!(src instanceof Locatable)){
            throw args.createError(Text.of("You are required to be a player or CommndBlock to use this command"));
        }
        Locatable locatable = (Locatable) src;
        String next = args.next();
        Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(locatable);
        if(!opGame.isPresent()){
            throw args.createError(Text.of("You are not in a public lobby"));
        }
        if(!(opGame.get() instanceof PublicLobby)){
            throw args.createError(Text.of("You are not in a public lobby"));
        }
        PublicLobby lobby = (PublicLobby)opGame.get();
        Optional<MapGamemode> opType = lobby.getMapOptionsForVoting().stream().filter(g -> {
            if(g.getId().equalsIgnoreCase(next)){
                return true;
            }
            if(g.getIdName().equalsIgnoreCase(next)){
                return true;
            }
            return false;
        }).findFirst();
        if(opType.isPresent()){
            return opType.get();
        }
        throw args.createError(Text.of("Could not find gamemode"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        if(!(src instanceof Locatable)){
            return new ArrayList<>();
        }
        Locatable locatable = (Locatable) src;
        String peek;
        try {
            peek = args.peek();
        } catch (ArgumentParseException e) {
            return new ArrayList<>();
        }
        Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(locatable);
        if(!opGame.isPresent()){
            return new ArrayList<>();
        }
        if(!(opGame.get() instanceof PublicLobby)){
            return new ArrayList<>();
        }
        PublicLobby lobby = (PublicLobby)opGame.get();
        List<String> ret = new ArrayList<>();
        lobby.getMapOptionsForVoting().stream().filter(g -> {
            if(g.getId().startsWith(peek.toLowerCase())){
                return true;
            }
            if(g.getIdName().startsWith(peek.toLowerCase())){
                return true;
            }
            return false;
        }).forEach(g -> ret.add(g.getId()));
        return ret;
    }
}
