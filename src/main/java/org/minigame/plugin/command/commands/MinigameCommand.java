package org.minigame.plugin.command.commands;

import org.minigame.data.MinigameKeys;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningGame;
import org.minigame.running.RunningLiveGame;
import org.minigame.running.lobby.RunningLobby;
import org.minigame.running.mapmaking.RunningMapMaker;
import org.minigame.running.midjoinable.InvitableGame;
import org.minigame.running.midjoinable.MidJoinableGame;
import org.minigame.utils.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MinigameCommand {

    public static final Text PLAYER = Text.of("player");
    private static final Text PLAYER_TO_INVITE = Text.of("player to invite");
    private static final Text WHOMS_GAME = Text.of("Whom's game");

    static class LeaveGame implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<Player> opPlayer = args.getOne(PLAYER);
            if(!opPlayer.isPresent()){
                throw new CommandException(Text.of("Player must be specified to kick"));
            }
            Player player = opPlayer.get();
            Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(player);
            if(!opGame.isPresent()){
                throw new CommandException(Text.of("Player is not in a minigame"));
            }
            RunningGame<? extends MinigameMap> game = opGame.get();
            game.unregister(player, (!src.equals(player)));
            return CommandResult.success();
        }
    }

    private static class ViewInvites implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<Player> opPlayer = args.getOne(PLAYER);
            if(!opPlayer.isPresent()){
                throw new CommandException(Text.of("Player must be specified"));
            }
            Player player = opPlayer.get();
            Map<UUID, MidJoinableGame<? extends MinigameMap>> map = player.get(MinigameKeys.MINIGAME_INVITES).get();
            UserStorageService service = Sponge.getServiceManager().getRegistration(UserStorageService.class).get().getProvider();
            player.sendMessage(Text.builder("Player to join | Gamemode | Map").color(TextColors.AQUA).build());
            map.entrySet().stream().forEach(e -> {
                User user = service.get(e.getKey()).get();
                MidJoinableGame<? extends MinigameMap> game = e.getValue();
                if(game instanceof RunningLiveGame){
                    RunningLiveGame<? extends MinigameMap> lgame = (RunningLiveGame<? extends MinigameMap>) game;
                    player.sendMessage(Text.builder(user.getName() + " : " + lgame.getGamemode().getIdName() + " - ").append(lgame.getMap().getName()).color(TextColors.AQUA).build());
                    return;
                }
                if(game instanceof RunningMapMaker) {
                    player.sendMessage(Text.builder(user.getName() + " : MapMaker - ").append(game.getMap().getName()).color(TextColors.AQUA).build());
                }
            });
            return CommandResult.success();
        }
    }

    private static class InviteToMap implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<Player> opPlayer = args.getOne(PLAYER);
            if (!opPlayer.isPresent()) {
                throw new CommandException(Text.of("Source player required"));
            }
            Player player = opPlayer.get();
            Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(player);
            if (!opGame.isPresent()) {
                if (player.equals(src)) {
                    throw new CommandException(Text.of("You are not in a minigame"));
                }
                throw new CommandException(Text.of(player.getName() + " is not in a minigame"));
            }
            RunningGame<? extends MinigameMap> game = opGame.get();
            if (!(game instanceof MidJoinableGame)) {
                throw new CommandException(Text.of("Minigame is not in a state for inviting people"));
            }
            MidJoinableGame<? extends MinigameMap> joinableGame = (MidJoinableGame<? extends MinigameMap>) game;
            if (src.equals(player) && !player.hasPermission(Permissions.CMD_MINIGAME_INVITE_OVERRIDE_HOST)) {
                if ((joinableGame instanceof InvitableGame && !((InvitableGame<? extends MinigameMap>)joinableGame).isHost(player))) {
                    throw new CommandException(Text.of("You do not have permission to invite another player"));
                }
            }
            Optional<Player> opInvite = args.getOne(PLAYER_TO_INVITE);
            if (!opPlayer.isPresent()) {
                throw new CommandException(Text.of("Player to invite missing"));
            }
            Player invite = opInvite.get();
            Map<UUID, MidJoinableGame<? extends MinigameMap>> map = invite.get(MinigameKeys.MINIGAME_INVITES).get();
            map.put(player.getUniqueId(), joinableGame);
            invite.offer(MinigameKeys.MINIGAME_INVITES, map);
            if (game instanceof RunningLiveGame) {
                RunningLiveGame<? extends MinigameMap> rGame = (RunningLiveGame<? extends MinigameMap>) game;
                if (rGame instanceof RunningLobby) {
                    RunningLobby lobby = (RunningLobby) rGame;
                    String gamemodeToBe = lobby.getGamemodeToBe().isPresent() ? lobby.getGamemodeToBe().get().getIdName() : "undecided";
                    Text mapToBe = lobby.getUnreadyMapToBe().isPresent() ? lobby.getUnreadyMapToBe().get().getName() : Text.builder("Undesided").color(TextColors.RED).build();
                    invite.sendMessage(Text.builder("You have been invited to ").append(player.get(Keys.DISPLAY_NAME).get(), Text.of("'s lobby. Playing " + gamemodeToBe + " on "), mapToBe).color(TextColors.AQUA).build());
                    return CommandResult.success();
                }
                invite.sendMessage(Text.builder("You have been invited to ").append(player.get(Keys.DISPLAY_NAME).get(), Text.of("'s game. Playing " + rGame.getGamemode().getIdName() + " on ", rGame.getMap().getName())).color(TextColors.AQUA).build());
                return CommandResult.success();
            }
            invite.sendMessage(Text.builder("You have been invited to ").append(player.get(Keys.DISPLAY_NAME).get(), Text.of("'s game.")).color(TextColors.AQUA).build());
            return CommandResult.success();
        }
    }

    private static class JoinMap implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<Player> opPlayer = args.getOne(PLAYER);
            if(!opPlayer.isPresent()){
                throw new CommandException(Text.of("Unknown player joining"));
            }
            Optional<User> opJoining = args.getOne(WHOMS_GAME);
            if(!opJoining.isPresent()){
                throw new CommandException(Text.of("Unknown player to join"));
            }
            Player player = opPlayer.get();
            User joining = opJoining.get();

            //CHECK INVITES FIRST
            Map<UUID, MidJoinableGame<? extends MinigameMap>> map = player.get(MinigameKeys.MINIGAME_INVITES).get();
            MidJoinableGame<? extends MinigameMap> game = map.get(player.getUniqueId());
            if(game == null) {
                throw new CommandException(Text.of("This player has not invited you to their game."));
            }
            if(!game.contains(joining.getUniqueId())){
                map.remove(joining.getUniqueId());
                player.offer(MinigameKeys.MINIGAME_INVITES, map);
                throw new CommandException(Text.of("Your invite has expired"));
            }
            game.register(player);
            player.sendMessage(Text.of("Joining game"));
            return CommandResult.success();
        }
    }

    public static CommandSpec createLeaveCommand(){
        return CommandSpec.builder()
                .executor(new MinigameCommand.LeaveGame())
                .description(Text.of())
                .arguments(
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
    }

    public static CommandSpec createCommand(){
        CommandSpec leave = createLeaveCommand();
        CommandSpec viewInvites = CommandSpec.builder()
                .executor(new MinigameCommand.ViewInvites())
                .permission(Permissions.CMD_MINIGAME_INVITES)
                .description(Text.of("View all invites"))
                .arguments(
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
        CommandSpec invite = CommandSpec.builder()
                .executor(new MinigameCommand.InviteToMap())
                .permission(Permissions.CMD_MINIGAME_INVITE_TO_MAP)
                .description(Text.of("Invite a player to this game"))
                .arguments(
                        GenericArguments.player(PLAYER_TO_INVITE),
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
        CommandSpec join = CommandSpec.builder()
                .executor(new MinigameCommand.JoinMap())
                .permission(Permissions.CMD_MINIGAME_JOIN_MAP)
                .description(Text.of("Join a players invite"))
                .arguments(
                        GenericArguments.player(PLAYER_TO_INVITE),
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
        return CommandSpec.builder()
                .child(leave, "leave")
                .child(invite, "invite")
                .child(join, "join")
                .child(viewInvites, "invites", "view")
                .build();
    }
}
