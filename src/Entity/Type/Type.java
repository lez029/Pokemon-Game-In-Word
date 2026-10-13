package Entity.Type;


import Tools.Parser.APILoader;
import Tools.Parser.TypeParser;

import java.io.IOException;
import java.util.ArrayList;

public enum Type {
    NORMAL,
    FIGHTING,
    FLYING,
    POISON,
    GROUND,
    ROCK,
    BUG,
    GHOST,
    STEEL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    PSYCHIC,
    ICE,
    DRAGON,
    DARK,
    FAIRY;

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
            return defType == null ? 1.0 : TYPE_CHART[this.ordinal()][defType.ordinal()];
    }

    public double getEffectiveness(Type defType1, Type defType2) {
        return getEffectiveness(defType1) * getEffectiveness(defType2);
    }

    public boolean getSameTypeBonus(Type pokeType) {
            return this == pokeType;
    }

    // Test TypeParser class, TypeChartLoader class, Type enum,
    // and all its methods.
    public static void main(String[] args)
            throws IOException, InterruptedException {
        ArrayList<Type> typeList = TypeParser.pokemonTypesParser(APILoader.loadAPI
                ("https://pokeapi.co/api/v2/pokemon/1"));
        System.out.print(Type.WATER.getEffectiveness(typeList.get(0)) + "\n");
        System.out.println(Type.WATER.getEffectiveness(typeList.get(0), typeList.get(1)));
        System.out.println(Type.POISON.getSameTypeBonus(typeList.get(1)));
    }
}
