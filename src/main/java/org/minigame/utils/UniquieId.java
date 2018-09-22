package org.minigame.utils;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public interface UniquieId {

    PluginContainer getPlugin();
    String getIdName();

    default String getId(){
        return getPlugin().getId() + ":" + getIdName().toLowerCase();
    }

    interface UniquieColoredId extends UniquieId {

        Text getName();

        @Override
        default String getIdName(){
            return getName().toPlain().toLowerCase();
        }

    }
}
