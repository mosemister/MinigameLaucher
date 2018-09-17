package org.minigame.running.lobby;

import org.minigame.exception.GamemodeDoesNotSupportMapException;
import org.minigame.exception.RunnableNotFoundException;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.map.builder.MapBuilder;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.playable.PlayingMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.AbstractRunningGame;
import org.minigame.running.RunningGame;
import org.minigame.running.RunningGameBuilder;
import org.minigame.running.RunningLiveGame;
import org.minigame.running.midjoinable.AnytimeJoinRunningGame;
import org.minigame.running.score.RunningScoreboardGame;
import org.minigame.team.LobbyGroup;
import org.minigame.team.Party;
import org.minigame.team.Team;
import org.minigame.team.splitter.TeamSplitter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class RunningLobby extends AbstractRunningGame implements RunningScoreboardGame<PlayingMap>, AnytimeJoinRunningGame.LiveMidJoinableGame<PlayingMap> {

    protected UnplayableMap mapToBeUnready;
    protected ReadyToPlayMap mapToBeReady;
    protected GamemodeType gamemodeToBe;
    protected ServerBossBar bar;

    private final Text OBJECTIVE_NAME_LIST = Text.builder("player connection").color(TextColors.AQUA).build();
    private final Text OBJECTIVE_UNDERNAME = Text.of("highscore undername");

    public RunningLobby(ReadyToPlayMap map, LobbyMapGamemode gamemode) {
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

    public void displayMinigameInformation() {
        if (bar != null) {
            bar.removePlayers(bar.getPlayers());
        }
        String gamemode = "Unknown";
        String map = "Unknown";
        if (getGamemodeToBe().isPresent()) {
            gamemode = getGamemodeToBe().get().getIdName();
        }
        if (getUnreadyMapToBe().isPresent()) {
            map = getUnreadyMapToBe().get().getIdName();
        }
        bar = ServerBossBar.builder().overlay(BossBarOverlays.PROGRESS).percent(0).color(BossBarColors.GREEN).name(Text.of("MinigameInfo: Gamemode: " + gamemode + " Map: " + map)).build();
        bar.addPlayers(getPlayers());
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

    public RunningLobby setGamemodeAndMap(MapGamemode mg) throws RunnableNotFoundException {
        if (!MinigamePlugin.getPlugin().getRunningGameBuilder(mg.getGamemode()).isPresent()) {
            throw new RunnableNotFoundException(mg.getGamemode());
        }
        this.mapToBeUnready = mg.getMap();
        this.gamemodeToBe = mg.getGamemode();
        this.mapToBeReady = null;
        return this;
    }

    public RunningLobby setGamemodeToBe(GamemodeType type) throws GamemodeDoesNotSupportMapException, RunnableNotFoundException {
        if(mapToBeUnready != null && (!mapToBeUnready.isSupportedGamemode(type))){
            throw new GamemodeDoesNotSupportMapException(type, mapToBeReady);
        }
        if (!MinigamePlugin.getPlugin().getRunningGameBuilder(type).isPresent()) {
            throw new RunnableNotFoundException(type);
        }
        this.gamemodeToBe = type;
        return this;
    }

    public RunningLobby setMap(ReadyToPlayMap map) throws GamemodeDoesNotSupportMapException {
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

    public void buildMap(Location<World> locToBuild, Object plugin, Consumer<ReadyToPlayMap> process) {
        new MapBuilder(locToBuild, this.mapToBeUnready) {
            @Override
            protected void onBuilt(ReadyToPlayMap map, Object plugin) {
                RunningLobby.this.mapToBeReady = map;
                process.accept(map);

            }
        }.build(plugin);
    }

    public Collection<Team> sortTeams(RunningLiveGame<? extends PlayableMap> game) {
        TeamSplitter splitter = game.getTeamSplitter();
        System.out.println("TeamSplitter: " + splitter.getId());
        Set<Party> parties = new HashSet<>();
        getPlayers().stream().forEach(p -> parties.add(new Party(p)));
        System.out.println("Parties: " + parties.size());
        LobbyGroup lobbyGroup = new LobbyGroup();
        parties.stream().forEach(p -> lobbyGroup.addParties(p));
        System.out.println("Lobbygroup: " + lobbyGroup.getParties().size());
        return game.splitTeams(splitter, lobbyGroup);
    }

    public void startMinigameForceSync() {
        Sponge.getScheduler().createTaskBuilder().execute(() -> startMinigame()).submit(MinigamePlugin.getPlugin());
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
        Optional<RunningGameBuilder<? extends RunningGame<? extends PlayableMap>>> oprgClass = MinigamePlugin.getPlugin().getRunningGameBuilder(this.gamemodeToBe);
        if(!oprgClass.isPresent()){
            System.err.println("Cannot find a runningGame associated with " + mapGamemode.getId());
            return;
        }
        this.bar.removePlayers(this.bar.getPlayers());
        RunningGameBuilder<? extends RunningGame<? extends PlayableMap>> rgClass = oprgClass.get();
        RunningLiveGame<? extends PlayableMap> game = rgClass.createGame(this.mapToBeReady, getMapGamemodeToBe().get(), this.snapshot);
        MinigamePlugin.register(game);
        System.out.println("Sorting teams");
        Collection<Team> teams = sortTeams(game);
        System.out.println("Initializing");
        game.initate(teams);
        System.out.println("Players deregistered from lobby");
        this.snapshot = new HashMap<>();
        System.out.println("spawning players");
        game.spawnPlayers();
        System.out.println("starting game");
        game.start();
        new MapBuilder(getMap()) {
            @Override
            protected void onBuilt(ReadyToPlayMap map, Object plugin) {

            }
        }.clear(MinigamePlugin.getPlugin());
    }

    @Deprecated
    @Override
    public void initate(Collection<Team> teams) {

    }

    @Deprecated
    @Override
    public Collection<Team> splitTeams(TeamSplitter splitter, LobbyGroup group) {
        return null;
    }

    @Deprecated
    @Override
    public TeamSplitter getTeamSplitter() {
        return null;
    }

    @Deprecated
    @Override
    public void spawnPlayers() {

    }

    @Deprecated
    @Override
    public void start() {
        startMinigameForceSync();
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
