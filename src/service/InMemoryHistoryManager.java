package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    CustomLinkedList<Task> historyTasks = new CustomLinkedList<>();
    HashMap<Integer, Node> storageNodes = new HashMap<>();


    @Override
    public void remove(Integer id) {
        historyTasks.removeNode(storageNodes.get(id));
        storageNodes.remove(id);
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


}

class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;


    void linkLast(Node node) {
        final Node<T> oldTail = tail;
        node.prev = oldTail;
        tail = node;
        if (oldTail == null)
            head = node;
        else
            oldTail.next = node;
    }

    List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add((Task) node.task);
            node = node.next;
        }
        return tasks;
    }

    void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            node = null;
        }
        if (prevNode == null && nextNode != null) {
            head = nextNode;
            nextNode.prev = null;
        }
        if (prevNode != null && nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        }
        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}

class Node<T> {
    public Node<T> prev;
    public T task;
    public Node<T> next;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }
}