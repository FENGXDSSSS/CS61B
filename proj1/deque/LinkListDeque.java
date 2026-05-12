package deque;

public class LinkListDeque<Item> {
    private class node{
        public node(node l, Item x, node r){
            prev = l;
            item = x;
            next = r;
        }

        public Item item;
        public node next;
        public node prev;
    }
    // 空链表构造
    public LinkListDeque(){
        sentinel = new node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }
    // 赋值链表构造
    public LinkListDeque(Item x){
        // 定义哨兵节点
        sentinel = new node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        // 定义新节点以连接哨兵节点
        node p = new node(sentinel.next, x, sentinel);
        sentinel.next = p;
        sentinel.prev = p;

        size = 1;
    }

    public void addFirst(Item x){
        node p = new node(sentinel, x, sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;

        size++;
    }

    public void addLast(Item x){
        node p = new node(sentinel.prev, x, sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;

        size++;
    }

    public boolean isEmpty(){
        if (size == 0){
            return true;
        }else{
            return false;
        }
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        node p = sentinel.next;
        while (p.next == sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public Item removeFirst(){
        if (size == 0){
            return null;
        }
        node temp = sentinel.next;

        sentinel.next = temp.next;
        temp.next.prev = sentinel;

        size--;
        return temp.item;
    }

    public Item removeLast(){
        if (size == 0){
            return null;
        }
        node temp = sentinel.prev;

        sentinel.prev = temp.prev;
        temp.prev.next = sentinel;

        size--;
        return temp.item;
    }

    public Item get(){
        if (size == 0){
            return null;
        }

        
    }

    private node sentinel;
    private int size;
}
