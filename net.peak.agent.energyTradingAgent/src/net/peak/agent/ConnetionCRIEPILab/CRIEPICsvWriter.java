package net.peak.agent.ConnetionCRIEPILab;

import java.io.FileWriter;
import java.io.IOException;

public class CRIEPICsvWriter {

    private String CSV_FILE_PATH;

    /**
     * Constructor to initialize the CSV writer with the path to the input data folder.
     *
     * @param metaFolderPathInputData The base path for storing the CSV file.
     */
    public CRIEPICsvWriter(String metaFolderPathInputData) {
        this.CSV_FILE_PATH = metaFolderPathInputData;
    }

    /**
     * Method to write a sample CSV file for CRIEPI testing.
     * The file includes predefined steps and parameters for voltage, current, and other test settings.
     */
    public void writeCsv() {
        // Append the specific file name to the base path
        CSV_FILE_PATH += "testScriptCRIEPI\\t001.csv";

        // Define the headers and sample data for the CSV
        String[] headers = {"Step", "Point", "Output", "sec", "Voltage(V)", "Current(A)", "OVP(V)", "OCP(A)", "Bleeder", "I"};
        String[][] data = {
            {"1", "Start", "ON", "1", "3", "1", "MAX", "MAX", "ON", "CVHS"},
            {"2", "", "ON", "1.5", "3.5", "1", "", "", "", ""},
            {"3", "", "ON", "2", "4", "1", "", "", "", ""}
        };

        // Write the data to the CSV file
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write each row of data
            for (String[] row : data) {
                writer.append(String.join(",", row));
                writer.append("\n");
            }

            System.out.println("CSV file successfully created.");
        } catch (IOException e) {
            // Handle any I/O exceptions that occur during file writing
            e.printStackTrace();
        }
    }
}
