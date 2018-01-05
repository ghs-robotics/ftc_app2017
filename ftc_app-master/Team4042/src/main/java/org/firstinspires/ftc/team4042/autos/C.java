package org.firstinspires.ftc.team4042.autos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Created by Gautham on 12/19/2017.
 */

public class C {
    File file;
    private HashMap<String, String> myConstants;
    private static C c;


    public static C get() {
        if(c == null){
            c = new C();
        }
        return c;
    }

    private C() {
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

    public String getString(String constant) {
        //return c.getString(constant);
        return get().myConstants.get(constant);
    }

    public int getInt(String constant) {
        //return c.getInt(constant);
        return Integer.parseInt(get().myConstants.get(constant));
    }

    public double getDouble(String constant) {
        //return c.getDouble(constant);
        return Double.parseDouble(get().myConstants.get(constant));
    }
}
