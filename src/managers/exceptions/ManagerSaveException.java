package managers.exceptions;

/**
 * Исключение, выбрасываемое при ошибке сохранения объекта в менеджере.
 */
public class ManagerSaveException extends RuntimeException {
    /**
     * Создает новый экземпляр ManagerSaveException с указанным сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public ManagerSaveException(String message) {
        super(message);
    }

    /**
     * Создает новый экземпляр ManagerSaveException с указанным сообщением об ошибке и исключением.
     *
     * @param message Сообщение об ошибке.
     * @param e       Исключение, вызвавшее ошибку.
     */
    public ManagerSaveException(String message, Exception e) {
    }
}