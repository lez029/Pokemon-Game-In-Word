package Entity.Type;


import java.io.IOException;

public enum Type {
    Normal,
    Fighting,
    Flying,
    Poison,
    Ground,
    Rock,
    Bug,
    Ghost,
    Steel,
    Fire,
    Water,
    Grass,
    Electric,
    Psychic,
    Ice,
    Dragon,
    Dark,
    Fairy;

    private static final double[][] TYPE_CHART;

    static {
        try {
            TYPE_CHART = TypeChartLoader.loadChart
                    ("resources/Type_Chart.csv");
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
            throw new RuntimeException(e);
        }
    }

    public double getEffectiveness(Type defType) {
            return TYPE_CHART[this.ordinal()][defType.ordinal()];
    }

    public double getEffectiveness(Type defType1, Type defType2) {
        return getEffectiveness(defType1) * getEffectiveness(defType2);
    }

    public boolean getSameTypeBonus(Type pokeType) {
            return this == pokeType;
    }
}
