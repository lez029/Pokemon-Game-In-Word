package Entity.Ability;

import BattleEngine.Field;
import BattleEngine.Weather;
import Entity.Move.Move;
import Entity.Pokemon;

public class Ability implements Cloneable{
    public final String name;
    public final String shortEffect;
    public final String description;
    public final String effect;
    public final boolean isHidden;
    public final int slot;
    public final int id;

    public Ability(String name,
                   String shortEffect,
                   String description,
                   String effect,
                   boolean isHidden,
                   int slot,
                   int id) {
        this.name = name;
        this.shortEffect = shortEffect;
        this.description = description;
        this.effect = effect;
        this.isHidden = isHidden;
        this.slot = slot;
        this.id = id;
    }

    @Override
    public Ability clone() throws CloneNotSupportedException {
        return new Ability(name, shortEffect, description, effect,
                isHidden, slot, id);
    }

    // In Battle
    // Switch In
    public void onSwitchIn(Pokemon user,
                            Weather weather,
                            Field field) {}

    // Move Use
    public void onMoveUse(Pokemon user,
                            Pokemon opponent,
                            Move move) {}

    // Attack Hit
    public void onAttackHit(Pokemon user,
                            Pokemon opponent,
                            Move move) {}

    // Take Damage
    public void onTakeDamage(Pokemon user,
                             Pokemon opponent,
                             Move move) {}

    // Turn End
    public void onTurnEnd(){}
}
