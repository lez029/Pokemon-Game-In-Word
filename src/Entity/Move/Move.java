package Entity.Move;


import Data.MoveData;
import Entity.Pokemon;
import Entity.Status.Status;
import Entity.Type.Type;

import java.util.ArrayList;
import java.util.EnumSet;

public class Move {
    public final MoveData data;
    protected final EnumSet<MoveTag> tags = EnumSet.noneOf(MoveTag.class);

    public Move(MoveData data) {
        this.data = data;
    }

    public Move clone() {
        try {
            return (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
