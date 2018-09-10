package org.minigame.plugin;

import org.minigame.data.invite.PlayerInviteData;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class MinigameListener {

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") @Has(value = PlayerInviteData.class, inverse = true) Player player){
        player.offer(player.getOrCreate(PlayerInviteData.class).get());
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport event){
        Entity entity = event.getTargetEntity();
        Optional<RunningGame<? extends MinigameMap>> opToGame = MinigamePlugin.getRunningGame(event.getToTransform().getLocation().getPosition());
        Optional<RunningGame<? extends MinigameMap>> opGame = MinigamePlugin.getRunningGame(entity);
        if(!opGame.isPresent()){
            return;
        }
        RunningGame<? extends MinigameMap> game = opGame.get();
        if(opToGame.isPresent()){
            RunningGame<? extends MinigameMap> toGame = opToGame.get();
            if(toGame.equals(game)){
                return;
            }
        }
        if(!(entity instanceof Player)){
            return;
        }
        Player player = (Player)entity;
        event.setCancelled(true);
        player.sendMessage(Text.builder("You can not teleport unless you leave the minigame first").color(TextColors.AQUA).build());
    }

}
