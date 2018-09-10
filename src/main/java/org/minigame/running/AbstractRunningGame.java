package org.minigame.running;

import org.minigame.map.builder.MapBuilder;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayingMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbstractRunningGame implements RunningLiveGame<PlayingMap>{

    protected PlayingMap map;
    protected MapGamemode mapGamemode;
    protected Map<UUID, EntitySnapshot> snapshot;

    public AbstractRunningGame(ReadyToPlayMap map, MapGamemode gamemode){
        this(map, gamemode, new HashMap<>());
    }

    public AbstractRunningGame(ReadyToPlayMap map, MapGamemode gamemode, Map<UUID, EntitySnapshot> snapshot){
        this.mapGamemode = gamemode;
        this.snapshot = snapshot;
        this.map = map.generatePlayingMap(this).get();
    }

    @Override
    public PlayingMap getMap() {
        return this.map;
    }

    @Override
    public Map<UUID, EntitySnapshot> getSnapshots() {
        return this.snapshot;
    }

    @Override
    public void unregister(Player player, boolean wasKicked) {
        EntitySnapshot snapshot = getSnapshots().get(player.getUniqueId());
        this.snapshot.remove(player.getUniqueId());
        snapshot.restore();
        if(getPlayers().isEmpty()){
            MinigamePlugin.unregister(this);
            new MapBuilder(map.getLocPos1(), getMap().getUnplayable()){

                @Override
                protected void onBuilt(ReadyToPlayMap map, Object plugin) {

                }
            }.clear(MinigamePlugin.getPlugin());
        }
        if(wasKicked){
            player.sendMessage(Text.builder("You were kicked from the minigame").color(TextColors.AQUA).build());
        }
    }

    @Override
    public MapGamemode getMapGamemode() {
        return this.mapGamemode;
    }
}
