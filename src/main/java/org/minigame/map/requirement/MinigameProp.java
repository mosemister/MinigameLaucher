package org.minigame.map.requirement;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface MinigameProp {

    public Vector3d getPosition();
    void setPosition(Vector3d pos);

    public interface VisualProp extends MinigameProp {

        void generate(Location<World> position);

    }

}
