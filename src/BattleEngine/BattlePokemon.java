package BattleEngine;

import Entity.Player;
import Entity.Pokemon;

public class BattlePokemon {
    public enum Side_Tag {
        PLAYER_SIDE,
        OPPONENT_SIDE
    }
    private Pokemon pokemon;
    private Side_Tag sideTag;
    private Player owner;
    private boolean isFainted;

    public BattlePokemon(Pokemon pokemon, Side_Tag sideTag) {
        this.pokemon = pokemon;
        this.sideTag = sideTag;
        owner = pokemon.getOwner();
    }

    public Side_Tag getSideTag() {
        return sideTag;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void checkIsFainted() {
        this.isFainted = pokemon.isFainted();
    }

    public boolean isFainted() {
        return pokemon.isFainted();
    }

    public void takeDamage(int damage) {
        pokemon.takeDamage(damage);
    }

    public Player getOwner() {
        return owner;
    }
}
