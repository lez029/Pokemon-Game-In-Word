package BattleEngine;

import BattleEngine.Action.Escape;
import Entity.Player;
import Entity.Pokemon;

import java.util.*;

public class Side {
    private Player player;
    private Pokemon activePokemon;
    private List<Pokemon> team;
    private boolean canEsacpe = true;
    private boolean doEscaped = false;
    public final int randomTier;

    public Side (Player player) {
        this.player = player;
        activePokemon = player.getActivePokemon();
        team = player.getTeam();
        randomTier = new Random().nextInt(500);
    }

    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    public List<Pokemon> getTeam(){
        return new ArrayList<>(team);
    }

    public boolean escape() {
        if (canEsacpe) {
            doEscaped = true;
            System.out.println("Got away safely!");
        }
        else {
            System.out.println("Couldn't escape!");
        }
        return doEscaped;
    }

    public boolean doEscaped() {
        return doEscaped;
    }

    public boolean areAllTeamsFainted() {
        Set<Player> playersChecked = new HashSet<>();
        for (Pokemon pokemon : team) {
            if (!pokemon.isFainted())
                return false;
            playersChecked.add(pokemon.getOwner());
        }
        for (Player currPlayer : playersChecked) {
            for (Pokemon pokemon : currPlayer.getTeam()) {
                if (!pokemon.isFainted())
                    return false;
            }
        }
        return true;
    }
}
