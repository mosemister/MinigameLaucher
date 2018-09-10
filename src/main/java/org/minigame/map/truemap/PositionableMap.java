package org.minigame.map.truemap;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface PositionableMap extends MinigameMap {

    public Vector3i getPos1();
    public Vector3i getPos2();

    public default Vector3i getPos(int value){
        switch(value){
            case 0: return getPos1();
            case 1: return getPos2();
            default: throw new IndexOutOfBoundsException();
        }
    }

    default Vector3i getSize(){
        Vector3i pos1 = getPos1();
        Vector3i pos2 = getPos2();
        return pos2.clone().min(pos1);
    }

    default boolean isLocationPartOfMap(Vector3d vector){
        Vector3i vec1 = getPos1();
        Vector3i vec2 = getPos2();
        System.out.println("Is location part of map: \n" +
                " X: " + vec1.getX() + " >= " + vector.getX() + " <= " + vec2.getX() +
                " Y: " + vec1.getZ() + " >= " + vector.getZ() + " <= " + vec2.getY() +
                " Z: " + vec1.getZ() + " >= " + vector.getZ() + " <= " + vec2.getZ()
        );
        if(!(vector.getX() >= vec1.getX())){
            return false;
        }
        if(!(vector.getY() >= vec1.getY())){
            return false;
        }
        if(!(vector.getZ() >= vec1.getZ())){
            return false;
        }
        if(!(vector.getX() <= vec2.getX())){
            return false;
        }
        if(!(vector.getY() <= vec2.getY())){
            return false;
        }
        if(!(vector.getZ() <= vec2.getZ())){
            return false;
        }
        return true;
    }

    public default Location<World> getPlusLoc(Vector3d vector){
        return getLocPos1().copy().add(vector);
    }

    public default Location<World> getMinLoc(Vector3d vector){
        return getLocPos1().copy().add(-vector.getX(), -vector.getY(), -vector.getZ());
    }

    public default Location<World> getLocPos1(){
        return MinigamePlugin.getPlugin().getMinigamesWorld().getLocation(getPos1());
    }

    public default Location<World> getLocPos2(){
        return MinigamePlugin.getPlugin().getMinigamesWorld().getLocation(getPos2());
    }

    public default Location<World> getLocPos(int value){
        return MinigamePlugin.getPlugin().getMinigamesWorld().getLocation(getPos(value));
    }

    public default World getWorld(){
        return MinigamePlugin.getPlugin().getMinigamesWorld();
    }


}
