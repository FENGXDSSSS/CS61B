package gh2;

/**
 * This code does some stuff. Run it (with sound on!) to find out what stuff it does!
 * Requires completion of CS 61B Homework 1.
 *
 * @author Eli Lipsitz
 */
public class Music {
    public static void main(String[] args) {
        GuitarPlayer player = new GuitarPlayer(new java.io.File(
                "C:\\FENG\\CS\\CS61B\\Midi\\source.mid"));
        player.play();
        // You can also do this:
        // GuitarPlayer player = new GuitarPlayer(new java.io.File("path/to/music.mid"));
        // player.play();
    }
}
