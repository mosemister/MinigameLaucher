package org.minigame.map.requirement;

import org.minigame.map.requirement.spawn.types.UserSpawnProp;

public interface PropType <P extends MinigameProp> {

    PropType<UserSpawnProp> USER_SPAWN = new AbstractPropType<>(UserSpawnProp.class);

    public Class<P> getPropClass();

    public class AbstractPropType<P extends MinigameProp> implements PropType<P>{

        Class<P> class1;

        public AbstractPropType(Class<P> class1){
            this.class1 = class1;
        }

        @Override
        public Class<P> getPropClass() {
            return this.class1;
        }
    }
}
