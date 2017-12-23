package org.firstinspires.ftc.team4042.autos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hazel on 10/18/2017.
 *
 * A single instruction to execute in auto. Stores things like the name of the function and the parametes to it.
 */

public class AutoInstruction {

    String functionName;
    private HashMap<String, String> parameters;

    public AutoInstruction() {
        functionName = "";
        parameters = new HashMap<>();
    }

    public AutoInstruction(HashMap<String, String> parameters) {
        this.functionName = parameters.get("function");
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
        StringBuilder toReturn = new StringBuilder(functionName + " ");
        for (String parameter : parameters.keySet()) {
            toReturn.append(parameter).append(":").append(parameters.get(parameter)).append(" ");
        }
        return toReturn.toString();
    }
}
