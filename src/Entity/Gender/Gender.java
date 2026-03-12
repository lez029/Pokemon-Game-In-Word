package Entity.Gender;

public abstract class Gender implements Cloneable{
    @Override
    public Gender clone() {
        try {
            return (Gender) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
