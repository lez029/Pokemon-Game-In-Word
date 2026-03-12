package Entity.Type;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TypeChartLoader {
    public static double[][] loadChart(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        br.readLine();
        double[][] chart =
                new double[Type.values().length][Type.values().length];
        String line;
        int row = 0;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            for (int col = 1; col < parts.length; col++) {
                chart[row][col] = Double.parseDouble(parts[col]);
            }
            row++;
        }
        br.close();
        return chart;
    }
}
