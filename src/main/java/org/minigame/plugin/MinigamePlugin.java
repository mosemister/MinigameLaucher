package org.minigame.plugin;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.data.MinigameKeys;
import org.minigame.data.invite.PlayerInviteData;
import org.minigame.data.invite.PlayerInviteDataBuilder;
import org.minigame.data.invite.PlayerInviteImmutableData;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.LobbyListener;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.PositionableMap;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.command.commands.CommandAdditions;
import org.minigame.plugin.command.commands.LobbyCommand;
import org.minigame.plugin.command.commands.MapMakerCommand;
import org.minigame.plugin.command.commands.MinigameCommand;
import org.minigame.running.RunningGame;
import org.minigame.running.RunningGameBuilder;
import org.minigame.utils.MinigameWorldGenerator;
import org.minigame.utils.UniquieId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Plugin(
        id = MinigamePlugin.PLUGIN_ID,
        name = MinigamePlugin.PLUGIN_NAME,
        version = MinigamePlugin.PLUGIN_VERSION,
        description = MinigamePlugin.PLUGIN_DESCRIPTION
)
public class MinigamePlugin {

    public static final String PLUGIN_ID = "minigameloader";
    public static final String PLUGIN_NAME = "Minigame Loader";
    public static final String PLUGIN_VERSION = "SNAPSHOT-0.1";
    public static final String PLUGIN_DESCRIPTION = "A vast plugin that makes minigame plugin development quicker";

    private World minigamesWorld;
    private UnplayableMap defaultLobbyMap;

    @Inject
    private PluginContainer container;

    private static final Set<RunningGame<? extends MinigameMap>> RUNNING_GAMES = new HashSet<>();
    private static final Map<GamemodeType, RunningGameBuilder<? extends RunningGame<? extends PlayableMap>>> RUNNING_GAME_BUILDERS = new HashMap<>();
    private static final Set<CommandAdditions> COMMAND_MAPMAKER_REGISTER_ADDITIONS = new HashSet<>();
    private static final Set<UniquieId> IDS = new HashSet<>();
    private static final Map<GamemodeType, Class<? extends MapGamemode<? extends GamemodeType>>> GAMEMODE_MAP = new HashMap<>();
    private static MinigamePlugin plugin;

    @Listener
    public void onServerStart(GamePreInitializationEvent event) {
        plugin = this;
        MinigameKeys.init();
        DataRegistration.builder()
                .dataName("Invite Data")
                .manipulatorId("invites")
                .dataClass(PlayerInviteData.class)
                .immutableClass(PlayerInviteImmutableData.class)
                .builder(new PlayerInviteDataBuilder())
                .buildAndRegister(this.getContainer());
        registerIds();
    }

    @Listener
    public void OnServerStarted(GameStartedServerEvent event){
        EventManager manager = Sponge.getEventManager();
        manager.registerListeners(this, new MinigameListener());
        manager.registerListeners(this, new LobbyListener());
        CommandManager cmdManager = Sponge.getCommandManager();
        cmdManager.register(this, MapMakerCommand.createCommand(), "mapmaker", "map");
        cmdManager.register(this, MinigameCommand.createCommand(), "minigame", "game");
        cmdManager.register(this, LobbyCommand.createCommand(), "lobby");

        try {
            this.minigamesWorld = MinigameWorldGenerator.loadOrCreateWorld(MinigameWorldGenerator.DEFAULT_MINIGAME_WORLD_NAME);
        } catch (IOException e) {
            System.err.println("Failed to create or load the world 'Minigame' ");
            e.printStackTrace();
        }
    }

    public World getMinigamesWorld(){
        return this.minigamesWorld;
    }

    public UnplayableMap getDefaultLobbyMap(){
        return this.defaultLobbyMap;
    }

    private void registerIds(){
        register(
                DefaultRegisters.LOBBY_GAMEMODE,
                DefaultRegisters.DEFAULT_LOBBY_MAP,
                DefaultRegisters.LOBBY_SPAWN_REQUIREMENT,
                DefaultRegisters.ORDERED_TEAM_SPLITTER,
                DefaultRegisters.DEFAULT_LOBBY_MAP_GAMEMODE
        );
        this.defaultLobbyMap = DefaultRegisters.DEFAULT_LOBBY_MAP;
    }

    public PluginContainer getContainer(){
        return this.container;
    }

    public Map<List<String>, ? extends CommandSpec> getCommandMapmakerRegisterAdditions(){
        Map<List<String>, ? extends CommandSpec> map = new HashMap<>();
        COMMAND_MAPMAKER_REGISTER_ADDITIONS.stream().forEach(ca -> map.put(Arrays.asList(ca.getAliances()), ca.getSpec()));
        return map;
    }

    public Optional<RunningGameBuilder<? extends RunningGame<? extends PlayableMap>>> getRunningGameBuilder(GamemodeType gamemode) {
        RunningGameBuilder<? extends RunningGame<? extends PlayableMap>> class2 = RUNNING_GAME_BUILDERS.get(gamemode);
        if(class2 == null){
            return Optional.empty();
        }
        return Optional.of(class2);
    }

    public static MinigamePlugin getPlugin(){
        return plugin;
    }

    public static void register(UniquieId... ids){
        for(UniquieId id : ids){
            register(id);
        }
    }

    public static void register(UniquieId id){
        if(id.getId().contains(" ")){
            throw new IllegalArgumentException("Id of '" + id + "' cannot contain any spaces");
        }
        if(!id.getId().startsWith(id.getPlugin().getId())){
            throw new IllegalArgumentException("Id of '" + id + "' does not start with the plugin id");
        }
        if (!id.getId().startsWith(id.getPlugin().getId() + ":")) {
            throw new IllegalArgumentException("Id of '" + id + "' does not contain ':' after the plugin id");
        }
        IDS.add(id);
    }

    public static <G extends GamemodeType> void register(G type, Class<? extends MapGamemode<G>> class1){
        GAMEMODE_MAP.put(type, class1);
    }

    public static void register(RunningGame<? extends MinigameMap> game){
        RUNNING_GAMES.add(game);
    }

    public static void register(GamemodeType gamemode, RunningGameBuilder<? extends RunningGame<? extends PlayableMap>> gameBuilder) {
        RUNNING_GAME_BUILDERS.put(gamemode, gameBuilder);
    }

    public static void unregister(RunningGame<? extends MinigameMap> game){
        RUNNING_GAMES.remove(game);
    }

    public static void registerMapMakerChildProp(CommandAdditions addition){
        COMMAND_MAPMAKER_REGISTER_ADDITIONS.add(addition);
    }

    public static Vector3i getNextOpenSpaceForMapMaking(Vector3i mapSize) {
        Vector3i starting = MinigameWorldGenerator.MINIGAME_MAP_CREATOR_STARTING_POSITION;
        for (RunningGame<? extends MinigameMap> game : getRunningGames()) {
            MinigameMap map = game.getMap();
            if (!(map instanceof PlayableMap)) {
                continue;
            }
            PlayableMap playableMap = (PlayableMap) map;
            Vector3i pos1 = playableMap.getPos1();
            Vector3i pos2 = playableMap.getPos2();
            if (starting.distance(pos1) < mapSize.getX()) {
                starting = new Vector3i(pos2.getX() + 1, starting.getY(), starting.getZ());
                continue;
            }
            break;
        }
        return starting;
    }

    public static Vector3i getNextOpenSpaceForPlayingMap(Vector3i mapSize){
        Vector3i starting = MinigameWorldGenerator.MINIGAME_MAP_PLAYING_STARTING_POSITION;
        for(RunningGame<? extends MinigameMap> game : getRunningGames()){
            System.out.println("\t Current starting is: " + starting.getX() + ", " + starting.getY() + ", " + starting.getZ());
            System.out.println("\t\t - " + game.getMap().getName().toPlain());
            MinigameMap map = game.getMap();
            if(!(map instanceof PlayableMap)){
                continue;
            }
            PlayableMap playableMap = (PlayableMap) map;
            Vector3i pos1 = playableMap.getPos1();
            Vector3i pos2 = playableMap.getPos2();
            float distance = starting.distance(pos1);
            System.out.println("\t\t\t :- Distance " + distance + " Map size X: " + mapSize.getX() + " Pos 1 X: " + pos1.getX() + " Pos 2 X: " + pos2.getX() + " Map: " + playableMap.getClass().getName());
            if (distance < mapSize.getX()) {
                System.out.println("\t\tskipped");
                starting = new Vector3i(pos2.getX() + 1, starting.getY(), starting.getZ());
                continue;
            }
            break;
        }
        System.out.println("\t Returning starting as " + starting.getX() + ", " + starting.getY() + ", " + starting.getZ());
        return starting;
    }

    public <G extends GamemodeType> Optional<MapGamemode<G>> createMapGamemode(G gamemode, UnplayableMap map){
        Class<? extends MapGamemode<? extends GamemodeType>> class1 = GAMEMODE_MAP.get(gamemode);
        if(class1 == null){
            return Optional.empty();
        }
        try {
            Constructor<? extends MapGamemode<? extends GamemodeType>> constructor = class1.getConstructor(UnplayableMap.class, gamemode.getClass());
            MapGamemode<? extends GamemodeType> mapGamemode = constructor.newInstance(map, gamemode);
            MapGamemode<G> castedMapGamemode = (MapGamemode<G>) mapGamemode;
            return Optional.of(castedMapGamemode);
        } catch (NoSuchMethodException e) {
            System.err.println("MinigamePlugin.createMapGamemode(GamemodeType, UnplayableMap) failed due to the fact that " + class1.getName() + " does not have a constructor with the following: " + class1.getSimpleName() + "(UnplayableMap map, " + gamemode.getClass().getSimpleName() + " gamemode). If this is the wrong gamemode, remove the register for this gamemode map combine");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("MinigamePlugin.createMapGamemode(GamemodeType, UnplayableMap) failed due to the fact that " + class1.getName() + "(UnplayableMap, " + gamemode.getClass() + ") constructor is not public");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Optional.empty();

    }

    public static Set<RunningGame<? extends MinigameMap>> getRunningGames(){
        return RUNNING_GAMES;
    }

    public static <M extends RunningGame<? extends MinigameMap>> Set<M> getRunningGames(Class<M> class1){
        return (Set<M>)getRunningGames().stream().filter(f -> class1.isInstance(f)).collect(Collectors.toSet());
    }

    public static Optional<RunningGame<? extends MinigameMap>> getRunningGame(Locatable loc){
        if(loc instanceof Player){
            return getRunningGame((Player)loc);
        }
        return getRunningGame(loc.getLocation().getPosition());
    }

    public static Optional<RunningGame<? extends MinigameMap>> getRunningGame(UUID uuid){
        return getRunningGames().stream().filter(rg -> rg.getSnapshots().keySet().stream().anyMatch(s -> s.equals(uuid))).findFirst();
    }

    public static Optional<RunningGame<? extends MinigameMap>> getRunningGame(Vector3d vector){
        return getRunningGames().stream().filter(f -> f.getMap() instanceof PositionableMap).filter(f -> ((PositionableMap)f.getMap()).isLocationPartOfMap(vector)).findFirst();
    }

    public static Optional<RunningGame<? extends MinigameMap>> getRunningGame(Player player){
        return getRunningGame((User)player);
    }

    public static Optional<RunningGame<? extends MinigameMap>> getRunningGame(User player){
        return getRunningGame(player.getUniqueId());
    }

    public static Set<UniquieId> getUniquie(){
        return IDS;
    }

    public static <T extends UniquieId> Set<T> getUniquieSet(Class<T> class1){
        return (Set<T>) getUniquie().stream().filter(f -> class1.isInstance(f)).collect(Collectors.toSet());
    }

    public static <T extends UniquieId> Optional<T> getUniquie(String id){
        return (Optional<T>) getUniquie().stream().filter(f -> f.getId().equals(id)).findFirst();
    }

    public static <T extends UniquieId> Optional<T> getUniquie(Class<T> class1){
        Iterator<T> iter = getUniquieSet(class1).iterator();
        if(iter.hasNext()){
            return Optional.of(iter.next());
        }
        return Optional.empty();
    }

    public static <T extends GamemodeType> Optional<MapGamemode<T>> getMapGamemodeCast(T gamemode, UnplayableMap map){
        Optional<MapGamemode<? extends GamemodeType>> opMap = getMapGamemode(gamemode, map);
        if(opMap.isPresent()){
            return Optional.of((MapGamemode<T>)opMap.get());
        }
        return Optional.empty();
    }

    public static Optional<MapGamemode<? extends GamemodeType>> getMapGamemode(GamemodeType gamemode, UnplayableMap map){
        Optional<MapGamemode> opMap = getUniquieSet(MapGamemode.class).stream().filter(m -> m.getGamemode().equals(gamemode)).filter(m -> m.getMap().equals(map)).findAny();
        if(opMap.isPresent()){
            return Optional.of(opMap.get());
        }
        return Optional.empty();
    }
}
