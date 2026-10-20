import BattleEngine.DamageCalculator;
import BattleEngine.Side;
import BattleEngine.Action.UseMove;
import Data.*;
import Entity.*;
import Entity.Ability.Ability;
import Entity.Move.*;
import Entity.Type.Type;
import java.util.*;
import org.junit.jupiter.api.Test;

public class DamageRegressionTest {
    private static SpeciesData species(Type type) {
        return new SpeciesData(1, "Test", type, null,
                100, 100, 100, 100, 100, 100, 600, new int[6], 100,
                level -> 100, List.of(new Ability(null)), false, null, List.of(),
                null, 70, false, 0, null, List.of());
    }

    private static Move move(Type type, DamageClass category, Integer accuracy,
                             int pp, Target target, StatChange... changes) {
        return new Move(new MoveData(accuracy, category, null, "", "", "", 1, "Test",
                category == DamageClass.STATUS ? 0 : 50, pp, 0,
                new ArrayList<>(List.of(changes)), target, type));
    }

    private static class FixedPokemon extends Pokemon {
        FixedPokemon(Type type) { super(species(type), 50); }
        @Override public int getBattleStat(Stat stat) {
            return stat == Stat.SPECIAL_ATTACK ? 200 : 100;
        }
    }

    private static Random rolls(int... values) {
        return new Random(0) {
            int index;
            @Override public int nextInt(int bound) {
                int value = values[index++];
                assert value >= 0 && value < bound;
                return value;
            }
        };
    }

    @Test
    void damageRegression() {
        org.junit.jupiter.api.Assertions.assertTrue(DamageRegressionTest.class.desiredAssertionStatus(),
                "Java assertions must be enabled for regression checks");
        Pokemon actor = new FixedPokemon(Type.NORMAL);
        Pokemon target = new FixedPokemon(Type.WATER);
        Move physical = move(Type.NORMAL, DamageClass.PHYSICAL, null, 20, Target.SELECTED_POKEMON);
        assert DamageCalculator.calculate(actor, physical, target, 100) == 36;
        assert DamageCalculator.calculate(actor, physical, target, 85) == 30;
        Move special = move(Type.NORMAL, DamageClass.SPECIAL, null, 20, Target.SELECTED_POKEMON);
        assert DamageCalculator.calculate(actor, special, target, 100) == 69;
        assert Type.FAIRY.getEffectiveness(Type.DRAGON) == 2;
        assert Type.DRAGON.getEffectiveness(Type.FAIRY) == 0;
        assert Type.NORMAL.getEffectiveness(Type.FAIRY) == 1;
        assert Type.FIRE.getEffectiveness(Type.GRASS, Type.STEEL) == 4;
        assert Type.WATER.getEffectiveness(Type.FIRE, null) == 2;
        assert DamageCalculator.calculate(actor, physical, new FixedPokemon(Type.GHOST), 100) == 0;

        Move miss = move(Type.NORMAL, DamageClass.PHYSICAL, 0, 1, Target.SELECTED_POKEMON);
        int hp = target.getCurrentHP();
        assert miss.use(actor, target, rolls(0)) == -1;
        assert miss.getCurrentPP() == 0;
        assert target.getCurrentHP() == hp;
        assert miss.use(actor, target, rolls()) == -1;
        miss.restorePP();
        assert miss.getCurrentPP() == 1;

        Move boost = move(Type.NORMAL, DamageClass.STATUS, null, 10, Target.USER,
                new StatChange(Stat.ATTACK, 2));
        assert boost.use(target, actor, rolls(0)) == 0;
        assert target.getStatStage(Stat.ATTACK) == 2;
        assert actor.getStatStage(Stat.ATTACK) == 0;
        target.changeStatStage(Stat.ATTACK, Integer.MAX_VALUE);
        assert target.getStatStage(Stat.ATTACK) == 6;
        target.changeStatStage(Stat.ATTACK, Integer.MIN_VALUE);
        assert target.getStatStage(Stat.ATTACK) == -6;

        Pokemon normal = new Pokemon(species(Type.NORMAL), 50);
        int attack = normal.getAttack();
        normal.changeStatStage(Stat.ATTACK, 2);
        assert normal.getBattleStat(Stat.ATTACK) == attack * 2;
        normal.getmoveMapNow().put(1, physical);
        Pokemon copy = normal.clone();
        copy.getmoveMapNow().get(1).use(copy, target, rolls(15));
        assert physical.getCurrentPP() == 20;
        assert copy.getmoveMapNow().get(1).getCurrentPP() == 19;
        copy.resetStatStages();
        assert normal.getStatStage(Stat.ATTACK) == 2;

        RealPlayer owner = new RealPlayer("Test", "", 20);
        Pokemon outgoing = new Pokemon(species(Type.NORMAL), 50, owner);
        Pokemon incoming = new Pokemon(species(Type.NORMAL), 50, owner);
        owner.addPokemon(outgoing);
        owner.addPokemon(incoming);
        outgoing.changeStatStage(Stat.SPEED, 3);
        owner.switchPokemon(1);
        assert outgoing.getStatStage(Stat.SPEED) == 0;
        Move strike = move(Type.NORMAL, DamageClass.PHYSICAL, null, 10, Target.SELECTED_POKEMON);
        int before = target.getCurrentHP();
        new UseMove(new Side(owner), incoming, strike, target).act();
        assert target.getCurrentHP() < before : "Real actions must deal damage";
        target.takeDamage(Integer.MAX_VALUE);
        assert strike.use(incoming, target, rolls()) == -1;
        assert strike.getCurrentPP() == 9;
        System.out.println("Damage regression checks passed.");
    }
}
