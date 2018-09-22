package org.minigame.utils;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;

public abstract class SpongeMapUtils {

    public static <V extends Object> Entity applyKeyValuePair(Entity entity, Key<? extends BaseValue<?>> key, V value){
        Key<? extends BaseValue<V>> key2 = (Key<? extends BaseValue<V>>) key;
        entity.offer(key2, value);
        return entity;
    }

}
