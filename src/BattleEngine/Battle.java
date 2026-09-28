package BattleEngine;

import Entity.Pokemon;

public class Battle {
    private final Side playerSide;
    private final Side opponentSide;
    private Side winSide = null;
    private boolean isWinnerPlayer = false;

    public Battle(Side playerSide,
                  Side opponentSide) {
        this.playerSide = playerSide;
        this.opponentSide = opponentSide;
    }

    public Side getPlayerSide() {
        return playerSide;
    }

    public Side getOpponentSide() {
        return opponentSide;
    }

    public boolean isEnd() {
        if (isSideFainted(playerSide)) {
            winSide = opponentSide;
            return true;
        }
        if (isSideFainted(opponentSide)) {
            winSide = playerSide;
            isWinnerPlayer = true;
            return true;
        }
        return false;
    }

    public Side getWinSide() {
        return winSide;
    }

    public boolean isWinnerPlayer() {
        return isWinnerPlayer;
    }

    private boolean isSideFainted(Side side) {
        return side.doEscaped() || side.areAllTeamsFainted();
    }
}
