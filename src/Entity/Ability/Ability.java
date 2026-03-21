package Entity.Ability;

import BattleEngine.Field;
import BattleEngine.Weather;
import Data.AbilityData.AbilityData;
import Entity.Move.Move;
import Entity.Pokemon;

public class Ability implements Cloneable{
    public final AbilityData data;

    public Ability(AbilityData data) {
        this.data = data;
    }

    @Override
    public Ability clone() throws CloneNotSupportedException {
        return new Ability(data);
    }

    public AbilityData getAbilityData() {
        return data;
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
