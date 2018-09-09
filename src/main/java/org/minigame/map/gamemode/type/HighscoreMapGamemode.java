package org.minigame.map.gamemode.type;

import org.minigame.map.gamemode.MapGamemode;
import org.spongepowered.api.entity.living.player.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface HighscoreMapGamemode extends MapGamemode {

    Map<UUID, Integer> getScores();

    default Optional<Integer> getScore(UUID uuid){
        return Optional.ofNullable(getScores().get(uuid));
    }

    default Optional<Integer> getScore(User user){
        return getScore(user.getUniqueId());
    }
}
