package interview.task.services;

import interview.task.models.Form;

import java.util.*;

public class DataContainer {

    public static int threadNumber = 0;

    public static int newsNumber = 0;

    public static int bufferLimit = 0;

    public static int newsPerCycleByOneThread = 0;

    public static List<String> banWords = new ArrayList<>();

    public static int [] newsPerThread;

    public static final Map<String, List<Form>> pairs = new HashMap<>();

/*    public static void calculateNewsPerThread() {
        int[] result = new int[threadNumber];
        int intPart = newsNumber / threadNumber;
        int fracPart = newsNumber % threadNumber;
        Arrays.fill(result, intPart);
        Arrays.fill(result,0, fracPart, (intPart + 1));
        newsPerThread =  result;
    }*/
}
