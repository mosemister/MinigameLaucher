package org.minigame.running;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public interface RunningGame <T extends MinigameMap> {

    public T getMap();
    public Map<UUID, EntitySnapshot> getSnapshots();
    public void unregister(Player player, boolean wasKicked);

    public default boolean contains(UUID uuid){
        return getSnapshots().keySet().stream().anyMatch(u -> u.equals(uuid));
    }

    public default Set<Player> getPlayers(){
        Set<Player> ret = new HashSet<>();
        Set<UUID> offline = new HashSet<>();
        getSnapshots().keySet().stream().forEach(u -> {
            Optional<Player> opPlayer = Sponge.getServer().getPlayer(u);
            if(opPlayer.isPresent()){
                ret.add(opPlayer.get());
                return;
            }
            offline.add(u);
        });
        return ret;
    }
}
