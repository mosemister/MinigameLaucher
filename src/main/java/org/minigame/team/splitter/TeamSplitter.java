package org.minigame.team.splitter;

import org.minigame.team.PlayerGroup;
import org.minigame.team.Team;
import org.minigame.utils.UniquieId;

import java.util.Set;

public interface TeamSplitter extends UniquieId {

    Set<Team> splitPlayersBySize(PlayerGroup.PartyGroup parties, int teamMaxSize);
    Set<Team> splitTeamsByTeamCount(PlayerGroup.PartyGroup parties, int teamCount);
}
