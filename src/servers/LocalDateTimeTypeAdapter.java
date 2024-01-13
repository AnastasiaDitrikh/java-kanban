package servers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTimeTypeAdapter - класс-адаптер для сериализации и десериализации объектов LocalDateTime в JSON и обратно.
 * Формат даты и времени для сериализации: "yyyy-MM-dd|HH:mm".
 */
public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm");

    /**
     * Метод write() выполняет сериализацию объекта LocalDateTime в JSON.
     *
     * @param jsonWriter    JsonWriter для записи JSON
     * @param localDateTime объект LocalDateTime для сериализации
     * @throws IOException возникает в случае ошибки записи в JsonWriter
     */
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(formatter));
        }
    }

    /**
     * Метод read() выполняет десериализацию объекта LocalDateTime из JSON.
     *
     * @param jsonReader JsonReader для чтения JSON
     * @return десериализованный объект LocalDateTime
     * @throws IOException возникает в случае ошибки чтения из JsonReader
     */
    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {

        return LocalDateTime.parse(jsonReader.nextString(), formatter);
    }
}