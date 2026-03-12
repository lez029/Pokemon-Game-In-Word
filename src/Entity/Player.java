package Entity;

import BattleEngine.Battle;
import BattleEngine.Field;
import BattleEngine.Weather;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    private String name;
    private String gender;
    private int age;
    private List<Pokemon> team;
    private Pokemon activePokemon;
    private Bag bag;

    protected Player(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        bag = new Bag();
        team = new ArrayList<Pokemon>();
    }

    public void switchPokemon(int teamIndex) {
        activePokemon = team.get(teamIndex);
    }

    public void sendPokemon(Pokemon opponent, Field field, Weather weather) {
        Battle newBattle = new Battle(activePokemon, opponent, field, weather);
    }

    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    public abstract void addPokemon(Pokemon pokemon);

}
