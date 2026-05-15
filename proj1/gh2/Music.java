package gh2;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * This code does some stuff. Run it (with sound on!) to find out what stuff it does!
 * Requires completion of CS 61B Homework 1.
 *
 * @author Eli Lipsitz
 */
public class Music {
    public static void main(String[] args) {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("C:\\FENG\\CS\\CS61B\\Midi\\source.mid"));
        player.play();
        // You can also do this:
        // GuitarPlayer player = new GuitarPlayer(new java.io.File("path/to/music.mid"));
        // player.play();
    }
}
