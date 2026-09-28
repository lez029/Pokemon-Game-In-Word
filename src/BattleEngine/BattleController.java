package BattleEngine;

import BattleEngine.Action.*;
import Entity.Move.Move;
import Entity.Pokemon;

import java.util.*;

public class BattleController {
    private static final String INDEX_HIT_RATE = "HIT_RATE";
    private static final String INDEX_EVASION_RATE = "EVASION_RATE";
    private static final Random RANDOM = new Random(561234557L);
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
                    int speed1 = action1.getActPokemon().getSpeed();
                    int speed2 = action2.getActPokemon().getSpeed();
                    if (speed1 == speed2){
                        int randomTier1 = action1.getRandomTier();
                        int randomTier2 = action2.getRandomTier();
                        if (randomTier1 == randomTier2) {
                            return 0;
                        }
                        return randomTier2 - randomTier1;
                    }
                    return speed2 - speed1;
                }
                return priority2 - priority1;
            }
        }
        );
        choiceMap.put(CHOICE_USE_MOVE,ActionType.UseMove);
        choiceMap.put(CHOICE_USE_ITEM,ActionType.UseItem);
        choiceMap.put(CHOICE_SWITCH,ActionType.Switch);
        choiceMap.put(CHOICE_ESCAPE,ActionType.Escape);
    }

    public void run() {
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
            if (action.getActPokemon().isFainted()) {
                continue;
            }

            action.act();

            // Check whether the battle has ended
            if (battle.isEnd()) {
                endBattle();
                return;
            }

            // Renew active Pokemon after the action
            playerPokemon = battle.getPlayerSide().getActivePokemon();
            opponentPokemon = battle.getOpponentSide().getActivePokemon();

            // Force switch if necessary
            if (playerPokemon.isFainted()) {
                actionQueue.add(
                        playerPokemon.getOwner().chooseSwitch(battle)
                );
            }

            if (opponentPokemon.isFainted()) {
                actionQueue.add(
                        opponentPokemon.getOwner().chooseSwitch(battle)
                );
            }
        }

        turn++;
        run();
    }

    private static boolean isHit(Pokemon actor, Move move, Pokemon target) {
        if (move.data.accuracy() == null)
            return true;
        int modifierPokemon = actor.getStatChanges().get(INDEX_HIT_RATE).modifier()
                - target.getStatChanges().get(INDEX_EVASION_RATE).modifier();
        modifierPokemon = Math.max(-6, Math.min(6, modifierPokemon));
        double modifier;
        if (modifierPokemon < 0) {
            modifier = 3.0 / (3 - modifierPokemon);
        }
        else {
            modifier = (3.0 + modifierPokemon) / 3.0;
        }
        int hitNum = Math.min((int)( move.data.accuracy()
                * modifier), 100);
        return RANDOM.nextInt(100) < hitNum;
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
