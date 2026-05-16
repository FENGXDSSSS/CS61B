package deque;
import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }
        T max_item = get(0);
        for (int i = 1; i < size(); i++) {
            T temp = get(i);
            if (comp.compare(temp, max_item) > 0) {
                max_item = temp;
            }
        }
        return max_item;
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T max_item = get(0);
        for (int i = 1; i < size(); i++) {
            T temp = get(i);
            if (c.compare(temp, max_item) > 0) {
                max_item = temp;
            }
        }
        return max_item;
    }

    private Comparator<T> comp;
}
