package algorithm.jiuzhang;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 题3. 简单实现一个拓扑排序，用于规划任务执行的先后顺序。
 * 如任务A依赖于任务B记成 A -> B
 * 实现： A->C B->D C->E B->C D->E F->A F->B
 * 最终算出执行顺序为 E C A D B F
 */
class MultiProcessor {

    public static void main(String[] args) {
        Task taskA = new Task(Node.A);
        Task taskB = new Task(Node.B);
        Task taskC = new Task(Node.C);
        Task taskD = new Task(Node.D);
        Task taskE = new Task(Node.E);
        Task taskF = new Task(Node.F);
        taskA.addPreTask(taskC);
        taskB.addPreTask(taskD, taskC);
        taskC.addPreTask(taskE);
        taskD.addPreTask(taskE);
        taskF.addPreTask(taskA, taskB);
        Stack<Task> stackTasks = new Stack<>();
        stackTasks.add(taskF); //F作为其实起始任务
        MultiProcessor.sortedRunTasks(stackTasks);
    }

    /**
     * 思路：用栈的形式，将依赖的节点依次入栈，再依次出栈即可
     *
     * @param stackTasks 要遍历的栈，已经将首节点任务放入其中
     */
    public static void sortedRunTasks(Stack<Task> stackTasks) {
        Task task = stackTasks.peek(); //
        while (!task.preTaskList.isEmpty()) {
            for (Task pre : task.preTaskList) {
                stackTasks.add(pre); //入栈
            }
            task = stackTasks.peek();
        }
        while (!stackTasks.isEmpty()) {
            stackTasks.pop().run(); //出栈
        }
    }
}

class Task {
    Node self;
    List<Task> preTaskList = new ArrayList<>(); //当前节点依赖的之后节点

    public Task(Node self) {
        this.self = self;
    }

    public void addPreTask(Task... preTask) {
        for (Task pre : preTask) {
            this.preTaskList.add(pre);
        }
    }

    public void run() {
        System.out.print(self.name() + " ");
    }
}

enum Node {
    A, B, C, D, E, F
}
