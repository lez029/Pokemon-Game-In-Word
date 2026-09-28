package Entity;

import BattleEngine.*;

import BattleEngine.Action.*;
import Entity.Move.Move;

import java.util.*;

public abstract class Player {
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
        this.playerID = 0;
    }

    public void switchPokemon(int teamIndex) {
        Pokemon pokemonTemp = activePokemon;
        activePokemon = team.get(teamIndex);
        team.add(teamIndex, pokemonTemp);
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

    public abstract void addPokemon(Pokemon pokemon);

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
        } while (move == null);

        // TODO: Judge the move target type
        int choiceTarget;
        Pokemon targetPokemon;

        do {
            choiceTarget = scanner.nextInt();
        } while (choiceTarget <= 0
                || choiceTarget > battle.getOpponentSide().getTeam().size());

        targetPokemon = battle.getOpponentSide()
                .getTeam()
                .get(choiceTarget - 1);

        Side currentSide = getCurrentSide(battle);

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
        } while (choiceSwitch < 0 || choiceSwitch >= team.size());

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
        int choice = scanner.nextInt();

        switch (actionMap.get(choice)) {
            case UseMove:
                return chooseMove(battle);

            case Switch:
                return chooseSwitch(battle);

            case UseItem:
                return chooseItem(battle);

            case Escape:
                return chooseEscape(battle);

            default:
                return chooseAction(actionMap, battle);
        }
    }

}
