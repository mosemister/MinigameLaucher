package org.minigame.map.gamemode.outofboundhandlers;

import com.flowpowered.math.vector.Vector3d;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.requirement.spawn.types.UserSpawnProp;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningLiveGame;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;

import java.util.Set;

public class ClosestSpawnLocationOutOfBoundHandler implements OutOfBoundsHandler {
    @Override
    public Transform<World> onPlayerOutOfBounds(Player player, RunningLiveGame<? extends PlayableMap> game, Transform<World> current, Transform<World> to) {
        Set<UserSpawnProp> spawns = game.getMapGamemode().getProps(PropType.USER_SPAWN);
        Vector3d pPos = current.getPosition();
        Vector3d vector3d = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        double distance = pPos.distance(vector3d);
        for(UserSpawnProp prop : spawns){
            double dis = prop.getPosition().distance(pPos);
            if(dis < distance){
                vector3d = prop.getPosition();
                distance = dis;
            }
        }
        return to.setPosition(vector3d);
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public String getIdName() {
        return "closest_spawn_location";
    }
}
