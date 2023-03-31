package algorithm;

import java.util.Stack;

/**
 * NC78 反转链表 ： https://www.nowcoder.com/practice/75e878df47f24fdc9dc3e400ec6058ca?tpId=117&tqId=37777&rp=1&ru=/exam/oj&qru=/exam/oj&sourceUrl=%2Fexam%2Foj%3Fpage%3D1%26tab%3D%25E7%25AE%2597%25E6%25B3%2595%25E7%25AF%2587%26topicId%3D117&difficulty=undefined&judgeStatus=undefined&tags=&title=
 * 描述
 * 给定一个单链表的头结点pHead(该头节点是有值的，比如在下图，它的val是1)，长度为n，反转该链表后，返回新链表的表头。
 * <p>
 * 数据范围： 0\leq n\leq10000≤n≤1000
 * 要求：空间复杂度 O(1)O(1) ，时间复杂度 O(n)O(n) 。
 * <p>
 * 如当输入链表{1,2,3}时，
 * 经反转后，原链表变为{3,2,1}，所以对应的输出为{3,2,1}。
 * 以上转换过程如下图所示：
 */
public class ReverseLinkedList {

    class ListNode {
        int val;
        ListNode next = null;

        ListNode(int val) {
            this.val = val;
        }
    }

    /**
     * 1，使用栈解决
     * 链表的反转是老生常谈的一个问题了，同时也是面试中常考的一道题。最简单的一种方式就是使用栈，因为栈是先进后出的。实现原理就是把链表节点一个个入栈，当全部入栈完之后再一个个出栈，出栈的时候在把出栈的结点串成一个新的链表。原理如下
     *
     * @param head
     * @return
     */
    public ListNode reverseList(ListNode head) {
        Stack<ListNode> stack = new Stack<>();
        //把链表节点全部摘掉放到栈中
        while (head != null) {
            stack.push(head);
            head = head.next;
        }
        if (stack.isEmpty())
            return null;
        ListNode node = stack.pop();
        ListNode dummy = node;
        //栈中的结点全部出栈，然后重新连成一个新的链表
        while (!stack.isEmpty()) {
            ListNode tempNode = stack.pop();
            node.next = tempNode;
            node = node.next;
        }
        //最后一个结点就是反转前的头结点，一定要让他的next
        //等于空，否则会构成环
        node.next = null;
        return dummy;
    }

    /**
     * 2,双链表求解
     * 双链表求解是把原链表的结点一个个摘掉，每次摘掉的链表都让他成为新的链表的头结点，然后更新新链表。下面以链表1→2→3→4为例画个图来看下。
     * 他每次访问的原链表节点都会成为新链表的头结点，最后再来看下代码
     *
     * @param head
     * @return
     */
    public ListNode reverseList2(ListNode head) {
        //新链表
        ListNode newHead = null;
        while (head != null) {
            //先保存访问的节点的下一个节点，保存起来
            //留着下一步访问的
            ListNode temp = head.next;
            //每次访问的原链表节点都会成为新链表的头结点，
            //其实就是把新链表挂到访问的原链表节点的
            //后面就行了
            head.next = newHead;
            //更新新链表
            newHead = head;
            //重新赋值，继续访问
            head = temp;
        }
        //返回新链表
        return newHead;
    }

    /**
     * 3,递归解决
     * 我们再来回顾一下递归的模板，终止条件，递归调用，逻辑处理。
     *
     * @param head
     * @return
     */
    public ListNode reverseList3(ListNode head) {
        //终止条件
        if (head == null || head.next == null)
            return head;
        //保存当前节点的下一个结点
        ListNode next = head.next;
        //从当前节点的下一个结点开始递归调用
        ListNode reverse = reverseList3(next);
        //reverse是反转之后的链表，因为函数reverseList
        // 表示的是对链表的反转，所以反转完之后next肯定
        // 是链表reverse的尾结点，然后我们再把当前节点
        //head挂到next节点的后面就完成了链表的反转。
        next.next = head;
        //这里head相当于变成了尾结点，尾结点都是为空的，
        //否则会构成环
        head.next = null;
        return reverse;
    }

    /**
     * 上面这种递归往下传递的时候基本上没有逻辑处理，当往回反弹的时候才开始处理，也就是从链表的尾端往前开始处理的。我们还可以再来改一下，
     * 在链表递归的时候从前往后处理，处理完之后直接返回递归的结果，这就是所谓的尾递归，这种运行效率要比上一种好很多
     * 尾递归虽然也会不停的压栈，但由于最后返回的是递归函数的值，所以在返回的时候都会一次性出栈，不会一个个出栈这么慢。
     *
     * @param head
     * @return
     */
    public ListNode ReverseList4(ListNode head) {
        return reverseListInt4(head, null);
    }

    private ListNode reverseListInt4(ListNode head, ListNode newHead) {
        if (head == null)
            return newHead;
        ListNode next = head.next;
        head.next = newHead;
        return reverseListInt4(next, head);
    }
}