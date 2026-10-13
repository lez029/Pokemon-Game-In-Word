package BattleEngine;

import BattleEngine.Action.*;
import Entity.Move.Stat;
import Entity.Pokemon;

import java.util.*;

public class BattleController {
    public static final int CHOICE_USE_MOVE = 1;
    public static final int CHOICE_USE_ITEM = 2;
    public static final int CHOICE_SWITCH = 3;
    public static final int CHOICE_ESCAPE = 4;
    private final Battle battle;
    private PriorityQueue<Action> actionQueue;
    private Field field;
    private Weather weather;
    private int turn;
    private Side winSide;
    private Map<Integer,ActionType> choiceMap = new HashMap<>();

    public BattleController(Battle battle) {
        this.battle = battle;
        this.field = null;
        this.weather = null;
        this.turn = 0;
        this.actionQueue = new PriorityQueue<Action>(
            new Comparator<Action>() {
                @Override
                public int compare(Action action1, Action action2) {
                int priority1 = action1.getPriority();
                int priority2 = action2.getPriority();
                if (priority1 == priority2) {
                    int speed1 = action1.getActPokemon().getBattleStat(Stat.SPEED);
                    int speed2 = action2.getActPokemon().getBattleStat(Stat.SPEED);
                    if (speed1 == speed2){
                        int randomTier1 = action1.getRandomTier();
                        int randomTier2 = action2.getRandomTier();
                        if (randomTier1 == randomTier2) {
                            return 0;
                        }
                        return Integer.compare(randomTier2, randomTier1);
                    }
                    return Integer.compare(speed2, speed1);
                }
                return Integer.compare(priority2, priority1);
            }
        }
        );
        choiceMap.put(CHOICE_USE_MOVE,ActionType.UseMove);
        choiceMap.put(CHOICE_USE_ITEM,ActionType.UseItem);
        choiceMap.put(CHOICE_SWITCH,ActionType.Switch);
        choiceMap.put(CHOICE_ESCAPE,ActionType.Escape);
    }

    public void run() {
        while (!battle.isEnd()) {
            forceSwitch(battle.getPlayerSide());
            forceSwitch(battle.getOpponentSide());
            Pokemon playerPokemon = battle.getPlayerSide().getActivePokemon();
            Pokemon opponentPokemon = battle.getOpponentSide().getActivePokemon();

            // Both sides choose their actions first
            actionQueue.add(
                    playerPokemon.getOwner().chooseAction(choiceMap, battle)
            );
            actionQueue.add(
                    opponentPokemon.getOwner().chooseAction(choiceMap, battle)
            );

            while (!actionQueue.isEmpty()) {
                Action action = actionQueue.poll();

                // Skip the action if the Pokemon has already fainted
                if (action.getActPokemon().isFainted()
                        || action.getActPokemon() != action.getActSide().getActivePokemon()) {
                    continue;
                }

                action.act();

                // Check whether the battle has ended
                if (battle.isEnd()) {
                    actionQueue.clear();
                    endBattle();
                    return;
                }
            }

            turn++;
        }
        endBattle();
    }

    private void forceSwitch(Side side) {
        Pokemon active = side.getActivePokemon();
        if (active.isFainted()) {
            Action replacement = active.getOwner().chooseSwitch(battle);
            if (!(replacement instanceof Switch) || replacement.getActSide() != side) {
                throw new IllegalStateException("A fainted Pokemon must be switched out");
            }
            replacement.act();
            if (side.getActivePokemon().isFainted() || side.getActivePokemon() == active) {
                throw new IllegalStateException("Switch must select a healthy reserve Pokemon");
            }
        }
    }

    public int getTurn() {
        return turn;
    }

    public void endBattle() {
        if (battle.isWinnerPlayer()) {
            System.out.print("You win the battle");
        }
        else {
            System.out.print("You lose the battle.");
        }
    }
}
