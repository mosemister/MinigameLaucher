package org.minigame.data.invite;

import org.minigame.data.MinigameKeys;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class PlayerInviteDataBuilder extends AbstractDataBuilder<PlayerInviteData> implements DataManipulatorBuilder<PlayerInviteData, PlayerInviteImmutableData> {

    public PlayerInviteDataBuilder() {
        super(PlayerInviteData.class, MinigameKeys.CURRENT_CONTEXT_VERSION);
    }

    @Override
    protected Optional<PlayerInviteData> buildContent(DataView container) throws InvalidDataException {
        return create().from(container);
    }

    @Override
    public PlayerInviteData create() {
        return new PlayerInviteData();
    }

    @Override
    public Optional<PlayerInviteData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }
}
