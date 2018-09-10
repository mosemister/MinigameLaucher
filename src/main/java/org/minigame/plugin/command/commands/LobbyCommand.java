package org.minigame.plugin.command.commands;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.gamemode.lobby.LobbyType;
import org.minigame.map.builder.MapBuilder;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.DefaultRegisters;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.plugin.command.arguments.lobby.AcceptableLobbyMapArgument;
import org.minigame.running.RunningGame;
import org.minigame.running.lobby.HostableLobby;
import org.minigame.running.lobby.PublicLobby;
import org.minigame.utils.MinigameWorldGenerator;
import org.minigame.utils.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LobbyCommand {

    private static Text MAP = Text.of("Map");
    private static Text GAMEMODE = Text.of("Gamemode");
    private static Text HOST = Text.of("Host");

    private static abstract class Create implements CommandExecutor {

        private static class Private extends Create {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                Optional<Player> opHost = args.getOne(HOST);
                if(!opHost.isPresent()){
                    throw new CommandException(Text.of("A private lobby requires a host"));
                }
                Player host = opHost.get();

                UnplayableMap unplayableMap = MinigamePlugin.getPlugin().getDefaultLobbyMap();
                LobbyType type = DefaultRegisters.LOBBY_GAMEMODE;
                Optional<MapGamemode<? extends GamemodeType>> opGMMap = MinigamePlugin.getMapGamemode(type, unplayableMap);
                if(!opGMMap.isPresent()){
                    throw new CommandException(Text.of("The default map for lobbies can not run the lobby gamemode. "));
                }

                Vector3i startingPos = MinigameWorldGenerator.MINIGAME_MAP_PLAYING_STARTING_POSITION;
                Location<World> loc = MinigamePlugin.getPlugin().getMinigamesWorld().getLocation(startingPos);
                host.sendMessage(Text.builder("Building lobby map, please wait").color(TextColors.AQUA).build());
                new MapBuilder(loc, unplayableMap) {
                    @Override
                    protected void onBuilt(ReadyToPlayMap map, Object plugin) {
                        System.out.println("Map built");
                        LobbyMapGamemode mapGamemode = (LobbyMapGamemode) opGMMap.get();
                        HostableLobby lobby = new HostableLobby(map, mapGamemode, host.getUniqueId());
                        lobby.register(host);
                        MinigamePlugin.register(lobby);
                        lobby.spawn(host);
                        System.out.println("Spawned");
                    }
                }.build(MinigamePlugin.getPlugin());
                return CommandResult.success();
            }
        }
    }

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

    public static CommandSpec createCreateCommand(){
        CommandSpec privateLobby = CommandSpec.builder()
                .description(Text.of("Created a invite only lobby"))
                .executor(new Create.Private())
                .arguments(GenericArguments.playerOrSource(HOST))
                .permission(Permissions.CMD_LOBBY_VOTE_GAMEMODE)
                .build();
        return CommandSpec.builder()
                .child(privateLobby, "private", "pri")
                .build();

    }

    public static CommandSpec createCommand(){
        CommandSpec vote = createVoteCommand();
        CommandSpec create = createCreateCommand();
        return CommandSpec.builder()
                .description(Text.of("All lobby commands"))
                .child(vote, "vote")
                .child(create, "create", "host")
                .build();
    }
}
