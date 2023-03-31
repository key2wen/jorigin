package algorithm;

import java.util.*;

public class LRUCache {
    //设置双向链表结构
    static class Node {
        int key;
        int val;
        Node pre;
        Node next;

        //初始化
        public Node(int key, int val) {
            this.key = key;
            this.val = val;
            this.pre = null;
            this.next = null;
        }
    }

    //哈希表
    private Map<Integer, Node> mp = new HashMap<>();
    //设置一个头
    private Node head = new Node(-1, -1);
    //设置一个尾
    private Node tail = new Node(-1, -1);
    private int size = 0;

    public int[] LRU(int[][] operators, int k) {
        //构建初始化连接
        //链表剩余大小
        this.size = k;
        this.head.next = this.tail;
        this.tail.pre = this.head;
        //获取操作数
        int len = (int) Arrays.stream(operators).filter(x -> x[0] == 2).count();
        int[] res = new int[len];
        //遍历所有操作
        for (int i = 0, j = 0; i < operators.length; i++) {
            if (operators[i][0] == 1)
                //set操作
                set(operators[i][1], operators[i][2]);
            else
                //get操作
                res[j++] = get(operators[i][1]);
        }
        return res;
    }

    //插入函数
    private void set(int key, int val) {
        //没有见过这个key，新值加入
        if (!mp.containsKey(key)) {
            Node node = new Node(key, val);
            mp.put(key, node);
            //超出大小，移除最后一个
            if (size <= 0)
                removeLast();
                //大小还有剩余
            else
                //大小减1
                size--;
            //加到链表头
            insertFirst(node);
        }
        //哈希表中已经有了，即链表里也已经有了
        else {
            mp.get(key).val = val;
            //访问过后，移到表头
            moveToHead(mp.get(key));
        }
    }

    //获取数据函数
    private int get(int key) {
        int res = -1;
        if (mp.containsKey(key)) {
            Node node = mp.get(key);
            res = node.val;
            moveToHead(node);
        }
        return res;
    }

    //移到表头函数
    private void moveToHead(Node node) {
        //已经到了表头
        if (node.pre == head)
            return;
        //将节点断开，取出来
        node.pre.next = node.next;
        node.next.pre = node.pre;
        //插入第一个前面
        insertFirst(node);
    }

    //将节点插入表头函数
    private void insertFirst(Node node) {
        node.pre = head;
        node.next = head.next;
        head.next.pre = node;
        head.next = node;
    }

    //删去表尾函数，最近最少使用
    private void removeLast() {
        //哈希表去掉key
        mp.remove(tail.pre.key);
        //断连该节点
        tail.pre.pre.next = tail;
        tail.pre = tail.pre.pre;
    }
}

