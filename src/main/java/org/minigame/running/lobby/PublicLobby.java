package org.minigame.running.lobby;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.exception.GamemodeDoesNotSupportMapException;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayingMap;
import org.minigame.map.truemap.unplayable.UnreadyMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.midjoinable.AnytimeJoinRunningGame;
import org.minigame.utils.MinigameWorldGenerator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PublicLobby extends RunningLobby implements AnytimeJoinRunningGame<PlayingMap> {

    protected int timeToVoteForGamemode;
    protected TimeUnit timeUnitToVoteForGamemode;
    protected int timeToJoin;
    protected TimeUnit timeUnitToJoin;
    protected int timeToVoteForMap;
    protected TimeUnit timeUnitToVoteForMap;
    protected boolean canAcceptMorePlayers = true;
    protected boolean secondVote;

    protected Map<GamemodeType, Set<UUID>> gamemodeVotes = new HashMap<>();
    protected Map<MapGamemode, Set<UUID>> mapVotes = new HashMap<>();

    public PublicLobby(PlayingMap map,
                       LobbyMapGamemode gamemode,
                       TimeUnit timeUnitToVoteForGamemode,
                       int timeToVoteForGamemode,
                       TimeUnit timeUnitToJoin,
                       int timeToJoin,
                       TimeUnit timeUnitToVoteForMap,
                       int timeToVoteForMap,
                       Collection<GamemodeType> collection) {
        super(map, gamemode);
        this.timeToJoin = timeToJoin;
        this.timeUnitToJoin = timeUnitToJoin;
        this.timeToVoteForGamemode = timeToVoteForGamemode;
        this.timeUnitToVoteForGamemode = timeUnitToVoteForGamemode;
        this.timeToVoteForMap = timeToVoteForMap;
        this.timeUnitToVoteForMap = timeUnitToVoteForMap;
        collection.stream().forEach(g -> gamemodeVotes.put(g, new HashSet<>()));
    }

    public TimeUnit getTimeUnitToJoin() {
        return timeUnitToJoin;
    }

    public TimeUnit getTimeUnitToVoteForGamemode(){
        return timeUnitToVoteForGamemode;
    }

    public TimeUnit getTimeUnitToVoteForMap(){
        return timeUnitToVoteForMap;
    }

    public int getTimeToJoin(){
        return timeToJoin;
    }

    public int getTimeToVoteForGamemode(){
        return timeToVoteForGamemode;
    }

    public int getTimeToVoteForMap(){
        return timeToVoteForMap;
    }

    public boolean canAcceptAnyMorePlayers(){
        return canAcceptMorePlayers;
    }

    public boolean vote(GamemodeType type, UUID... uuids){
        Set<UUID> set = this.gamemodeVotes.get(type);
        if(set == null){
            return false;
        }
        for(UUID uuid : uuids){
            this.gamemodeVotes.values().stream().filter(c -> c.stream().anyMatch(id -> id.equals(uuid))).forEach(c -> c.remove(uuid));
            set.add(uuid);
        }
        return true;
    }

    public boolean vote(MapGamemode map, UUID... uuids){
        Set<UUID> set = this.mapVotes.get(map);
        if(set == null){
            return false;
        }
        for(UUID uuid : uuids){
            this.mapVotes.values().stream().filter(c -> c.stream().anyMatch(id -> id.equals(uuid))).forEach(c -> c.remove(uuid));
            set.add(uuid);
        }
        return true;
    }

    public Set<GamemodeType> getGamemodeOptionsForVoting(){
        return this.gamemodeVotes.keySet();
    }

    public Set<MapGamemode> getMapOptionsForVoting(){
        return this.mapVotes.keySet();
    }

    public Set<MapGamemode> getMapVoteResult(){
        int maxVotes = 0;
        Set<MapGamemode> relatedMaps = new HashSet<>();
        for(Map.Entry<MapGamemode, Set<UUID>> entry : this.mapVotes.entrySet()){
            if(entry.getValue().size() > maxVotes){
                maxVotes = entry.getValue().size();
                relatedMaps.clear();
                relatedMaps.add(entry.getKey());
            }
            if(entry.getValue().size() == maxVotes){
                relatedMaps.add(entry.getKey());
            }
        }
        return relatedMaps;
    }

    public Set<GamemodeType> getGamemodeVoteResult(){
        int maxVotes = 0;
        Set<GamemodeType> relatedGamemodes = new HashSet<>();
        for(Map.Entry<GamemodeType, Set<UUID>> entry : this.gamemodeVotes.entrySet()){
            if(entry.getValue().size() > maxVotes){
                maxVotes = entry.getValue().size();
                relatedGamemodes.clear();
                relatedGamemodes.add(entry.getKey());
            }
            if(entry.getValue().size() == maxVotes){
                relatedGamemodes.add(entry.getKey());
            }
        }
        return relatedGamemodes;
    }

    private void activateGamemodeVote(){
        Sponge.getScheduler().createTaskBuilder()
                .async()
                .delay(this.timeToVoteForMap, this.timeUnitToVoteForMap)
                .execute(() -> processGamemodeVoteResult())
                .submit(MinigamePlugin.getPlugin());
    }

    private void activateMapVote() {
        Sponge.getScheduler().createTaskBuilder()
                .async()
                .delay(this.timeToVoteForGamemode, this.timeUnitToVoteForGamemode)
                .execute(() -> processMapVoteResult())
                .submit(MinigamePlugin.getPlugin());
    }

    private void actvateWait(){
        Sponge.getScheduler().createTaskBuilder()
                .async()
                .delay(this.timeToJoin, this.timeUnitToJoin)
                .execute(() -> processWait())
                .submit(MinigamePlugin.getPlugin());
    }

    private void processMapVoteResult(){
        Set<MapGamemode> votes = getMapVoteResult();
        if(votes.isEmpty()){
            activateGamemodeVote();
            return;
        }
        MapGamemode type = votes.iterator().next();
        if(votes.size() >= 2){
            if(!this.secondVote) {
                this.mapVotes.clear();
                votes.forEach(v -> this.mapVotes.put(v, new HashSet<>()));
                activateGamemodeVote();
                this.secondVote = true;
                return;
            }
        }
        setGamemodeAndMap(type);
        Vector3i pos = MinigamePlugin.getNextOpenSpaceForPlayingMap(type.getMap().getMinimumSize());
        Location<World> loc = MinigamePlugin.getPlugin().getMinigamesWorld().getLocation(pos);
        buildMap(loc, MinigamePlugin.getPlugin());
        startMinigame();

    }

    private void processWait(){
        this.canAcceptMorePlayers = false;
        Set<MapGamemode> set = this.mapVotes.keySet().stream().filter(k -> k.isSuitable(this)).collect(Collectors.toSet());
        this.mapVotes.keySet().stream().filter(m -> set.stream().noneMatch(m1 -> m1.equals(m))).forEach(m -> this.mapVotes.remove(m));
        activateMapVote();
    }

    private void processGamemodeVoteResult(){
        Set<GamemodeType> votes = getGamemodeVoteResult();
        if(votes.isEmpty()){
            activateGamemodeVote();
            return;
        }
        GamemodeType type = votes.iterator().next();
        if(votes.size() >= 2){
            if(!this.secondVote) {
                this.gamemodeVotes.clear();
                votes.forEach(v -> this.gamemodeVotes.put(v, new HashSet<>()));
                activateGamemodeVote();
                this.secondVote = true;
                return;
            }
        }
        try {
            setGamemodeToBe(type);
        } catch (GamemodeDoesNotSupportMapException e) {
            e.printStackTrace();
        }
        type.getSupportedMapDetails().stream().forEach(md -> this.mapVotes.put(md, new HashSet<>()));
        actvateWait();
    }
}
