package Entity.Ability;

import Entity.Pokemon;

public abstract class Ability implements Cloneable{
    private String name;
    private String description;

    public abstract void onSwitchIn();
    public abstract void onHPChange(Pokemon attacker);
    public abstract void onHPChange(Pokemon attacker, Pokemon defender);
    public abstract void onBeforeMove(Pokemon attacker, Pokemon defender);
    public abstract void onAttack(Pokemon attacker, Pokemon defender);
    public abstract void onTakeDamage();

    @Override
    public Ability clone() {
        try {
            return (Ability) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
