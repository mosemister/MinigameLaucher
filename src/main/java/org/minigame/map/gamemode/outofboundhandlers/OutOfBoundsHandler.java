package org.minigame.map.gamemode.outofboundhandlers;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.running.RunningGame;
import org.minigame.running.RunningLiveGame;
import org.minigame.utils.UniquieId;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

public interface OutOfBoundsHandler extends UniquieId {

    public Transform<World> onPlayerOutOfBounds(Player player, RunningLiveGame<? extends PlayableMap> game, Transform<World> current, Transform<World> to);
}
