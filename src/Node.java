import java.util.HashMap;
import java.util.Map;

/**
 * @author zzy
 * @description
 * @date 2022/4/14 21:48
 */


public class Node {
    private String item;
    private Integer count;

    // 用于FP Tree中记录父节点与子节点
    private Node parent;
    private Map<String, Node> children;

    // 用于head中的dummy节点记录首节点与尾节点
    private Node head;
    private Node tail;

    // 在head链表中与下一项相连
    private Node next;

    Node() {
        this.count = 0;
        this.children = new HashMap<>();
    }

    public Node(String item) {
        this.item = item;
        this.count = 0;
        this.children = new HashMap<>();
    }

    Node(String item, Node parent) {
        this.item = item;
        this.parent = parent;
        this.count = 0;
        this.children = new HashMap<>();
    }

    Node(Integer count) {
        this.count = count;
    }

    void add(Node node) {
        if(head == null) {
            head = tail = node;
        } else {
            tail.setNext(node);
            tail = node;
        }
    }

    void increase(int num) {
        this.count += num;
    }

    String getItem() {
        return item;
    }

    void setItem(String item) {
        this.item = item;
    }

    Integer getCount() {
        return count;
    }

    void setCount(Integer count) {
        this.count = count;
    }

    Node getParent() {
        return parent;
    }

    void setParent(Node parent) {
        this.parent = parent;
    }

    Map<String, Node> getChildren() {
        return children;
    }

    void setChildren(Map<String, Node> children) {
        this.children = children;
    }

    Node getNext() {
        return next;
    }

    void setNext(Node next) {
        this.next = next;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }
}
