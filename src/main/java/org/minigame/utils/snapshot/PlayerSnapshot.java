package org.minigame.utils.snapshot;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerSnapshot extends EquipableEntitySnapshot {

    UUID uuid;

    private PlayerSnapshot(UUID uuid, EntitySnapshot snapshot, InventorySnapshot inv, Transform<World> transform, Map<Key<? extends BaseValue<? extends Object>>, Object> map) {
        super(snapshot, inv, transform, map);
        this.uuid = uuid;
    }

    @Override
    public Optional<UUID> getUniqueId(){
        return Optional.of(this.uuid);
    }

    @Override
    public Optional<Entity> restore(){
        Optional<Player> opPlayer = Sponge.getServer().getPlayer(getUniqueId().get());
        if(!opPlayer.isPresent()){
            return Optional.empty();
        }
        Player player = opPlayer.get();
        Transform<World> transform = this.getTransform().get();
        player.setTransform(transform);
        this.getKeys().stream().forEach(k -> this.apply(player, k));
        this.originalKeys.keySet().stream().forEach(k -> this.apply(player, k));
        this.invSnapshot.restore();
        return Optional.of(player);
    }

    private static <T extends Object> void put(Entity entity, EntitySnapshot.Builder snapshot, Key<? extends BaseValue<? extends Object>> k){
        Key<? extends BaseValue<T>> key = (Key<? extends BaseValue<T>>) k;
        Optional<T> opValue = entity.get(key);
        if(opValue.isPresent()){
            snapshot.add(key, opValue.get());
        }
    }

    private static <T extends Object> void put(Map<Key<? extends BaseValue<? extends Object>>, Object> map, Entity entity, Key<? extends BaseValue<? extends Object>> k){
        Key<? extends BaseValue<T>> key = (Key<? extends BaseValue<T>>) k;
        entity.get(key).ifPresent(v -> map.put(key, v));
    }

    public static Optional<EntitySnapshot> of(Player player){
        Inventory inv = player.getInventory();
        InventorySnapshot inv2 = InventorySnapshot.of(inv);
        Map<Key<? extends BaseValue<? extends Object>>, Object> map = new HashMap<>();
        player.getKeys().stream().forEach(k -> put(map, player, k));
        return Optional.of(new PlayerSnapshot(player.getUniqueId(), player.createSnapshot(), inv2, new Transform<>(player.getTransform().getLocation().copy()), map));
    }
}
