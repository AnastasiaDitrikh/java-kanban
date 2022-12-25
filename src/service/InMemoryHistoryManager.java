package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    CustomLinkedList<Task> historyTasks = new CustomLinkedList<>();
    HashMap<Integer, Node> storageNodes = new HashMap<>();


    @Override
    public void remove(Integer id) {
        if (storageNodes.containsKey(id)) {
            historyTasks.removeNode(storageNodes.get(id));
            storageNodes.remove(id);
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

    class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;


        void linkLast(Node node) {
            final Node<T> oldTail = tail;
            node.prev = oldTail;
            tail = node;
            if (oldTail == null) { //исправлено
                head = node;
            } else {
                oldTail.next = node;
            }
        }

    /* Нарушение кодстайла сязано с примером, представленным в примере практикума, по созданию двусвязанного списка)
    тема LinkedList, возможно это и объясняет схожесть или, может сязано с тем,
    что у нас был созвон с некоторыми студентами, обсуждали проект, но тем не менее у меня проект ушло не меньше 15 часов,
    переделывала самостоятельно несколько раз, советовалась и искала ошибки с наставником
    *     public void addLast(T element) {
			final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(null, element, oldTail);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.prev = newNode;
        size++;
    } */

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
}