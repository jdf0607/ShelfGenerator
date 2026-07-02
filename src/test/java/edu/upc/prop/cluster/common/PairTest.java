package edu.upc.prop.cluster.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alejandro Ruiz Patón
 */
public class PairTest {

    @Test
    public void testConstructorAndGetters() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");

        assertEquals(1, pair.first());
        assertEquals("uno", pair.second());
    }


    @Test
    public void testSetters() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");

        pair.setFirst(2);
        assertEquals(2, pair.first());

        pair.setSecond("dos");
        assertEquals("dos", pair.second());
    }


    @Test
    public void testEqualsAndHashCodeWithSameValues() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");
        Pair<Integer, String> pair2 = new Pair<>(1, "uno");

        assertEquals(pair, pair2);
        assertEquals(pair.hashCode(), pair2.hashCode());
    }


    @Test
    public void testEqualsAndHashCodeWithDifferentValues() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");
        Pair<Integer, String> pair2 = new Pair<>(2, "dos");

        assertNotEquals(pair, pair2);
        assertNotEquals(pair.hashCode(), pair2.hashCode());
    }


    @Test
    public void testEqualsWithDifferentTypes() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");
        Pair<Integer, Integer> pair2 = new Pair<>(1, 1);
        String s = new String("uno");

        assertNotEquals(pair, pair2);
        assertNotEquals(pair, s);
    }


    @Test
    public void testEqualsWithSameReference() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");

        assertEquals(pair, pair);
    }


    @Test
    public void testEqualsNull() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");

        assertNotEquals(pair, null);
    }


    @Test
    public void testHashCode() {
        Pair<Integer, String> pair = new Pair<>(1, "uno");

        int hash1 = pair.hashCode();
        int hash2 = pair.hashCode();

        assertEquals(hash1, hash2);
    }


    @Test
    public void testPairWithNullValues() {
        Pair<Integer, String> pair = new Pair<>(null, null);

        assertNull(pair.first());
        assertNull(pair.second());
    }


    @Test
    public void testEqualsWithNullValues() {
        Pair<Integer, String> pair1 = new Pair<>(null, null);
        Pair<Integer, String> pair2 = new Pair<>(null, null);

        assertEquals(pair1, pair2);
    }


    @Test
    public void testToString() {
        Pair<Integer, String> pair = new Pair<>(1, "One");

        String expected = "{1, One}";
        assertEquals(expected, pair.toString());
    }
}

