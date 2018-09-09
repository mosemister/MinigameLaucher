package org.minigame.plugin.command.commands;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.plugin.command.arguments.lobby.AcceptableLobbyMapArgument;
import org.minigame.running.RunningGame;
import org.minigame.running.lobby.PublicLobby;
import org.minigame.utils.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class LobbyCommand {

    private static Text MAP = Text.of("Map");
    private static Text GAMEMODE = Text.of("Gamemode");

    private static abstract class Vote implements CommandExecutor {

        private static class VoteForGamemode extends Vote {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                if(!(src instanceof Player)){
                    src.sendMessage(Text.of("This is a player only command"));
                    return CommandResult.success();
                }
                Player player = (Player)src;
                Optional<GamemodeType> opType = args.getOne(GAMEMODE);
                if(!opType.isPresent()){
                    throw new CommandException(Text.of("Unknown map"));
                }
                GamemodeType gamemode = opType.get();
                Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(player);
                if(!opGame.isPresent()){
                    throw new CommandException(Text.of("You are not in a lobby"));
                }
                if(!(opGame.get() instanceof PublicLobby)){
                    throw new CommandException(Text.of("You are not in a public lobby"));
                }
                PublicLobby lobby = (PublicLobby)opGame.get();
                lobby.vote(gamemode, player.getUniqueId());
                return CommandResult.success();
            }
        }

        private static class VoteForMap extends Vote {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                if(!(src instanceof Player)){
                    src.sendMessage(Text.of("This is a player only command"));
                    return CommandResult.success();
                }
                Player player = (Player)src;
                Optional<MapGamemode> opMap = args.getOne(MAP);
                if(!opMap.isPresent()){
                    throw new CommandException(Text.of("Unknown map"));
                }
                MapGamemode gamemode = opMap.get();
                Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(player);
                if(!opGame.isPresent()){
                    throw new CommandException(Text.of("You are not in a lobby"));
                }
                if(!(opGame.get() instanceof PublicLobby)){
                    throw new CommandException(Text.of("You are not in a public lobby"));
                }
                PublicLobby lobby = (PublicLobby)opGame.get();
                lobby.vote(gamemode, player.getUniqueId());
                return CommandResult.success();
            }
        }

    }

    private static CommandSpec createVoteCommand(){
        CommandSpec map = CommandSpec.builder()
                .description(Text.of("Vote for a map"))
                .permission(Permissions.CMD_LOBBY_VOTE_MAP)
                .executor(new Vote.VoteForMap())
                .arguments(
                    new AcceptableLobbyMapArgument(MAP)
                ).build();
        CommandSpec gamemode = CommandSpec.builder()
                .description(Text.of())
                .permission(Permissions.CMD_LOBBY_VOTE_GAMEMODE)
                .executor(new Vote.VoteForGamemode())
                .arguments(
                        new AcceptableLobbyMapArgument(GAMEMODE)
                ).build();
        return CommandSpec.builder()
                .description(Text.of("Vote for something in a public lobby"))
                .child(gamemode, "gamemode", "game")
                .child(map, "map")
                .build();
    }

    public static CommandSpec createCommand(){
        CommandSpec vote = createVoteCommand();
        return CommandSpec.builder()
                .description(Text.of("All lobby commands"))
                .child(vote, "vote")
                .build();
    }
}
