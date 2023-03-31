package algorithm;

import java.util.Iterator;

/**
 * https://blog.csdn.net/qq_42977003/article/details/122333902
 *
 * @param <T>
 */
public class HeapMax<T extends Comparable<T>> implements Iterable {
    private T[] items;
    private int len;

    HeapMax() {
        items = (T[]) new Comparable[16];
        this.len = 0;
    }

    HeapMax(int capacity) {
        items = (T[]) new Comparable[capacity];
        this.len = 0;
    }

    private boolean compareIsLittle(Comparable e1, Comparable e2) {
        return e1.compareTo(e2) < 0;
    }

    private void exchange(Comparable[] arr, int e1, int e2) {
        Comparable temp = arr[e1];
        arr[e1] = arr[e2];
        arr[e2] = temp;
    }

    public int size() {
        return this.len;
    }

    public void clear() {
        items = null;
        this.len = 0;
    }

    public boolean isEmpty() {
        return this.len == 0;
    }

    public void insert(T t) {
        //注意0为存储元素，废弃了
        if (len == items.length - 1) {
            resize((int) (items.length * 1.5));
        }
        //数组索引0废弃掉了，方便索引计算
        items[++len] = t;
        //进行上浮操作，也就是排序(比较排序)
        swim(len);
    }

    private void swim(int len) {
        //0废弃了，没有元素
        //思考一下，为什么不是len>0
        //最大堆必须保持，父节点大于等于子节点，len代表传进来的子节点，如果是左子节点，则len可以为1，如果len代表的是右子节点，则父节点就不存在了
        //父节点索引=左子节点索引/2=右子节点索引/2-1
        //边界是存在右子节点
        while (len > 1) {
            //子节点和父节点进行比较,满足条件进行位置交换
            if (!compareIsLittle(items[len], items[len / 2])) {
                exchange(items, len, len / 2);
            }
            //当父节点为1时就结束循环了
            len /= 2;
        }
    }

    public T maxEle() {
        if (this.len == 0) {
            return null;
        }
        return items[1];
    }

    public T delMax() {
        if (len == items.length / 4) {
            resize(items.length / 2);
        }
        //该堆是最大堆，索引1存的最大元素
        T max = items[1];
        //堆删除元素其实就是维护一个完全二叉树
        //将堆的最后一个元素的值赋给索引1后,就变成了一颗完全二叉树(无序),然后堆的长度-1
        items[1] = items[len];
        items[len] = null;
        this.len--;
        //最大堆必须满足父节点大于等于子节点，所以需要对索引1出的值进行下浮(排序)操作
        sink(1);
        return max;
    }

    private void sink(int k) {
        //下沉的操作比上浮的操作复杂
        //最大堆必须保持，父节点大于等于子节点，首先我们需要找出子节点种较大的，然后进行下沉操作
        //思考一下这个地方为什么不是k<=n和2*k+1<=n，而是2*k<=n
        //k<=len时: 代表传入的是最有一个节点，不需要进行下沉操作了
        //2*k+1<=len: 时代表传入的节点的两个子节点都存在,不能算作边界条件
        //2*k<=len:  时代表传入的节点存在左子节点，不一定存在右子节点
        //左子节点索引=父节点索引*2   右子节点索引=父节点索引*2+1
        while (2 * k <= len) {
            //定义一个变量max,获取到左右子节点种较大节点的索引
            int max;
            //判断是否存在右子节点
            if (2 * k + 1 <= len) {
                //找出较大节点的索引
                if (!compareIsLittle(items[2 * k], items[2 * k + 1])) {
                    max = 2 * k + 1;
                } else {
                    max = 2 * k;
                }
            } else {
                //不存在右子节点，直接将左子节点的索引赋给max
                max = 2 * k;
            }
            //父节点与子节点进行下沉操作(排序)
            if (compareIsLittle(items[k], items[max])) {
                exchange(items, k, max);
                k *= 2;
            } else {
                //当父节点大于等于子节点时说明已经排好序，跳出循环
                break;
            }
        }
    }

    //扩容，缩容
    public void resize(int capacity) {
        Comparable[] temp = items;
        items = (T[]) new Comparable[capacity];
        //注意从索引1开始
        for (int i = 1; i <= temp.length; i++) {
            items[i] = (T) temp[i];
        }
    }

    public boolean contain(T t) {
        //思考一下能够使用二叉查找的方法查找t吗
        //最大堆只是说父节点大于等于子节点，并没有说左子树大于右子树，从二叉树的定义每个节点做多有两个节点上，堆是二叉树，但是不满足二叉树的左子树大于右子树的特点
        //这里使用遍历数组的方式判断堆种是否包含t
        for (int i = 1; i <= len; i++) {
            if (items[i] == t) {
                return true;
            }
        }
        return false;
    }


    private class HIterator implements Iterator {
        int n;

        HIterator() {
            n = 1;
        }

        @Override
        public boolean hasNext() {
            return n <= len;
        }

        @Override
        public Object next() {
            return items[n++];
        }
    }

    @Override
    public Iterator iterator() {
        return new HIterator();
    }
}

class HeapMaxTest {
    public static void main(String[] args) {
        HeapMax<Integer> heapMax = new HeapMax<>();
        heapMax.insert(3);
        heapMax.insert(2);
        heapMax.insert(5);
        heapMax.insert(1);
        heapMax.insert(9);
        heapMax.insert(7);
        heapMax.insert(8);
        heapMax.insert(6);

        System.out.println(heapMax.maxEle());
        System.out.println(heapMax.size());


        System.out.println("-----------foreach遍历--------------");
        //HeapMax需要实现Iterator方法，自定义私有类实现类Iterator接口重写里面的方法
        for (Object o : heapMax) {
            System.out.println(o);
        }
//
        System.out.println("----------------------------");
        System.out.println(heapMax.delMax());
        System.out.println(heapMax.size());
        System.out.println(heapMax.contain(2));
        heapMax.clear();
        System.out.println(heapMax.size());
    }
}



