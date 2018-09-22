package org.minigame.map.maker;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.gamemode.GamemodeType;
import org.minigame.utils.MinigameWorldGenerator;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MapMakerBuilder {

    public static Class<? extends MapMaker> DEFAULT_MAP_MAKER_CLASS = AbstractMapMaker.class;

    protected Vector3i pos1 = MinigameWorldGenerator.MINIGAME_MAP_CREATOR_STARTING_POSITION.clone();
    protected Vector3i pos2 = pos1.clone().add(1, 1, 1);
    protected GamemodeType buildingTowards;
    protected Class<? extends MapMaker> mapMakerClass = DEFAULT_MAP_MAKER_CLASS;
    protected Text nameOfMap;

    public MapMakerBuilder setName(Text name){
        this.nameOfMap = name;
        return this;
    }

    public MapMakerBuilder setMap(Class<? extends MapMaker> class1){
        this.mapMakerClass = class1;
        return this;
    }

    public MapMakerBuilder setGamemode(GamemodeType type){
        this.buildingTowards = type;
        return this;
    }

    public MapMakerBuilder setPositionOne(Vector3i pos){
        this.pos1 = pos;
        return this;
    }

    public MapMakerBuilder setPositionTwo(Vector3i pos){
        this.pos2 = pos;
        return this;
    }

    public MapMaker build(){
        if(nameOfMap == null){
            System.err.println("Name of MapMaker was not set in builder");
        }
        if(buildingTowards == null){
            System.err.println("A gamemode type needs to be set in building towards in the mapmaker builder");
        }
        try {
            Constructor<? extends MapMaker> cons = mapMakerClass.getConstructor(Vector3i.class, Vector3i.class, Text.class, GamemodeType.class);
            MapMaker map = cons.newInstance(pos1, pos2, nameOfMap, buildingTowards);
            return map;
        } catch (NoSuchMethodException e) {
            System.err.println(mapMakerClass.getName() + " does not have a constructor of '" + mapMakerClass.getSimpleName() + "(Vector3i pos1, Vector3i pos2, Text name, GamemodeType type)'");
        } catch (IllegalAccessException e) {
            System.err.println(mapMakerClass.getName() + " does not have a public constructor with the parameters of '" + mapMakerClass.getSimpleName() + "(Vector3i pos1, Vector3i pos2, Text name, GamemodeType type)'");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
