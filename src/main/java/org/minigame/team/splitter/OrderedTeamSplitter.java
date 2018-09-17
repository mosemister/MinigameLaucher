package org.minigame.team.splitter;

import org.minigame.plugin.MinigamePlugin;
import org.minigame.team.Party;
import org.minigame.team.PlayerGroup;
import org.minigame.team.Team;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderedTeamSplitter implements TeamSplitter {

    @Override
    public Set<Team> splitPlayersBySize(PlayerGroup.PartyGroup parties, int teamMaxSize) {
        double teamCountDouble = ((double) parties.getParties().size() / (double) teamMaxSize);
        System.out.println("Original team count: " + teamCountDouble);
        if (teamCountDouble - ((int) teamCountDouble) != 0) {
            teamCountDouble = ((int) teamCountDouble) + 1;
        }
        int teamCount = (int) teamCountDouble;
        System.out.println("Split players by team size: " + teamCount + " Parties: " + parties.getParties().size() + " MaxTeamSize: " + teamMaxSize);
        return splitTeams(parties, teamCount, teamMaxSize);
    }

    @Override
    public Set<Team> splitTeamsByTeamCount(PlayerGroup.PartyGroup parties, int teamCount) {
        int playersPerTeam = (parties.getParties().size() / teamCount);
        System.out.println("Split players by amount of teams: " + teamCount);
        return splitTeams(parties, teamCount, playersPerTeam);
    }

    private Set<Team> splitTeams(PlayerGroup.PartyGroup parties, int teamCount, int playersPerTeam) {
        System.out.println("\tSplitting teams");
        Team[] teams = new Team[teamCount];
        System.out.println("\tRegistered teams: " + teams.length);
        for (int a = 0; a < teamCount; a++) {
            teams[a] = new Team();
        }
        Set<Player> playersToAdd = new HashSet<>(parties.getPlayers());
        System.out.println("\tPlayers to add: " + playersToAdd);
        Set<Party> realParties = parties.getParties().stream().filter(p -> p.getPlayers().size() > 1).collect(Collectors.toSet());
        System.out.println("\tReal parties: " + realParties.size());
        while (!realParties.isEmpty()) {
            boolean timeout = true;
            for (Party party : realParties) {
                for (Team team : teams) {
                    if (team.getPlayers().size() <= (playersPerTeam - party.getPlayers().size())) {
                        team.addParties(party);
                        realParties.remove(party);
                        playersToAdd.removeAll(party.getPlayers());
                        timeout = false;
                    }
                }
            }
            if (timeout) {
                break;
            }
        }
        System.out.println("\tadding other players:");
        while (!playersToAdd.isEmpty()) {
            Player player = playersToAdd.iterator().next();
            System.out.println("\tLeft to add: " + playersToAdd.size() + ": target: " + player.getName());
            boolean timeout = true;
            for (Team team : teams) {
                System.out.println("\t\tChecking team: " + team.getPlayers().size());
                if ((team.getPlayers().size() < playersPerTeam)) {
                    System.out.println("\t\tSpace found in team, adding");
                    team.addPlayers(player);
                    playersToAdd.remove(player);
                    timeout = false;
                    break;
                }
            }
            if (timeout) {
                System.out.println("Timed out");
                Team team = null;
                for (Team team2 : teams) {
                    if (team == null) {
                        team = team2;
                    }
                    if (team2.getPlayers().size() < team.getPlayers().size()) {
                        team = team2;
                    }
                }
                System.out.println("adding player to team");
                team.addPlayers(player);
                playersToAdd.remove(player);
            }
        }
        return new HashSet<>(Arrays.asList(teams));
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public String getIdName() {
        return "OrderedTeamSplitter";
    }
}
