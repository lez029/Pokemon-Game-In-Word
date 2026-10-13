package BattleEngine.Action;

import BattleEngine.Side;
import Entity.Move.Move;
import Entity.Pokemon;

public class UseMove extends Action{
    private final Move actMove;
    private final Pokemon targetPokemon;
    public UseMove(Side actSide,
                   Pokemon activePokemon,
                   Move actMove,
                   Pokemon targetPokemon) {
        super(actSide, activePokemon);
        this.actMove = actMove;
        this.targetPokemon = targetPokemon;
        this.priority = actMove.data.priority();
    }

    @Override
    public void act() {
        Pokemon target = targetPokemon.getOwner() == null
                ? targetPokemon : targetPokemon.getOwner().getActivePokemon();
        actPokemon.attack(target, actMove);
    }
}
