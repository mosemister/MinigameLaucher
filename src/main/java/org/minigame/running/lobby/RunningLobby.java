package org.minigame.running.lobby;

import org.minigame.exception.GamemodeDoesNotSupportMapException;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.map.builder.MapBuilder;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.playable.PlayingMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.AbstractRunningGame;
import org.minigame.running.RunningGame;
import org.minigame.running.midjoinable.AnytimeJoinRunningGame;
import org.minigame.running.score.RunningScoreboardGame;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class RunningLobby extends AbstractRunningGame implements RunningScoreboardGame<PlayingMap>, AnytimeJoinRunningGame<PlayingMap> {

    protected UnplayableMap mapToBeUnready;
    protected PlayableMap mapToBeReady;
    protected GamemodeType gamemodeToBe;

    private final Text OBJECTIVE_NAME_LIST = Text.builder("player connection").color(TextColors.AQUA).build();
    private final Text OBJECTIVE_UNDERNAME = Text.of("highscore undername");

    public RunningLobby(PlayingMap map, LobbyMapGamemode gamemode) {
        super(map, gamemode);
    }

    public Optional<UnplayableMap> getUnreadyMapToBe(){
        return Optional.ofNullable(this.mapToBeUnready);
    }

    public Optional<PlayableMap> getReadyMapToBe(){
        return Optional.ofNullable(this.mapToBeReady);
    }

    public Optional<GamemodeType> getGamemodeToBe() {
        return Optional.ofNullable(this.gamemodeToBe);
    }

    public Optional<MapGamemode<? extends GamemodeType>> getMapGamemodeToBe(){
        if(this.gamemodeToBe == null){
            return Optional.empty();
        }
        if(this.mapToBeUnready == null){
            return Optional.empty();
        }
        return MinigamePlugin.getMapGamemode(this.gamemodeToBe, this.mapToBeUnready);
    }

    public RunningLobby setGamemodeAndMap(MapGamemode mg){
        this.mapToBeUnready = mg.getMap();
        this.gamemodeToBe = mg.getGamemode();
        this.mapToBeReady = null;
        return this;
    }

    public RunningLobby setGamemodeToBe(GamemodeType type) throws GamemodeDoesNotSupportMapException {
        if(mapToBeUnready != null && (!mapToBeUnready.isSupportedGamemode(type))){
            throw new GamemodeDoesNotSupportMapException(type, mapToBeReady);
        }
        this.gamemodeToBe = type;
        return this;
    }

    public RunningLobby setMap(PlayableMap map) throws GamemodeDoesNotSupportMapException{
        if(gamemodeToBe != null && !map.isSupportedGamemode(this.gamemodeToBe)){
            throw new GamemodeDoesNotSupportMapException(this.gamemodeToBe, map);
        }
        this.mapToBeReady = map;
        this.mapToBeUnready = map.getUnplayable();
        return this;
    }

    public RunningLobby setMap(UnplayableMap map) throws GamemodeDoesNotSupportMapException{
        if(gamemodeToBe != null && !map.isSupportedGamemode(this.gamemodeToBe)){
            throw new GamemodeDoesNotSupportMapException(this.gamemodeToBe, map);
        }
        this.mapToBeUnready = map;
        this.mapToBeReady = null;
        return this;
    }

    public void buildMap(Location<World> locToBuild, Object plugin){
        new MapBuilder(locToBuild, this.mapToBeUnready) {
            @Override
            protected void onBuilt(PlayableMap map, Object plugin) {
                RunningLobby.this.mapToBeReady = map;
            }
        }.build(plugin);
    }

    public synchronized void startMinigame(){
        if(this.mapToBeReady == null){
            System.err.println("Minigame attempted to start without a map created");
            return;
        }
        if(this.gamemodeToBe == null){
            System.err.println("Minigame attempted to start without a gamemode");
            return;
        }
        if(!this.getMapGamemodeToBe().isPresent()){
            System.err.println("Cannot find MapGamemode for " + this.gamemodeToBe.getId() + " on " + this.mapToBeUnready.getId());
            return;
        }
        MapGamemode<? extends GamemodeType> mapGamemode = getMapGamemodeToBe().get();
        Optional<Class<RunningGame<? extends GamemodeType>>> oprgClass = MinigamePlugin.getPlugin().getRunningGameClass(mapGamemode);
        if(!oprgClass.isPresent()){
            System.err.println("Cannot find a runningGame associated with " + mapGamemode.getId());
            return;
        }
        Class<RunningGame<? extends GamemodeType>> rgClass = oprgClass.get();
        Constructor<RunningGame<? extends GamemodeType>> constructor = null;
        try {
            constructor = rgClass.getConstructor(PlayingMap.class, this.gamemodeToBe.getClass(), this.snapshot.getClass());
        } catch (NoSuchMethodException e) {
            try{
                constructor = rgClass.getConstructor(PlayingMap.class, GamemodeType.class, this.snapshot.getClass());
            }catch(NoSuchMethodException e1){
                System.err.println("Could not find the constructor of " + rgClass.getSimpleName() + " (PlayingMap, " + this.gamemodeToBe.getClass() + ", " + this.snapshot.getClass().getSimpleName() + "<" + UUID.class.getSimpleName() + "," + EntitySnapshot.class.getSimpleName() + ">) in " + rgClass.getSimpleName());
                e.printStackTrace();
            }
        }
        RunningGame<? extends GamemodeType> runningGame = null;
        try {
            runningGame = constructor.newInstance(this.mapToBeReady, this.gamemodeToBe, this.snapshot);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        MinigamePlugin.register(runningGame);

    }

    @Override
    public LobbyMapGamemode getMapGamemode(){
        return (LobbyMapGamemode) super.getMapGamemode();
    }

    @Override
    public Scoreboard updateOrCreateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        board.updateDisplaySlot(createOrGetPlayerListObjective(board), DisplaySlots.LIST);
        return board;
    }

    private Objective createOrGetPlayerListObjective(Scoreboard board){
        Objective obj = board.getObjective(DisplaySlots.LIST).orElseGet(() -> {
            Objective obj2 = Objective.builder()
                    .name(this.OBJECTIVE_NAME_LIST.toPlain())
                    .displayName(this.OBJECTIVE_NAME_LIST)
                    .criterion(Criteria.DUMMY).build();
            board.addObjective(obj2);
            return obj2;
        });
        Map<Text, Score> map = obj.getScores();
        Set<Text> set = map.keySet().stream().filter(t -> Sponge.getServer().getOnlinePlayers().stream().anyMatch(p -> p.get(Keys.DISPLAY_NAME).get().toPlain().equals(t.toPlain()))).collect(Collectors.toSet());
        map.keySet().stream().filter(t -> set.stream().anyMatch(s -> s.toPlain().equals(t.toPlain()))).forEach(t -> map.remove(t));
        Sponge.getServer().getOnlinePlayers().stream().filter(p -> !map.keySet().contains(p.getDisplayNameData().displayName().get())).forEach(p -> map.put(p.getDisplayNameData().displayName().get(), obj.getOrCreateScore(p.getDisplayNameData().displayName().get())));
        map.entrySet().stream().forEach(e -> Sponge.getServer().getOnlinePlayers().stream().filter(p -> p.get(Keys.DISPLAY_NAME).get().toPlain().equals(e.getKey().toPlain())).forEach(p -> e.getValue().setScore(p.getConnection().getLatency())));
        return obj;
    }
}
