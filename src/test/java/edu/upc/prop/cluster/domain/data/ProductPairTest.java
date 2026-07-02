package edu.upc.prop.cluster.domain.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alejandro Ruiz Patón
 */
public class ProductPairTest {

    @Test
    public void testConstructorAndGetters() {
        ProductPair pp = new ProductPair(5.5, 1);

        assertEquals(5.5, pp.first());
        assertEquals(1, pp.second());
    }


    @Test
    public void testEquals() {
        ProductPair pp = new ProductPair(5.5, 1);
        ProductPair pp2 = new ProductPair(5.5, 1);

        assertEquals(pp, pp);
        assertEquals(pp, pp2);
    }


    @Test
    public void testDifferents() {
        ProductPair pp = new ProductPair(5.5, 1);
        ProductPair pp2 = new ProductPair(5.5, 2);

        assertNotEquals(pp, pp2);
    }


    @Test
    public void testNull() {
        ProductPair pp = new ProductPair(null, null);

        assertNull(pp.first());
        assertNull(pp.second());
    }


    @Test
    public void testHashCodeSameProductID() {
        ProductPair pp = new ProductPair(5.5, 1);
        ProductPair pp2 = new ProductPair(6.5, 1);

        assertEquals(pp.hashCode(), pp2.hashCode());
    }


    @Test
    public void testHashCodeDifferentProductID() {
        ProductPair pp = new ProductPair(5.5, 1);
        ProductPair pp2 = new ProductPair(5.5, 2);

        assertNotEquals(pp.hashCode(), pp2.hashCode());
    }
}