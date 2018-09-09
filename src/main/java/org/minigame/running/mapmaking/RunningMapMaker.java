package org.minigame.running.mapmaking;

import org.minigame.map.builder.MapBuilder;
import org.minigame.map.maker.MapMaker;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.midjoinable.InvitableGame;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class RunningMapMaker implements InvitableGame<MapMaker> {

    MapMaker maker;
    Set<UUID> host = new HashSet<>();
    Map<UUID, EntitySnapshot> map = new HashMap<>();

    public RunningMapMaker(MapMaker maker){
        this.maker = maker;
    }

    @Override
    public void addHosts(UUID... uuids){
        for(UUID uuid : uuids){
            this.host.add(uuid);
        }
    }

    @Override
    public Set<UUID> getHosts() {
        return this.host;
    }

    @Override
    public MapMaker getMap() {
        return this.maker;
    }

    @Override
    public Map<UUID, EntitySnapshot> getSnapshots() {
        return this.map;
    }

    @Override
    public void unregister(Player player, boolean wasKicked) {
        EntitySnapshot snapshot = getSnapshots().get(player.getUniqueId());
        this.map.remove(player.getUniqueId());
        snapshot.restore();
        if(getPlayers().isEmpty()){
            //SAVE MAP AND DECONTRUCT
            /*new MapBuilder(getMap().getLocPos1(), ){

                @Override
                protected void onBuilt(PlayableMap map, Object plugin) {

                }
            }.clear(MinigamePlugin.getPlugin());*/
            MinigamePlugin.unregister(this);
        }
        if(wasKicked){
            player.sendMessage(Text.builder("You were kicked from the minigame").color(TextColors.AQUA).build());
        }
    }
}
