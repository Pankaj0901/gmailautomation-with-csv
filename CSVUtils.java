package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.*;

public class CSVUtils {

    public static List<Map<String, String>> readCSV(String filePath) throws IOException, CsvException {
        InputStream is = CSVUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            throw new FileNotFoundException("CSV file not found in resources: " + filePath);
        }

        CSVReader reader = new CSVReader(new InputStreamReader(is));
        List<String[]> allRows = reader.readAll();
        List<Map<String, String>> data = new ArrayList<>();
        String[] headers = allRows.get(0);

        for (int i = 1; i < allRows.size(); i++) {
            Map<String, String> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j], allRows.get(i)[j]);
            }
            data.add(row);
        }

        return data;
    }
}
