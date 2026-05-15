package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final double CONC = 440.0;

    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        int len = keyboard.length();
        GuitarString[] Guitar_key = new GuitarString[len];

        for (int i = 0; i < len; i++){
            Guitar_key[i] = new GuitarString((CONC * Math.pow(2.0, (i - 24.0)/ 12.0)));
        }

        while (true){
            if (StdDraw.hasNextKeyTyped()){
                char key = StdDraw.nextKeyTyped();
                if (keyboard.indexOf(key) != -1){
                    int index = keyboard.indexOf(key);
                    Guitar_key[index].pluck();
                }
            }
            double sample = 0;
            for (int i = 0; i < len; i++){
                sample += Guitar_key[i].sample();
            }
            StdAudio.play(sample);

            for (int i = 0; i < len; i++){
                Guitar_key[i].tic();
            }
        }
    }
}
