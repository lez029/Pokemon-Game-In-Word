package Entity.Move;


import Entity.Type.Type;

public class Move {
    public final String name;
    public final Type type;
    // accuracy -> int%
    public final int accuracy;
    public final DamageClass damage_class;
    public final int power;

    public Move clone() {
        try {
            return (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
