import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.*;

public class fibLoopBig {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    //static int numberOfTrials = 100000;  // can change 1-1000 for testing , set at 1000 for actual test
    static int numberOfTrials = 10;
    static int MAXINPUTSIZE  = 100;
    static int MININPUTSIZE  =  1;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    static String ResultsFolderPath = "/home/nicolocker/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        verifyWorks();
        System.out.println("\n");

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("FibLoopBig-Exp1.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("FibFormula-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("FibFormulaBig-Exp3.txt");
    }

    public static void verifyWorks(){
        System.out.println("\n------- Test Run ------");
        for(int i = 0; i <=20; i++){
            System.out.println(fibLoopBig(i));
        }
    }


    public static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#X(value)         N(size)        T(time)"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize++) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();


            // run the tirals
            for (long trial = 0; trial < numberOfTrials; trial++) {


                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                //    long foundIndex = binarySearch(testSearchKey, testList);

               // fibLoopBig.fibLoopBig(inputSize);
                // fibLoopBig.fibFormula(inputSize);
                fibLoopBig.fibFormulaBig(inputSize);

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            long N = (long)(Math.floor(Math.log(inputSize)/Math.log(2)));
            /* print data for this size of input */
            resultsWriter.printf("%6d  %15d %15.2f\n",inputSize, N, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }
    }

    public static long fibFormula(long num){
        double phi = (1 + Math.sqrt(5))/2;
        return Math.round(Math.pow(phi, num) / Math.sqrt(5));
    }


    public static BigDecimal fibFormulaBig(long num){
        BigDecimal phi = new BigDecimal((1 + Math.sqrt(5))/2);

        BigDecimal result = phi.pow((int) num);

        result = result.divide(BigDecimal.valueOf(Math.sqrt(5)), RoundingMode.FLOOR);

        return result;
    }

    // https://www.geeksforgeeks.org/large-fibonacci-numbers-java/
    public static BigInteger fibLoopBig(long num){
        BigInteger a = BigInteger.valueOf(0);
        BigInteger b = BigInteger.valueOf(1);
        BigInteger c = BigInteger.valueOf(1);

        for(int i = 2; i <= num; i++){
            c = a.add(b);
            a = b;
            b = c;
        }

        return a;
    }
}

