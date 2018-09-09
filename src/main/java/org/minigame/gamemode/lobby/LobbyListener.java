package org.minigame.gamemode.lobby;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningGame;
import org.minigame.running.mapmaking.RunningMapMaker;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.Optional;

public class LobbyListener {

    @Listener
    public void onPlayerDamage(DamageEntityEvent event){
        Entity entity = event.getTargetEntity();
        Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(entity);
        if(!opGame.isPresent()){
            return;
        }
        RunningGame<? extends MinigameMap> game = opGame.get();
        if(!(game instanceof RunningMapMaker)) {
            return;
        }
        event.setCancelled(true);
    }

    @Listener
    public void onEntitySpawn(ConstructEntityEvent.Pre event){
        cancelIfPresent(event, MinigamePlugin.getRunningGame(event.getTransform().getPosition()));
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event){
        onBlockChange(event);
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event){
        cancelIfPresent(event, MinigamePlugin.getRunningGame(event.getTransactions().get(0).getDefault().getPosition().toDouble()));
    }

    private void cancelIfPresent(Cancellable event, Optional<RunningGame<? extends MinigameMap>> opGame){
        if(!opGame.isPresent()){
            return;
        }
        RunningGame<? extends MinigameMap> game = opGame.get();
        if(!(game instanceof RunningMapMaker)) {
            return;
        }
        event.setCancelled(true);
    }

    private void onBlockChange(ChangeBlockEvent event){
        cancelIfPresent(event, MinigamePlugin.getRunningGame(event.getTransactions().get(0).getOriginal().getPosition().toDouble()));
    }
}
