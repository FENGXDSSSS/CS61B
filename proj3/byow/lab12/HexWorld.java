package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;
    private static final Random RANDOM = new Random(new Date().getTime());

    public static Vector<Integer> helperGenera(TETile[][] world, int size, int last,
                                               int x, int y, TETile tile, int model) {

        if (last == 0) {
            Vector<Integer> pos = new Vector<>();
            pos.add(x);
            pos.add(y);
            return pos;
        }
        int k = x;
        for (int i = 0; i < size; i++) {
            world[k++][y] = tile;
        }
        if (model >= 0) {
            return helperGenera(world, size + 2, last - 1, x - 1, y - 1, tile, model);
        } else {
            return helperGenera(world, size - 2, last - 1, x + 1, y - 1, tile, model);
        }
    }

    public static void addHexagon(TETile[][] world, int size, int x, int y, TETile tile) {
        if (world[x][y] != Tileset.NOTHING) {
            return;
        }
        Vector<Integer> pos;
        pos = helperGenera(world, size, size, x, y, tile, 1);
        if (pos == null) {
            return;
        }
        int size2 = size + (2 * (size - 1));
        x = pos.get(0) + 1;
        y = pos.get(1);
        helperGenera(world, size2, size, x, y, tile, -1);
    }

    //获取周围坐标
    private static int[][] getAroundPos(int side, int x, int y) {
        int[][] b = new int[6][2];
        b[0][0] = x;
        b[0][1] = y + (side * 2);
        b[1][0] = x;
        b[1][1] = y - (side * 2);
        b[2][0] = x - (side * 2 - 1);
        b[2][1] = y + side;
        b[3][0] = x - (side * 2 - 1);
        b[3][1] = y - side;
        b[4][0] = x + side * 2 - 1;
        b[4][1] = y + side;
        b[5][0] = x + side * 2 - 1;
        b[5][1] = y - side;
        return b;
    }

    // 扩圈
    public static void helpHexagons(TETile[][] world, int side, int lost, int x, int y) {
        if (lost == 0) {
            return;
        }
        int[][] pos = getAroundPos(side, x, y);
        for (int i = 0; i < 6; i++) {
            addHexagon(world, side, pos[i][0], pos[i][1], randomTile());
            helpHexagons(world, side, lost - 1, pos[i][0], pos[i][1]);
        }

    }

    // 绘制组合六边形
    public static void tesselationOfHexagons(TETile[][] world, int size2, int size, int x, int y) {
        int lost = size2;
        addHexagon(world, size, x, y, randomTile());
        lost--;
        if (lost == 0) {
            return;
        }
        helpHexagons(world, size, lost, x, y);
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.FLOOR;
            default: return Tileset.TREE;
        }
    }

    // 初始化世界
    public static void initWorld(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        initWorld(world);
        tesselationOfHexagons(world, 4, 3, WIDTH / 2, HEIGHT / 2);
        ter.renderFrame(world);
    }
}
