package Entity.Status;

public class Status implements Cloneable{
    @Override
    public Status clone() {
        try {
            return (Status) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
