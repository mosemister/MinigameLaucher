package org.minigame.running;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.team.LobbyGroup;
import org.minigame.team.Team;
import org.minigame.team.splitter.TeamSplitter;

import java.util.Collection;

public interface RunningLiveGame <T extends PlayableMap> extends RunningGame<T> {
    public MapGamemode getMapGamemode();

    public default GamemodeType getGamemode(){
        return getMapGamemode().getGamemode();
    }

    public TeamSplitter getTeamSplitter();

    public Collection<Team> splitTeams(TeamSplitter splitter, LobbyGroup group);

    public void initate(Collection<Team> teams);

    public void spawnPlayers();

    public void start();
}
