package BattleEngine;

import Entity.Pokemon;
import Entity.Move.DamageClass;
import Entity.Move.Move;
import Entity.Move.Stat;
import Entity.Type.Type;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/** Basic single-target damage; weather, critical hits and abilities are not yet applied. */
public final class DamageCalculator {
    private DamageCalculator() { }

    public static int calculate(Pokemon actor, Move move, Pokemon target) {
        return calculate(actor, move, target, ThreadLocalRandom.current().nextInt(85, 101));
    }

    /** Explicit roll makes damage bounds reproducible in tests and previews. */
    public static int calculate(Pokemon actor, Move move, Pokemon target, int roll) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(move, "move");
        Objects.requireNonNull(target, "target");
        if (roll < 85 || roll > 100) throw new IllegalArgumentException("Roll must be 85..100");
        if (move.data.power() <= 0 || move.data.damageClass() == DamageClass.STATUS) return 0;
        if (move.data.damageClass() == null) throw new IllegalArgumentException("Missing damage class");
        boolean physical = move.data.damageClass() == DamageClass.PHYSICAL;
        double attack = actor.getBattleStat(physical ? Stat.ATTACK : Stat.SPECIAL_ATTACK);
        double defense = target.getBattleStat(physical ? Stat.DEFENSE : Stat.SPECIAL_DEFENSE);
        Type type = move.data.type();
        double effectiveness = type == null ? 1.0
                : type.getEffectiveness(target.data.type1(), target.data.type2());
        if (effectiveness == 0) return 0;
        double base = Math.floor(Math.floor((2L * actor.getLevel() / 5 + 2)
                * move.data.power() * attack / defense) / 50) + 2;
        double stab = type != null && (type == actor.data.type1() || type == actor.data.type2())
                ? 1.5 : 1.0;
        return (int)Math.max(1, Math.min(Integer.MAX_VALUE,
                Math.floor(Math.floor(base * roll / 100.0) * stab * effectiveness)));
    }
}
