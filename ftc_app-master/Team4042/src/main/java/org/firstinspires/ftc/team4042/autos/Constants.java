package org.firstinspires.ftc.team4042.autos;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Created by Gautham on 12/19/2017.
 */

public class Constants {
    File file;
    private HashMap<String, String> myConstants;
    private static Constants c;


    public static Constants getInstance() {
        if(c == null){
            c = new Constants();
        }
        return c;
    }

    private Constants() {
        file = new File("./storage/emulated/0/DCIM/" + "constants.txt");
        myConstants = loadFile();
    }

    private HashMap<String, String> loadFile() {
        if (file == null) {
            return null;
        } //Can't load a null file

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (line != null && line.length() > 0 && line.charAt(0) != '#') { //Reads the first line
                HashMap<String, String> parameters = new HashMap<>();
                //x:3 --> k = x, v = 3
                String[] inputParameters = line.split(" ");
                int i = 0;
                while (i < inputParameters.length) {
                    String parameter = inputParameters[i];
                    int colon = parameter.indexOf(':');
                    String k = parameter.substring(0, colon);
                    String v = parameter.substring(colon + 1);
                    parameters.put(k, v); //Gets the next parameter and adds it to the list
                    i++;
                }
                return parameters;

            }
            fileReader.close();
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));

        }
        return null;
    }

    public String getString(String k) {
        //return c.getString(k);
        return getInstance().myConstants.get(k);
    }

    public int getInt(String k) {
        //return c.getInt(k);
        return Integer.parseInt(getInstance().myConstants.get(k));
    }

    public double getDouble(String k) {
        //return c.getDouble(k);
        return Double.parseDouble(getInstance().myConstants.get(k));
    }
}
