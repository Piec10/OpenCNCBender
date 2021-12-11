package com.opencncbender.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piec on 2021-12-10.
 */

public class BendFileReader {

    public BendFileReader() {

    }

    public static List<SingleStep> readFile(String filepath){

        List<SingleStep> bendingSteps = new ArrayList<>();

        try(BufferedReader inputFile = new BufferedReader(new FileReader(filepath))) {

            String line;
            SingleStep readSingleStep;

            while((line = inputFile.readLine()) != null){

                readSingleStep = processSingleLine(line);
                bendingSteps.add(readSingleStep);
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return bendingSteps;
    }

    private static SingleStep processSingleLine(String singleLine){

        SingleStep readSingleStep;
        double readDistanceX, readAngleA, readAngleB;

        String[] singleBendLine = singleLine.split(" ");
        int stringLength = singleBendLine.length;

        if(stringLength == 1){

            readDistanceX = Double.parseDouble(singleBendLine[0].substring(1));

            readSingleStep = new SingleStep(readDistanceX);
        }
        else if(stringLength == 2){

            readDistanceX = Double.parseDouble(singleBendLine[0].substring(1));
            readAngleA = Double.parseDouble(singleBendLine[1].substring(1));

            readSingleStep = new SingleStep(readDistanceX,readAngleA);
        }
        else{

            readDistanceX = Double.parseDouble(singleBendLine[0].substring(1));
            readAngleA = Double.parseDouble(singleBendLine[1].substring(1));
            readAngleB = Double.parseDouble(singleBendLine[2].substring(1));

            readSingleStep = new SingleStep(readDistanceX,readAngleA,readAngleB);
        }

        return readSingleStep;
    }
}

