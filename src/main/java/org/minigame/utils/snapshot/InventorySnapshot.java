package org.minigame.utils.snapshot;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.*;

public class InventorySnapshot implements DataSerializable {

    Map<SlotIndex, ItemStackSnapshot> map = new HashMap<>();
    Inventory originalInventory;

    public InventorySnapshot(Inventory original, Map<SlotIndex, ItemStackSnapshot> items){
        this.originalInventory = original;
        this.map.putAll(items);
    }

    public Inventory getOriginalInventory(){
        return this.originalInventory;
    }

    public Optional<ItemStackSnapshot> getItem(SlotIndex index){
        Optional<Map.Entry<SlotIndex, ItemStackSnapshot>> opEntry = this.map.entrySet().stream().filter(e -> e.getKey().getValue() == index.getValue()).findFirst();
        if(!opEntry.isPresent()){
            return Optional.empty();
        }
        return Optional.of(opEntry.get().getValue());
    }

    public Set<SlotIndex> getPositions(){
        return this.map.keySet();
    }

    public Collection<ItemStackSnapshot> getItems(){
        return this.map.values();
    }

    public void restore(){
        this.originalInventory.clear();
        this.map.entrySet().forEach(e -> this.originalInventory.slots().forEach(s -> {
            if (s.getInventoryProperty(SlotIndex.class).get().getValue() == e.getKey().getValue()){
                s.set(e.getValue().createStack());
            }
        }));
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = DataContainer.createNew();
        map.entrySet().stream().forEach(e -> {
            container.set(DataQuery.of("index" + e.getKey().getValue()), e.getValue());
        });
        return container;
    }

    public static InventorySnapshot of(Inventory inventory){
        Map<SlotIndex, ItemStackSnapshot> map = new HashMap<>();
        inventory.slots().forEach(s -> {
            Optional<ItemStack> opStack = s.peek();
            if(!opStack.isPresent()){
                return;
            }
            map.put(s.getInventoryProperty(SlotIndex.class).get(), opStack.get().createSnapshot());
        });
        return new InventorySnapshot(inventory, map);
    }
}
