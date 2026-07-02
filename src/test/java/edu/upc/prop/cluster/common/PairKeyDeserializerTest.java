package edu.upc.prop.cluster.common;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alejandro Ruiz Patón
 */
public class PairKeyDeserializerTest {
    private final PairKeyDeserializer deserializer = new PairKeyDeserializer();

    @Test
    // El formato es válido y se deserializa convirtiendose en un Pair<Int,Int>
    public void testValidKey() throws IOException {
        String key = "{1,2}";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Object result = deserializer.deserializeKey(key, mockContext);

        assertTrue(result instanceof Pair);
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) result;
        assertEquals(1, pair.first());
        assertEquals(2, pair.second());
    }

    @Test
    // El formato admite espacios
    public void testValidKeyWithSpace() throws IOException {
        String key = "{ 42 , 7 }";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Object result = deserializer.deserializeKey(key, mockContext);

        assertTrue(result instanceof Pair);
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) result;
        assertEquals(42, pair.first());
        assertEquals(7, pair.second());
    }

    @Test
    // El formato no admite la ausencia de {}
    public void testInvalidKeyFormat() {
        String key = "1,2";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserializeKey(key, mockContext);
        });
        assertEquals("Clave no está en el formato esperado: " + key, exception.getMessage());
    }

    @Test
    // El formato no admite que dentro de {} no exista una coma separando los dos valores
    public void testInvalidKeyFormatMissingComma() {
        String key = "{1}";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserializeKey(key, mockContext);
        });
        assertEquals("Formato inválido para Pair: " + key, exception.getMessage());
    }

    @Test
    // El formato solo admite pares de enteros
    public void testInvalidKeyNonNumericValues() {
        String key = "{a,b}";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Exception exception = assertThrows(IOException.class, () -> {
            deserializer.deserializeKey(key, mockContext);
        });
        assertEquals("Error al convertir la clave Pair: " + key, exception.getMessage());
    }

    @Test
    // El formato no admite claves vacías
    public void testEmptyKey() {
        String key = "";
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserializeKey(key, mockContext);
        });
        assertEquals("Clave no está en el formato esperado: " + key, exception.getMessage());
    }

    @Test
    public void testNullKey() {
        String key = null;
        DeserializationContext mockContext = Mockito.mock(DeserializationContext.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserializeKey(key, mockContext);
        });
        assertEquals("Clave no puede ser nula", exception.getMessage());
    }
}
