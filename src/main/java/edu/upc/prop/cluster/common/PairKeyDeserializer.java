package edu.upc.prop.cluster.common;

import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;


/**
 * Deserializador de claves para objetos {@code Pair} que utiliza el formato {@code {first, second}}.
 * Esta clase se utiliza para convertir una cadena de texto en una instancia de {@code Pair}.
 * @author Jorge Vico Lora
 */
public class PairKeyDeserializer extends KeyDeserializer {

    /**
     * Deserializa una clave en formato {@code {first, second}} y la convierte en un objeto {@code Pair}.
     * La clave debe estar entre llaves ({}) y ser una cadena con dos valores enteros separados por una coma.
     *
     * @param key la clave que se va a deserializar
     * @param ctxt el contexto de deserialización
     * @return una instancia de {@code Pair} con los valores deserializados
     * @throws IOException si ocurre un error al convertir la clave
     * @throws IllegalArgumentException si el formato de la clave es incorrecto
     */
    @Override
    public Object deserializeKey(String key, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Clave no puede ser nula");
        }

        // Quitar las llaves externas `{}` y separar por coma
        if (!key.startsWith("{") || !key.endsWith("}")) {
            throw new IllegalArgumentException("Clave no está en el formato esperado: " + key);
        }
        key = key.substring(1, key.length() - 1); // Elimina '{' y '}'
        String[] parts = key.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato inválido para Pair: {" + key + "}");
        }
        try {
            Integer first = Integer.parseInt(parts[0].trim());
            Integer second = Integer.parseInt(parts[1].trim());
            return new Pair<>(first, second);
        } catch (NumberFormatException e) {
            throw new IOException("Error al convertir la clave Pair: {" + key + "}", e);
        }
    }
}
