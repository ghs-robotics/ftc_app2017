package org.firstinspires.ftc.team4042.autos;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Hazel on 2/21/2018.
 */

public class AutoParser {
    File file;
    Telemetry.Log log;
    Telemetry telemetry;

    private ArrayList<AutoInstruction> instructions = new ArrayList<>();

    public AutoParser(Telemetry telemetry, String filePath) {
        this.telemetry = telemetry;
        this.log = telemetry.log();
        log.add("Reading file " + filePath);
        file = new File("./storage/emulated/0/bluetooth/" + filePath);

        loadFile();
    }

    /**
     * Reads from the file and puts the information into instructions
     */
    private void loadFile() {
        if (file == null) { return; } //Can't load a null file

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //Reads the lines from the file in order
                if (line.length() > 0) {
                    if (line.charAt(0) != '#') { //Use a # for a comment
                        HashMap<String, String> parameters = new HashMap<>();

                        //x:3 --> k = x, v = 3
                        String[] inputParameters = line.split(" ");
                        StringBuilder para = new StringBuilder("Parameter: ");
                        int i = 0;
                        while (i < inputParameters.length) {
                            String parameter = inputParameters[i];
                            String[] kv = parameter.split(":", 2);
                            parameters.put(kv[0],  kv[1]);
                            //Gets the next parameter and adds it to the list
                            para.append(kv[0]).append(":").append(kv[1]).append(" ");
                            i++;
                        }

                        log.add(para.toString());

                        //Stores those values as an instruction
                        AutoInstruction instruction = new AutoInstruction(parameters);
                        instructions.add(instruction);

                        telemetry.update();
                    }
                }
            }
            fileReader.close();
        } catch (Exception ex) {
            telemetry.addData("Error", "trying to load file");
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("error", sw.toString());
        }
    }

    public AutoInstruction popNext() {
        try {
            return instructions.remove(0);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public File getFile() {
        return file;
    }

    /**
     * Gets a required double parameter, or throws an exception if it's not found
     * @param parameters A hashmap of all the parameters
     * @param key The parameter key to look for
     * @return The value for the key
     * @throws NoSuchFieldError If the key isn't mapped
     */
    public double getParam(HashMap<String, String> parameters, String key) throws NoSuchFieldError {
        try {
            return Double.parseDouble(parameters.get(key));
        } catch (Exception ex) {
            throw new NoSuchFieldError("Could not find " + key);
        }
    }

    /**
     * Gets an optional double parameter
     * @param parameters A hashmap of all the parameters
     * @param key The parameter key to look for
     * @param defaultVal The value to return if the optional parameter is not included
     * @return The value for the key, or the default value if the parameter doesn't exist
     */
    public double getParam(HashMap<String, String> parameters, String key, double defaultVal) {
        return parameters.containsKey(key) ? Double.parseDouble(parameters.get(key)) : defaultVal;
    }

    /**
     * Gets an optional integer parameter
     * @param parameters A hashmap of all the parameters
     * @param key The parameter key to look for
     * @param defaultVal The value to return if the optional parameter is not included
     * @return The value for the key, or the default value if the parameter doesn't exist
     */
    public int getParam(HashMap<String, String> parameters, String key, int defaultVal) {
        return parameters.containsKey(key) ? Integer.parseInt(parameters.get(key)) : defaultVal;
    }
}
