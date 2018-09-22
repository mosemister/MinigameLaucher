package org.minigame.map.requirement;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.utils.UniquieId;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface MinigameRequirement <P extends MinigameProp> extends UniquieId {

    int getAmountRequired();
    boolean isProp(P prop);
    Class<P> getPropClass();

}
