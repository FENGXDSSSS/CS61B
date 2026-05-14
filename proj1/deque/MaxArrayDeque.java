package deque;
import java.util.Comparator;
public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    public MaxArrayDeque(Comparator<Item> c){
        comp = c;
    }

    public Item max(){
        if (size == 0){
            return null;
        }
        Item max_item = get(0);
        for (int i = 1; i < size; i++){
            Item temp = get(i);
            if (comp.compare(temp, max_item) > 0){
                max_item = temp;
            }
        }
        return max_item;
    }

    public Item max(Comparator<Item> c){
        if (size == 0){
            return null;
        }
        Item max_item = get(0);
        for (int i = 1; i < size; i++){
            Item temp = get(i);
            if (c.compare(temp, max_item) > 0){
                max_item = temp;
            }
        }
        return max_item;
    }

    private Comparator<Item> comp;
}
