package math;

import edu.princeton.cs.algs4.Stopwatch;

public class math {
    public static boolean dup1(int [] A) {
        for (int i = 0; i < A.length; i++) {
            for (int j = i + 1; j < A.length; j++){
                if (A[i] == A[j]) {
                    return true;
                }
            }
        }
        return false;
    }
    //如果再相同条件下运行dup2，那么在结果上会收敛到一个大约为2的数字上
    public static boolean dup2(int[] A) {
        for (int i = 0; i < A.length - 1; i++){
            if (A[i] == A[i + 1]) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int testsize = 1000;
        double lasttestruntime = 0.0;
        while (lasttestruntime < 10){
            int[] arr = new int[testsize];
            for (int j = 0; j < testsize; j++){
                arr[j] = j;
            }
            Stopwatch s = new Stopwatch();
            boolean b = dup2(arr);
            double newtime = s.elapsedTime();
            System.out.println("Test of size " + testsize + " " + newtime);
            testsize *= 2;
            lasttestruntime = newtime;
        }
    }
}
