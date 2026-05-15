package gh2;

import deque.Deque;
import deque.LinkedListDeque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. 用于存储声音的缓存区*/
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  创建一个给定频率的琴弦*/
    public GuitarString(double frequency) {
        buffer = new LinkedListDeque<Double>();
        int capacity = (int)Math.round(SR / frequency);

        for (int i = 0; i < capacity; i++){
            buffer.addFirst(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. 用白噪音替换缓存器，然后弹奏吉他弦*/
    public void pluck() {
        double r = 0.0;
        int size = buffer.size();
        for (int i = 0; i < size; i++){
            r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addLast(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm. 通过执行一次 Karplus-Strong 算法的迭代，讲模拟推进一个步
     */
    public void tic() {
        double first, second;
        first = buffer.removeFirst();
        second = buffer.get(0);
        double newDouble = (first + second) / 2.0 * DECAY;
        buffer.addLast(newDouble);
    }

    /* Return the double at the front of the buffer. 返回缓冲区前端的双精度浮点数*/
    public double sample() {
        return buffer.get(0);
    }
}
