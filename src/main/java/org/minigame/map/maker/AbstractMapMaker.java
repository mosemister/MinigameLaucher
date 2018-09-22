package org.minigame.map.maker;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.gamemode.GamemodeType;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractMapMaker implements MapMaker {

    protected GamemodeType current;
    protected Map<GamemodeType, Boolean> supported = new HashMap<>();
    protected Text name;
    protected Set<MinigameProp> props = new HashSet<>();
    protected Vector3i pos1;
    protected Vector3i pos2;

    public AbstractMapMaker(Vector3i pos1, Vector3i pos2, Text name, GamemodeType workingTowards){
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.name = name;
        this.current = workingTowards;
    }

    @Override
    public GamemodeType getCurrentGamemode() {
        return this.current;
    }

    @Override
    public Map<GamemodeType, Boolean> getSupportedGamemodesHashMap() {
        return this.supported;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public boolean updateSupportedList() {
        Map<GamemodeType, Boolean> values = MinigamePlugin.getUniquieSet(GamemodeType.class).stream()
                .filter(gm -> gm.getRequirements().stream()
                        .allMatch(this::meetsRequirement))
                .filter(gm -> !getAllSupportedGamemodes().contains(gm))
                .collect(Collectors.toMap(v -> v, v -> true));
        getSupportedGamemodesHashMap().entrySet().stream()
                .filter(e -> !e.getValue())
                .forEach(e -> values.replace(e.getKey(), e.getValue()));
        this.supported = values;
        return true;
    }

    @Override
    public boolean registerProp(MinigameProp prop) {
        return this.props.add(prop);
    }

    @Override
    public boolean unregisterProp(MinigameProp prop) {
        return this.props.remove(prop);
    }

    @Override
    public Set<MinigameProp> getProps() {
        return new HashSet<>(this.props);
    }

    @Override
    public MapMaker setPos1(Vector3i pos) {
        Vector3i oldPos = getPos1();
        Vector3i dif = pos.clone().min(oldPos);
        this.props.forEach(p -> p.setPosition(p.getPosition().add(dif.toDouble())));
        this.pos1 = pos;
        return this;
    }

    @Override
    public MapMaker setPos2(Vector3i pos) {
        Vector3i oldPos = getPos2();
        Vector3i dif = pos.clone().min(oldPos);
        this.props.forEach(p -> p.setPosition(p.getPosition().add(dif.toDouble())));
        this.pos2 = pos;
        return this;
    }

    @Override
    public Vector3i getPos1() {
        return this.pos1;
    }

    @Override
    public Vector3i getPos2() {
        return this.pos2;
    }
}
