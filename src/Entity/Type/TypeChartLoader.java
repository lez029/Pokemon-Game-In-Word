package Entity.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class TypeChartLoader {
    public static double[][] loadChart(String path) throws IOException {
        int count = Type.values().length;
        double[][] chart = new double[count][count];
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || header.split(",", -1).length != count + 1) {
                throw new IOException("Invalid type chart header");
            }
            String[] columns = header.split(",", -1);
            for (int col = 0; col < count; col++) {
                if (!columns[col + 1].trim().equalsIgnoreCase(Type.values()[col].name())) {
                    throw new IOException("Unexpected defender type at column " + (col + 1));
                }
            }
            for (int row = 0; row < count; row++) {
                String line = reader.readLine();
                if (line == null) throw new IOException("Incomplete type chart");
                String[] cells = line.split(",", -1);
                if (cells.length != count + 1
                        || !cells[0].trim().equalsIgnoreCase(Type.values()[row].name())) {
                    throw new IOException("Invalid type chart row " + (row + 1));
                }
                for (int col = 0; col < count; col++) {
                    try {
                        double value = Double.parseDouble(cells[col + 1].trim());
                        if (value != 0 && value != 0.5 && value != 1 && value != 2) {
                            throw new NumberFormatException("Invalid multiplier");
                        }
                        chart[row][col] = value;
                    } catch (NumberFormatException e) {
                        throw new IOException("Invalid multiplier at row " + (row + 1)
                                + ", column " + (col + 1), e);
                    }
                }
            }
        }
        return chart;
    }
}
