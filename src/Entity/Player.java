package Entity;

import BattleEngine.*;

import BattleEngine.Action.*;
import Entity.Move.Move;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player {
    private static final AtomicInteger NEXT_PLAYER_ID = new AtomicInteger();
    private String name;
    private String gender;
    private int age;
    private final int playerID;
    private List<Pokemon> team;
    private Pokemon activePokemon;
    private Bag bag;
    private final Scanner scanner = new Scanner(System.in);

    protected Player(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        bag = new Bag();
        team = new ArrayList<Pokemon>();

        // TODO: Initialize PlayerID Data Field
        this.playerID = NEXT_PLAYER_ID.incrementAndGet();
    }

    public void switchPokemon(int teamIndex) {
        Pokemon replacement = team.get(teamIndex);
        if (replacement.isFainted() || replacement == activePokemon) {
            throw new IllegalArgumentException("Choose a healthy reserve Pokemon");
        }
        if (activePokemon != null) activePokemon.resetStatStages();
        activePokemon = replacement;
    }

    public void sendPokemon(Pokemon opponent, Field field, Weather weather) {
        /*Side playerSide = new Side(getActivePokemon(),);
        Battle newBattle = new Battle(activePokemon, opponent, field, weather);*/
    }

    // Getter Methods
    public Pokemon getActivePokemon() {
        return activePokemon;
    }
    public List<Pokemon> getTeam() {return team;}

    public void addPokemon(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        if (team.size() >= 6) {
            throw new IllegalStateException("A team can contain at most six Pokemon");
        }
        if (team.contains(pokemon)) {
            throw new IllegalArgumentException("Pokemon is already in the team");
        }
        Pokemon owned = pokemon.getOwner() == this ? pokemon : new Pokemon(pokemon, this);
        team.add(owned);
        if (activePokemon == null) activePokemon = owned;
    }

    public void useItem(Item item, Pokemon pokemon) {}

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Player))
            return false;
        if (this.playerID == ((Player) o).playerID)
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(playerID);
    }

    private Side getCurrentSide(Battle battle) {
        if (battle.getPlayerSide().getTeam().contains(activePokemon)) {
            return battle.getPlayerSide();
        }
        return battle.getOpponentSide();
    }

    private Action chooseMove(Battle battle) {
        int choiceMove;
        Move move;

        // Check if the move is valid
        do {
            choiceMove = scanner.nextInt();
            move = activePokemon.getmoveMapNow().get(choiceMove);
        } while (move == null || move.getCurrentPP() == 0);

        // TODO: Judge the move target type
        Side currentSide = getCurrentSide(battle);
        Side targetSide = currentSide == battle.getPlayerSide()
                ? battle.getOpponentSide() : battle.getPlayerSide();
        Pokemon targetPokemon = targetSide.getActivePokemon();

        return new UseMove(
                currentSide,
                activePokemon,
                move,
                targetPokemon
        );
    }

    public Action chooseSwitch(Battle battle) {
        int choiceSwitch;

        do {
            choiceSwitch = scanner.nextInt();
        } while (choiceSwitch < 0 || choiceSwitch >= team.size()
                || team.get(choiceSwitch).isFainted()
                || team.get(choiceSwitch) == activePokemon);

        Side currentSide = getCurrentSide(battle);

        return new Switch(
                currentSide,
                activePokemon,
                choiceSwitch
        );
    }

    private Action chooseItem(Battle battle) {
        int choiceItem;

        do {
            choiceItem = scanner.nextInt();
        } while (choiceItem < 0 || choiceItem >= bag.getCountItem());

        Item itemUse = bag.getItem(choiceItem);

        Side currentSide = getCurrentSide(battle);

        return new UseItem(
                itemUse,
                activePokemon,
                currentSide
        );
    }

    private Action chooseEscape(Battle battle) {
        Side currentSide = getCurrentSide(battle);

        return new Escape(
                currentSide,
                activePokemon
        );
    }

    public Action chooseAction(Map<Integer, ActionType> actionMap, Battle battle) {
        while (true) {
            int choice = scanner.nextInt();
            ActionType actionType = actionMap.get(choice);
            if (actionType == null) continue;
            switch (actionType) {
                case UseMove:
                    return chooseMove(battle);

                case Switch:
                    return chooseSwitch(battle);

                case UseItem:
                    return chooseItem(battle);

                case Escape:
                    return chooseEscape(battle);

                default:
                    break;
            }
        }
    }

}
