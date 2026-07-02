package edu.upc.prop.cluster.domain.data;

import edu.upc.prop.cluster.domain.data.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for the Tag class with mocked dependencies.
 *
 * @author Alejandro Ruiz Patón
 */
public class TagTest {
    private Tag tag;

    @BeforeEach
    public void setUp() {
        tag = new Tag("test");
    }

    @Test
    public void testConstructorAndName() {
        assertEquals("test", tag.getName(), "Tag name debería ser 'test'");
    }

    @Test
    public void testAddProductAndProductCounter() {
        boolean added = tag.addProduct(3.5,1);
        assertTrue(added, "Producto no añadido");
        assertEquals(1, tag.getProductCount());

        // Intento añadir el mismo producto
        added = tag.addProduct(3.5,1);
        assertFalse(added, "Producto ya existe");
        assertEquals(1, tag.getProductCount());
    }

    @Test
    public void testDeleteProduct() {
        tag.addProduct(3.5,1);

        // Devuelve falso si no existe el producto
        assertFalse(tag.deleteProduct(2));
        assertEquals(1, tag.getProductCount());

        // Cierto si lo elimina
        assertTrue(tag.deleteProduct(1));
        assertEquals(0, tag.getProductCount());
    }

    @Test
    public void testChangeWeight() {
        // Si el producto no existe devuelve falso
        assertFalse(tag.changeWeight(3.5,1));

        // Si existe lo modifica
        tag.addProduct(3.5,1);
        assertTrue(tag.changeWeight(4.5,1));
    }

    @Test
    public void testGetIds() {
        tag.addProduct(3.5,1);
        tag.addProduct(4.5,2);
        List<Integer> ids = tag.getIDs();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(1));
        assertTrue(ids.contains(2));

        // Al eliminar un producto ya no lo devuelve en la lista
        tag.deleteProduct(1);
        List<Integer> ids2 = tag.getIDs();
        assertEquals(1, ids2.size());
        assertFalse(ids2.contains(1));
    }

    @Test
    public void testEquals() {
        Tag tag2 = new Tag("test2");
        Tag tag3 = new Tag("test");

        assertFalse(tag.equals(tag2));
        assertTrue(tag.equals(tag3));
        assertFalse(tag.equals(null));
        assertFalse(tag.equals("testDiff"));
    }

    @Test
    public void testGetProductCount() {
        assertEquals(0, tag.getProductCount());

        tag.addProduct(3.5,1);
        assertEquals(1, tag.getProductCount());

        tag.addProduct(3.5,2);
        assertEquals(2, tag.getProductCount());

        tag.deleteProduct(1);
        assertEquals(1, tag.getProductCount());
    }


}
