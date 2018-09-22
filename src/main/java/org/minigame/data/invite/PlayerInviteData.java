package org.minigame.data.invite;

import org.minigame.data.MinigameKeys;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.minigame.running.midjoinable.MidJoinableGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.*;

public class PlayerInviteData extends AbstractMappedData<UUID, MidJoinableGame<? extends MinigameMap>, PlayerInviteData, PlayerInviteImmutableData> {

    public PlayerInviteData(){
        this(new HashMap<>());
    }

    public PlayerInviteData(Map<UUID, MidJoinableGame<? extends MinigameMap>> value) {
        super(value, MinigameKeys.MINIGAME_INVITES);
    }

    public Optional<PlayerInviteData> from(DataView view){
        if(view.contains(MinigameKeys.MINIGAME_INVITES.getQuery())){
            setValue((Map<UUID, MidJoinableGame<? extends MinigameMap>>) view.getMap(MinigameKeys.MINIGAME_INVITES.getQuery()).get());
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public Optional<MidJoinableGame<? extends MinigameMap>> get(UUID key) {
        return Optional.ofNullable(getValue().get(key));
    }

    @Override
    public Set<UUID> getMapKeys() {
        return getValue().keySet();
    }

    @Override
    public PlayerInviteData put(UUID key, MidJoinableGame<? extends MinigameMap> value) {
        Map<UUID, MidJoinableGame<? extends MinigameMap>> map = getValue();
        map.put(key, value);
        setValue(map);
        return this;
    }

    @Override
    public PlayerInviteData putAll(Map<? extends UUID, ? extends MidJoinableGame<? extends MinigameMap>> map) {
        Map<UUID, MidJoinableGame<? extends MinigameMap>> values = getValue();
        map.entrySet().stream().forEach(e -> values.put(e.getKey(), e.getValue()));
        setValue(values);
        return this;
    }

    @Override
    public PlayerInviteData remove(UUID key) {
        Map<UUID, MidJoinableGame<? extends MinigameMap>> values = getValue();
        values.remove(key);
        setValue(values);
        return this;
    }

    @Override
    public Optional<PlayerInviteData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<PlayerInviteData> opData = dataHolder.get(PlayerInviteData.class);
        if(opData.isPresent()){
            PlayerInviteData data = opData.get();
            PlayerInviteData finalData = overlap.merge(this, data);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<PlayerInviteData> from(DataContainer container) {
        return from((DataView) container);
    }

    @Override
    public PlayerInviteData copy() {
        return new PlayerInviteData(getValue());
    }

    @Override
    public PlayerInviteImmutableData asImmutable() {
        return new PlayerInviteImmutableData(getValue());
    }

    @Override
    public int getContentVersion() {
        return MinigameKeys.CURRENT_CONTEXT_VERSION;
    }
}
