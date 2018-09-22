package org.minigame.running;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.builder.MapBuilder;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.team.LobbyGroup;
import org.minigame.team.Team;
import org.minigame.team.splitter.TeamSplitter;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Map;

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

    public default void end(boolean includePlayers){
        if(includePlayers) {
            this.getPlayers().stream().forEach(p -> this.unregister(p, false));
        }
        new MapBuilder(this.getMap()){

            @Override
            protected void onBuilt(ReadyToPlayMap map, Object plugin) {

            }

            @Override
            protected void onClear() {
                MinigamePlugin.unregister(RunningLiveGame.this);
            }

            @Override
            protected void onStackFinish(int totalStack, int current, Map<Location<World>, BlockState> written) {

            }
        }.clear(MinigamePlugin.getPlugin());
    }
}
