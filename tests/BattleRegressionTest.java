import BattleEngine.*;
import BattleEngine.Action.*;
import Data.SpeciesData;
import Entity.*;
import Entity.Ability.Ability;
import Entity.Move.Move;
import Data.MoveData;
import java.util.*;
import org.junit.jupiter.api.Test;

/** Offline battle regression checks executed by Maven Surefire. */
public class BattleRegressionTest {
    private static final SpeciesData SPECIES = new SpeciesData(
            1, "Test", null, null, 50, 50, 50, 50, 50, 50, 300,
            new int[6], 50, level -> 100,
            List.of(new Ability(null)), false, null, List.of(), null,
            70, false, 0, null, List.of());

    private static class Trainer extends RealPlayer {
        int choices;
        int switches;
        int finishAfter = Integer.MAX_VALUE;
        boolean attack;
        Move battleMove;

        Trainer() { super("Test", "", 20); }

        @Override public Action chooseAction(Map<Integer, ActionType> map, Battle battle) {
            choices++;
            Side side = battle.getPlayerSide().getActivePokemon().getOwner() == this
                    ? battle.getPlayerSide() : battle.getOpponentSide();
            if (choices >= finishAfter) return new Escape(side, getActivePokemon());
            if (battleMove != null) {
                Side other = side == battle.getPlayerSide()
                        ? battle.getOpponentSide() : battle.getPlayerSide();
                return new UseMove(side, getActivePokemon(), battleMove, other.getActivePokemon());
            }
            return new Action(side, getActivePokemon()) {
                @Override public void act() {
                    if (attack) {
                        Side other = side == battle.getPlayerSide()
                                ? battle.getOpponentSide() : battle.getPlayerSide();
                        other.getActivePokemon().takeDamage(Integer.MAX_VALUE);
                    }
                }
            };
        }

        @Override public Action chooseSwitch(Battle battle) {
            switches++;
            Side side = battle.getPlayerSide().getActivePokemon().getOwner() == this
                    ? battle.getPlayerSide() : battle.getOpponentSide();
            for (int i = 0; i < getTeam().size(); i++) {
                if (!getTeam().get(i).isFainted() && getTeam().get(i) != getActivePokemon()) {
                    return new Switch(side, getActivePokemon(), i);
                }
            }
            throw new AssertionError("No healthy reserve");
        }
    }

    private static Pokemon add(Trainer owner) {
        Pokemon pokemon = new Pokemon(SPECIES, 50, owner);
        owner.addPokemon(pokemon);
        return pokemon;
    }

    @Test
    void battleRegression() {
        org.junit.jupiter.api.Assertions.assertTrue(BattleRegressionTest.class.desiredAssertionStatus(),
                "Java assertions must be enabled for regression checks");
        Trainer player = new Trainer();
        Trainer opponent = new Trainer();
        assert !player.equals(opponent) : "Players need distinct identities";
        Pokemon first = add(player);
        Pokemon reserve = add(player);
        Side side = new Side(player);
        new Switch(side, first, 1).act();
        assert side.getActivePokemon() == reserve : "Side must track switches";
        assert player.getTeam().size() == 2 : "Switch must preserve the roster";
        assert player.getTeam().get(0) == first;
        try {
            reserve.takeDamage(-1);
            throw new AssertionError("Negative damage accepted");
        } catch (IllegalArgumentException expected) { }

        Pokemon target = add(opponent);
        Pokemon newTarget = add(opponent);
        Move hit = new Move(new MoveData(null, null, null, "", "", "", 1,
                "test", 0, 1, 0, new ArrayList<>(), null, null)) {
            @Override public void applyMove(Pokemon pokemon) { pokemon.takeDamage(1); }
        };
        UseMove selected = new UseMove(side, reserve, hit, target);
        opponent.switchPokemon(1);
        int hp = newTarget.getCurrentHP();
        selected.act();
        assert newTarget.getCurrentHP() == hp - 1 : "Attack must hit the incoming Pokemon";
        assert target.getCurrentHP() == target.getMaxHP();

        Bag bag = new Bag();
        Item item = new Item();
        assert bag.getNumItemMap(item) == 0;
        bag.addItem(item, 2);
        bag.addItem(item, 3);
        assert bag.getNumItemMap(item) == 5;
        bag.getItemList().clear();
        assert bag.getCountItem() == 1;

        Trainer attacker = new Trainer();
        Trainer defender = new Trainer();
        add(attacker);
        add(defender);
        add(defender);
        attacker.attack = true;
        Battle battle = new Battle(new Side(attacker), new Side(defender));
        new BattleController(battle).run();
        assert defender.switches == 1 : "Fainted Pokemon must be replaced";
        assert defender.choices == 2;
        assert battle.isWinnerPlayer();

        Trainer striker = new Trainer();
        Trainer receiver = new Trainer();
        add(striker);
        add(receiver);
        add(receiver);
        striker.battleMove = new Move(new MoveData(null, Entity.Move.DamageClass.PHYSICAL,
                null, "", "", "", 2, "Strike", 200, 40, 0,
                new ArrayList<>(), Entity.Move.Target.SELECTED_POKEMON, null));
        // If actual moves fail to end the battle, escape instead of hanging the test.
        striker.finishAfter = 41;
        Battle actualBattle = new Battle(new Side(striker), new Side(receiver));
        new BattleController(actualBattle).run();
        assert actualBattle.isWinnerPlayer() : "Ordinary moves must resolve a complete battle";
        assert receiver.switches == 1;
        assert striker.battleMove.getCurrentPP() < 40;

        Trainer patient = new Trainer();
        Trainer other = new Trainer();
        add(patient);
        add(other);
        patient.finishAfter = 20000;
        Battle longBattle = new Battle(new Side(patient), new Side(other));
        BattleController controller = new BattleController(longBattle);
        controller.run();
        assert controller.getTurn() == 19999 : "Long battles must not recurse";
        assert longBattle.getWinSide() == longBattle.getOpponentSide();
        System.out.println("\nAll regression checks passed.");
    }
}
