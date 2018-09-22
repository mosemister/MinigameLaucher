package org.minigame.plugin.command.commands;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.exception.GamemodeDoesNotSupportSpawnProp;
import org.minigame.gamemode.GamemodeType;
import org.minigame.map.maker.MapMaker;
import org.minigame.map.maker.MapMakerBuilder;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.types.UserSpawnProp;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.plugin.command.arguments.MinigameIDArgument;
import org.minigame.plugin.command.arguments.RunningPositionArgument;
import org.minigame.running.mapmaking.RunningMapMaker;
import org.minigame.utils.Permissions;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class MapMakerCommand {

    private static final Text POSITION = Text.of("XYZ");
    private static final Text ROTATION = Text.of("rotation");
    private static final Text PLAYER = Text.of("player");
    private static final Text NAME = Text.of("MapName");
    private static final Text GAMEMODE = Text.of("Gamemode");

    public static class CreateMap implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!MinigamePlugin.getRunningGames(RunningMapMaker.class).isEmpty()) {
                throw new CommandException(Text.of("A map is already being made"));
            }
            Text name = args.<Text>getOne(NAME).get();
            GamemodeType type = args.<GamemodeType>getOne(GAMEMODE).get();
            Optional<Player> opPlayer = args.getOne(PLAYER);
            if (!opPlayer.isPresent()) {
                throw new CommandException(Text.of("A player is required"));
            }
            MapMaker map = new MapMakerBuilder().setName(name).setGamemode(type).build();
            Location<World> loc = map.getLocPos1();
            loc.setBlockType(BlockTypes.DIRT);
            RunningMapMaker runningMM = new RunningMapMaker(map);
            Player player = opPlayer.get();
            runningMM.register(player);
            runningMM.addHosts(player);
            MinigamePlugin.register(runningMM);
            player.offer(Keys.GAME_MODE, GameModes.CREATIVE);
            player.setLocation(loc.copy().add(new Vector3i(0, 0, 0)));
            return CommandResult.success();
        }
    }

    public interface RegisterCMD extends CommandExecutor {

        class PlayerSpawnPoint implements RegisterCMD {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                Vector3d pos = args.<Vector3d>getOne(MapMakerCommand.POSITION).get();
                double rotation = args.<Double>getOne(MapMakerCommand.ROTATION).get();
                Optional<Player> opPlayer = args.getOne(MapMakerCommand.PLAYER);
                if (!opPlayer.isPresent()) {
                    throw new CommandException(Text.of("Unknown player"));
                }
                Player player = opPlayer.get();
                Optional<RunningMapMaker> opGame = MinigamePlugin.getRunningGames(RunningMapMaker.class).stream().filter(r -> r.getPlayers().contains(player)).findFirst();
                if (opGame.isPresent()) {
                    throw new CommandException(Text.of("You need to be in a mapmaker to run this command"));
                }
                RunningMapMaker game = opGame.get();

                try {
                    UserSpawnProp prop = game.getMap().registerProp(pos, new Vector3d(rotation, 0, 0), PropType.USER_SPAWN);
                    prop.createEntityForMapMaker(game.getMap());
                } catch (GamemodeDoesNotSupportSpawnProp e) {
                    throw new CommandException(Text.of(e.getMessage()));
                }
                return CommandResult.success();
            }
        }

    }

    private static CommandSpec createRegisterCommand() {
        CommandSpec userSpawnPropCMD = CommandSpec.builder()
                .executor(new RegisterCMD.PlayerSpawnPoint())
                .description(Text.of("register a user spawn point"))
                .permission(Permissions.CMD_MAPMAKER_REGISTER_USER_SPAWN_PROP)
                .arguments(
                        new RunningPositionArgument(POSITION),
                        GenericArguments.doubleNum(ROTATION),
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
        return CommandSpec.builder()
                .child(userSpawnPropCMD, "userspawn")
                .children(MinigamePlugin.getPlugin().getCommandMapmakerRegisterAdditions())
                .build();
    }

    public static CommandCallable createCommand() {
        CommandSpec createMapCMD = CommandSpec.builder()
                .executor(new CreateMap())
                .description(Text.of("Create a map"))
                .permission(Permissions.CMD_MAPMAKER_CREATE_MAP)
                .arguments(
                        GenericArguments.text(NAME, TextSerializers.FORMATTING_CODE, false),
                        new MinigameIDArgument<>(GAMEMODE, GamemodeType.class),
                        GenericArguments.playerOrSource(PLAYER)
                ).build();
        CommandSpec registerCMD = createRegisterCommand();
        CommandSpec leaveCMD = MinigameCommand.createLeaveCommand();
        return CommandSpec.builder()
                .child(createMapCMD, "createmap", "create")
                .child(registerCMD, "register", "reg")
                .child(leaveCMD, "leave", "kick")
                .build();
    }


}
