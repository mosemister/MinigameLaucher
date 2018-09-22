package org.minigame.data.invite;

import org.minigame.data.MinigameKeys;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.minigame.running.midjoinable.MidJoinableGame;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;

import java.util.Map;
import java.util.UUID;

public class PlayerInviteImmutableData extends AbstractImmutableMappedData<UUID, MidJoinableGame<? extends MinigameMap>, PlayerInviteImmutableData, PlayerInviteData> {

    public PlayerInviteImmutableData(Map<UUID, MidJoinableGame<? extends MinigameMap>> value) {
        super(value, MinigameKeys.MINIGAME_INVITES);
    }

    @Override
    public PlayerInviteData asMutable() {
        return new PlayerInviteData(getValue());
    }

    @Override
    public int getContentVersion() {
        return MinigameKeys.CURRENT_CONTEXT_VERSION;
    }
}
