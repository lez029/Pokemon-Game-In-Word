package BattleEngine.Action;

import BattleEngine.Side;
import Entity.Player;
import Entity.Pokemon;

public class Switch extends Action {

    private final int switchID;
    private final Player actPlayer;
    public Switch(Side actSide, Pokemon activePokemon, int swtichID) {
        super(actSide, activePokemon);
        this.switchID = swtichID;
        this.priority = SWITCH_PRIORITY;
        this.actPlayer = actPokemon.getOwner();
    }

    @Override
    public void act () {
        actPlayer.switchPokemon(switchID);
        actSide.getTeam().remove(actPokemon);
        actSide.getTeam().add(actPlayer.getActivePokemon());
    }

}
