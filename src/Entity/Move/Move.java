package Entity.Move;


import Data.MoveData;
import Entity.Pokemon;
import BattleEngine.DamageCalculator;
import Data.StatChange;
import java.util.Objects;
import java.util.Random;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

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

    /** Extension hook called after a hit unless immunity blocks it, regardless of the stat-change roll. */
    public void applyMove(Pokemon targetPokemon){}

    public int getCurrentPP() { return currentPP; }

    public void restorePP() { currentPP = data.pp(); }

    public int use(Pokemon actor, Pokemon target) {
        return use(actor, target, ThreadLocalRandom.current());
    }

    /**
     * Executes the move and returns actual HP lost, or -1 if unavailable or missed.
     * Accepts a Random instance for reproducible tests. Misses still consume PP.
     */
    public int use(Pokemon actor, Pokemon target, Random random) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(random, "random");
        if (data.target() == Target.USER) target = actor;
        if (actor.isFainted() || target.isFainted() || currentPP == 0) return -1;
        currentPP--;
        if (!hits(actor, target, random)) return -1;

        int before = target.getCurrentHP();
        int damage = DamageCalculator.calculate(actor, this, target, 85 + random.nextInt(16));
        target.takeDamage(damage);
        // Immunity to a damaging move also blocks stat changes and the extension hook.
        if (data.damageClass() != DamageClass.STATUS && data.power() > 0 && damage == 0) return 0;

        applyStatChanges(target, random);
        applyMove(target);
        return before - target.getCurrentHP();
    }

    /** Null accuracy skips the accuracy check without consuming a random value. */
    private boolean hits(Pokemon actor, Pokemon target, Random random) {
        Integer accuracy = data.accuracy();
        if (accuracy == null) return true;
        int stage = Math.max(-6, Math.min(6,
                actor.getStatStage(Stat.ACCURACY) - target.getStatStage(Stat.EVASION)));
        double modifier = stage >= 0 ? (3.0 + stage) / 3.0 : 3.0 / (3.0 - stage);
        return random.nextInt(100) < Math.min(100, accuracy * modifier);
    }

    private void applyStatChanges(Pokemon target, Random random) {
        // Roll before checking the target and list to preserve random draw order.
        boolean applyChanges = data.damageClass() == DamageClass.STATUS
                || data.statusChance() == null || random.nextInt(100) < data.statusChance();
        if (!target.isFainted() && applyChanges && data.statChangeArrayList() != null) {
            for (StatChange change : data.statChangeArrayList()) {
                target.changeStatStage(change.stat_change(), change.modifier());
            }
        }
    }

    public static void main(String[] args) {

    }
}
