package org.firstinspires.ftc.team4042;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hazel on 10/18/2017.
 */

public class AutoInstruction {

    String functionName;
    private HashMap<String, String> parameters;

    public AutoInstruction() {
        functionName = "";
        parameters = new HashMap<>();
    }

    public AutoInstruction(String functionName, HashMap<String, String> parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public String getFunctionName() {
        return functionName;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String toReturn = functionName + " ";
        for (String parameter : parameters.keySet()) {
            toReturn += parameter + ":" + parameters.get(parameter) + " ";
        }
        return toReturn;
    }
}
