package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final CustomLinkedList<Task> historyTasks = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> storageNodes = new HashMap<>();


    @Override
    public void remove(Integer id) {
        if (storageNodes.containsKey(id)) {
            historyTasks.removeNode(storageNodes.remove(id));
        }
    }

    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        if (storageNodes.containsKey(task.getId())) {
            historyTasks.removeNode(storageNodes.get(task.getId()));
        }
        historyTasks.linkLast(node);
        storageNodes.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks.getTasks();
    }


    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;


        void linkLast(Node<T> node) {
            final Node<T> oldTail = tail;
            node.prev = oldTail;
            tail = node;
            if (oldTail == null) {
                head = node;
            } else {
                oldTail.next = node;
            }
        }


        List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<T> node = head;
            while (node != null) {
                tasks.add((Task) node.task);
                node = node.next;
            }
            return tasks;
        }

        void removeNode(Node<T> node) {
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            if (prevNode == null && nextNode == null) {
                head = null;
                tail = null;
            } else if (prevNode == null) {
                head = nextNode;
                nextNode.prev = null;
            } else if (nextNode == null) {
                tail = prevNode;
                prevNode.next = null;
            } else {
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }
        }
    }

    private static class Node<T> {
        public Node<T> prev;
        public T task;
        public Node<T> next;

        public Node(Node<T> prev, T task, Node<T> next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}