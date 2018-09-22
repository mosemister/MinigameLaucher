package org.minigame.utils.snapshot;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.*;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Function;

public class EquipableEntitySnapshot implements EntitySnapshot {

    protected InventorySnapshot invSnapshot;
    protected EntitySnapshot snapshot;
    protected Transform<World> transform;
    protected Map<Key<? extends BaseValue<? extends Object>>, Object> originalKeys;

    protected EquipableEntitySnapshot(EntitySnapshot snapshot, InventorySnapshot inv, Transform<World> transform, Map<Key<? extends BaseValue<? extends Object>>, Object> keyValuePair){
        this.snapshot = snapshot;
        this.invSnapshot = inv;
        this.transform = transform;
        this.originalKeys = keyValuePair;
    }

    protected  <T extends Object> void apply(Entity entity, Key<?> k){
        Key<? extends BaseValue<T>> key = (Key<? extends BaseValue<T>>) k;
        System.out.print("Key: " + key.getId());
        Optional<T> opValue = this.get(key);
        if(opValue.isPresent()){
            System.out.print(" : " + opValue.get());
            entity.offer(key, opValue.get());
        }
        Object nValue = this.originalKeys.get(key);
        if(nValue != null){
            T value = (T) nValue;
            System.out.print(" : " + value);
            entity.offer(key, value);
        }
    }

    public Map<Key<? extends BaseValue<? extends Object>>, Object> getOriginalKeys(){
        return this.originalKeys;
    }

    public InventorySnapshot getInventory(){
        return this.invSnapshot;
    }

    @Override
    public Optional<Entity> restore() {
        Optional<Entity> opEntity = this.snapshot.restore();
        if(!opEntity.isPresent()){
            return Optional.empty();
        }
        Entity entity = opEntity.get();
        this.getKeys().stream().forEach(k -> this.apply(entity, k));
        this.originalKeys.keySet().stream().forEach(k -> this.apply(entity, k));
        this.invSnapshot.restore();
        return Optional.of(entity);
    }

    @Override
    public Optional<UUID> getUniqueId() {
        return this.snapshot.getUniqueId();
    }

    @Override
    public Optional<Transform<World>> getTransform() {
        return Optional.of(this.transform);
    }

    @Override
    public EntityType getType() {
        return this.snapshot.getType();
    }

    @Override
    public EntityArchetype createArchetype() {
        return this.snapshot.createArchetype();
    }

    @Override
    public UUID getWorldUniqueId() {
        return this.transform.getExtent().getUniqueId();
    }

    @Override
    public Vector3i getPosition() {
        return this.transform.getPosition().toInt();
    }

    @Override
    public Optional<Location<World>> getLocation() {
        return Optional.of(this.transform.getLocation());
    }

    @Override
    public EntitySnapshot withLocation(Location<World> location) {
        this.transform.setLocation(location);
        return this;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return this.snapshot.getManipulators();
    }

    @Override
    public int getContentVersion() {
        return this.snapshot.getContentVersion();
    }

    @Override
    public DataContainer toContainer() {
        return this.snapshot.toContainer();
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return this.snapshot.getProperty(propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return this.snapshot.getApplicableProperties();
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return this.snapshot.get(containerClass);
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return this.snapshot.getOrCreate(containerClass);
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return this.snapshot.supports(containerClass);
    }

    @Override
    public <E> Optional<EntitySnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return this.snapshot.transform(key, function);
    }

    @Override
    public <E> Optional<EntitySnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        return this.snapshot.with(key, value);
    }

    @Override
    public Optional<EntitySnapshot> with(BaseValue<?> value) {
        return this.snapshot.with(value);
    }

    @Override
    public Optional<EntitySnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return this.snapshot.with(valueContainer);
    }

    @Override
    public Optional<EntitySnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return this.snapshot.with(valueContainers);
    }

    @Override
    public Optional<EntitySnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return this.snapshot.without(containerClass);
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that) {
        return this.snapshot.merge(that);
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that, MergeFunction function) {
        return this.snapshot.merge(that, function);
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        return this.snapshot.getContainers();
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        return this.snapshot.get(key);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        return this.snapshot.getValue(key);
    }

    @Override
    public boolean supports(Key<?> key) {
        return this.snapshot.supports(key);
    }

    @Override
    public EntitySnapshot copy() {
        return new EquipableEntitySnapshot(this.snapshot.copy(), this.invSnapshot, this.transform, new HashMap<>(this.originalKeys));
    }

    @Override
    public Set<Key<?>> getKeys() {
        return this.snapshot.getKeys();
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        return this.snapshot.getValues();
    }

    private static <T extends Object> void put(Map<Key<? extends BaseValue<? extends Object>>, Object> map, Entity entity, Key<? extends BaseValue<? extends Object>> k){
        Key<? extends BaseValue<T>> key = (Key<? extends BaseValue<T>>) k;
        entity.get(key).ifPresent(v -> map.put(key, v));
    }

    public static Optional<EntitySnapshot> of(Equipable equipable){
        if(!(equipable instanceof Entity)){
            return Optional.empty();
        }
        Entity entity = (Entity) equipable;
        Inventory inv = equipable.getInventory();
        InventorySnapshot inv2 = InventorySnapshot.of(inv);
        Map<Key<? extends BaseValue<? extends Object>>, Object> map = new HashMap<>();
        entity.getKeys().stream().forEach(k -> put(map, entity, k));
        return Optional.of(new EquipableEntitySnapshot(entity.createSnapshot(), inv2, new Transform<>(entity.getTransform().getLocation().copy()), map));
    }
}
