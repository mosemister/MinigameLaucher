package org.minigame.map.requirement;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

public interface MinigameProp {

    public Vector3d getPosition();
    void setPosition(Vector3d pos);

}
