package Entity;

public class RealPlayer extends Player{
    public RealPlayer(String name, String gender, int age) {
        super(name, gender, age);
    }
    @Override
    public  void addPokemon(Pokemon pokemon){
        return;
    }
}
