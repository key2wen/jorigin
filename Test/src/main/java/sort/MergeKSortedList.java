package sort;

/**
 * https://blog.csdn.net/baidu_37366272/article/details/92905407
 * <p>
 * https://www.cnblogs.com/gjmhome/p/14406726.html
 * 1. 题目描述
 * <p>
 * You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 * 翻译：
 * 给定一个链表长度为k的链表数组，每个链表按升序排序。
 * 将数组中所有的链表合并为一个有序的链表，并返回它。
 * Input: lists = [[1,4,5],[1,3,4],[2,6]]
 * Output: [1,1,2,3,4,4,5,6]
 * Explanation: The linked-lists are:
 * [
 * 1->4->5,
 * 1->3->4,
 * 2->6
 * ]
 * merging them into one sorted list:
 * 1->1->2->3->4->4->5->6
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

public class MergeKSortedList {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0 || lists[0] == null) {
            return null;
        }

        ListNode head = null;//新建的链表头结点
        for (int i = 0; i < lists.length; i++) {
            ListNode listnode = lists[i];  //取出数组中的每一个链表
            head = merge2Link(head, listnode);//合并
        }
        return head;
    }

    /**
     * 递归思路
     *
     * @param head
     * @param tail
     * @return
     */
    public ListNode merge2Link(ListNode head, ListNode tail) {
        if (tail == null)
            return head;
        if (head == null)
            return tail;
        if (head.val < tail.val) {
            head.next = merge2Link(head.next, tail);//递归
            return head;
        } else {
            tail.next = merge2Link(head, tail.next);//递归
            return tail;
        }
    }

    /*
    接下来就用到归并排序思想了
    */
    public ListNode merge2Link2(ListNode node1, ListNode node2) {
        ListNode res = new ListNode(0);//返回时，将res.next返回
        ListNode head = res;//中间指针变量，下面的循环用到的是这个指针
        while (node1 != null && node2 != null) {
            if (node1.val < node2.val) {
                head.next = node1;
                node1 = node1.next;
            } else {
                head.next = node2;
                node2 = node2.next;
            }
            head = head.next;
        }
        while (node1 != null) {
            head.next = node1;
        }
        while (node2 != null) {
            head.next = node2;
        }
        return res.next;
    }
}