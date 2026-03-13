package Entity.Type;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class TypeChartLoader {
    public static double[][] loadChart(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        br.readLine();
        // System.out.print(Type.values().length + "\n");
        double[][] chart =
                new double[Type.values().length][Type.values().length];
        String line;
        // First row of Type_Chart.csv is type of defender
        int row = 1;
        while ((line = br.readLine()) != null && row <= 17) {
            String[] parts = line.split(",");
            // First col of Type_Chart.csv is type of attacker
            for (int col = 1; col < parts.length - 1; col++) {
                // System.out.print(Arrays.toString(new int[]{row, col})  + "\n");
                chart[row - 1][col - 1] = Double.parseDouble(parts[col]);
            }
            row++;
        }
        br.close();
        return chart;
    }
}
