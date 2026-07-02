package edu.upc.prop.cluster.persistence.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alejandro Ruiz Patón
 */
public class TreeTest {

    private Tree tree;

    @BeforeEach
    public void setUp() {
        tree = new Tree();
    }


    @Test
    public void testAddTag() {
        tree.addTag("apple");
        tree.addTag("orange");

        List<String> allTags = tree.getAllTags();

        assertTrue(allTags.contains("apple"));
        assertTrue(allTags.contains("orange"));
        assertEquals(2, allTags.size());
    }


    @Test
    public void testSearchTagWithPrefix() {
        tree.addTag("apple");
        tree.addTag("orange");
        tree.addTag("apricot");

        List<String> allTags = tree.searchTags("ap");
        assertTrue(allTags.contains("apple"));
        assertFalse(allTags.contains("orange"));
        assertTrue(allTags.contains("apricot"));
    }


    @Test
    public void testSearchTagNoResult() {
        tree.addTag("apple");
        tree.addTag("orange");

        List<String> allTags = tree.searchTags("apr");

        assertEquals(0, allTags.size());
    }


    @Test
    public void testDeleteTag() {
        tree.addTag("apple");
        tree.addTag("orange");

        List<String> allTags = tree.getAllTags();

        assertTrue(allTags.contains("apple"));
        assertTrue(allTags.contains("orange"));

        tree.deleteTag("apple");
        allTags.clear();
        allTags = tree.getAllTags();

        assertFalse(allTags.contains("apple"));
        assertTrue(allTags.contains("orange"));
    }


    @Test
    public void testAddProduct() {
        tree.addProduct("apple", 1);
        tree.addProduct("orange", 2);

        assertEquals(1, tree.getProductId("apple"));
        assertEquals(2, tree.getProductId("orange"));
    }


    @Test
    public void testAddProductRepetido() {
        tree.addProduct("apple", 1);
        assertEquals(1, tree.getProductId("apple"));

        tree.addProduct("apple", 2);
        assertEquals(2, tree.getProductId("apple"));
    }


    @Test
    public void testSearchProduct() {
        tree.addProduct("apple", 1);
        tree.addProduct("orange", 2);
        tree.addProduct("apricot", 3);

        List<Integer> ids = tree.searchProducts("ap");
        assertEquals(2, ids.size());
        assertTrue(ids.contains(1));
        assertTrue(ids.contains(3));
    }


    @Test
    public void testGetProductId() {
        tree.addProduct("apple", 1);
        tree.addProduct("orange", 2);
        tree.addProduct("apricot", 3);

        assertEquals(1, tree.getProductId("apple"));
        assertEquals(2, tree.getProductId("orange"));
        assertEquals(3, tree.getProductId("apricot"));
        assertNull(tree.getProductId("banana"));
    }


    @Test
    public void testRemoveProduct() {
        tree.addProduct("apple", 1);
        tree.addProduct("orange", 2);

        tree.removeProduct("apple");

        assertNull(tree.getProductId("apple"));
        assertEquals(2, tree.getProductId("orange"));
    }


    @Test
    public void testRemoveProductPrefix() {
        // Probamos a eliminar un prefijo de una palabra ya existente para observar si no elimina la palabra prefijo
        tree.addProduct("orang", 1);
        tree.addProduct("orange", 2);
        tree.addProduct("oranges", 3);

        tree.removeProduct("orang");

        assertEquals(2, tree.getProductId("orange"));
        assertEquals(3, tree.getProductId("oranges"));
    }

}