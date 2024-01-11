package managers.exceptions;

/**
 * Исключение, выбрасываемое при ошибке валидации задачи.
 */
public class TaskValidationException extends RuntimeException {

    /**
     * Создает новый экземпляр TaskValidationException с указанным сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public TaskValidationException(String message) {
        super(message);
    }
}