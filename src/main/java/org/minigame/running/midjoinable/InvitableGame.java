package org.minigame.running.midjoinable;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.User;

import java.util.Set;
import java.util.UUID;

public interface InvitableGame<M extends MinigameMap> extends MidJoinableGame<M> {

    void addHosts(UUID... uuid);

    default void addHosts(Entity... entities){
        UUID[] uuids = new UUID[entities.length];
        for(int A = 0; A < uuids.length; A++){
            uuids[A] = entities[A].getUniqueId();
        }
        addHosts(uuids);
    }

    Set<UUID> getHosts();

    default boolean isHost(UUID uuid){
        return getHosts().stream().anyMatch(id -> id.equals(uuid));
    }

    default boolean isHost(User user){
        return isHost(user.getUniqueId());
    }

}
