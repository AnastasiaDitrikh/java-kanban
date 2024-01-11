package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс реализует управление историей в памяти (в качестве хранилища используется Map и CustomLinkedList)
 */

public class InMemoryHistoryManager implements HistoryManager {


    private final CustomLinkedList<Task> historyTasks = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> storageNodes = new HashMap<>();

    /**
     * Удаляет задачу из истории по ее идентификатору.
     *
     * @param id Идентификатор задачи.
     */
    @Override
    public void remove(Integer id) {
        if (storageNodes.containsKey(id)) {
            historyTasks.removeNode(storageNodes.remove(id));
        }
    }

    /**
     * Добавляет задачу в историю.
     *
     * @param task Задача, которую необходимо добавить в историю.
     */
    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        if (storageNodes.containsKey(task.getId())) {
            historyTasks.removeNode(storageNodes.get(task.getId()));
        }
        historyTasks.linkLast(node);
        storageNodes.put(task.getId(), node);
    }

    /**
     * Возвращает список задач из истории.
     *
     * @return Список задач из истории.
     */
    @Override
    public List<Task> getHistory() {
        return historyTasks.getTasks();
    }

    /**
     * Вспомогательный класс CustomLinkedList для хранения и управления связанным списком
     *
     * @param <T> Тип элементов в списке.
     */
    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        /**
         * Добавляет новый узел в конец списка.
         *
         * @param node Узел, который будет добавлен в список.
         */
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

        /**
         * Возвращает список задач из связанного списка.
         *
         * @return Список задач из связанного списка.
         */
        List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<T> node = head;
            while (node != null) {
                tasks.add((Task) node.task);
                node = node.next;
            }
            return tasks;
        }

        /**
         * Удаляет указанный узел из связанного списка.
         *
         * @param node Узел, который будет удален из списка.
         */
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

    /**
     * Вспомогательный класс Node для представления узла в связанном списке.
     *
     * @param <T> Тип элемента в узле.
     */
    private static class Node<T> {
        public Node<T> prev;
        public T task;
        public Node<T> next;

        /**
         * Создает новый узел с указанными значениями prev, task и next.
         *
         * @param prev Предыдущий узел в списке.
         * @param task Задача, которая хранится в узле.
         * @param next Следующий узел в списке.
         */
        public Node(Node<T> prev, T task, Node<T> next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}