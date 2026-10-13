package Entity.Move;


import Data.MoveData;
import Entity.Pokemon;
import Entity.Status.Status;
import Entity.Type.Type;
import BattleEngine.DamageCalculator;
import Data.StatChange;
import java.util.Objects;
import java.util.Random;

import java.util.ArrayList;
import java.util.EnumSet;

public class Move implements Cloneable {
    public final MoveData data;
    protected EnumSet<MoveTag> tags = EnumSet.noneOf(MoveTag.class);
    private int currentPP;

    public Move(MoveData data) {
        this.data = Objects.requireNonNull(data, "data");
        if (data.pp() < 0) throw new IllegalArgumentException("PP cannot be negative");
        this.currentPP = data.pp();
    }

    public Move clone() {
        try {
            Move copy = (Move) super.clone();
            copy.tags = tags.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void applyMove(Pokemon targetPokemon){}

    public int getCurrentPP() { return currentPP; }

    public void restorePP() { currentPP = data.pp(); }

    public int use(Pokemon actor, Pokemon target) {
        return use(actor, target, new Random());
    }

    /** Returns actual HP lost, or -1 for a miss or an unavailable move. */
    public int use(Pokemon actor, Pokemon target, Random random) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(random, "random");
        if (data.target() == Target.USER) target = actor;
        if (actor.isFainted() || target.isFainted() || currentPP == 0) return -1;
        currentPP--;
        Integer accuracy = data.accuracy();
        int stage = Math.max(-6, Math.min(6,
                actor.getStatStage(Stat.ACCURACY) - target.getStatStage(Stat.EVASION)));
        double modifier = stage >= 0 ? (3.0 + stage) / 3.0 : 3.0 / (3.0 - stage);
        if (accuracy != null && random.nextInt(100) >= Math.min(100, accuracy * modifier)) return -1;
        int before = target.getCurrentHP();
        int damage = DamageCalculator.calculate(actor, this, target, 85 + random.nextInt(16));
        target.takeDamage(damage);
        // Immunity also blocks secondary effects of damaging moves.
        if (data.damageClass() != DamageClass.STATUS && data.power() > 0 && damage == 0) return 0;
        boolean applyChanges = data.damageClass() == DamageClass.STATUS
                || data.statusChance() == null || random.nextInt(100) < data.statusChance();
        if (!target.isFainted() && applyChanges && data.statChangeArrayList() != null) {
            for (StatChange change : data.statChangeArrayList()) {
                target.changeStatStage(change.stat_change(), change.modifier());
            }
        }
        applyMove(target);
        return before - target.getCurrentHP();
    }

    public static void main(String[] args) {

    }
}
