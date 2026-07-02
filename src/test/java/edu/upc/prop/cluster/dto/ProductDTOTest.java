package edu.upc.prop.cluster.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alejandro Ruiz Patón
 */
public class ProductDTOTest {

    @Test
    public void testConstructorAndGetters() {
        String productName = "Product1";
        Map<String, Double> productTags = new TreeMap<>();
        productTags.put("Tag1", 0.8);
        productTags.put("Tag2", 0.5);

        ProductDTO product = new ProductDTO(productName, productTags);

        assertEquals(productName, product.getName(), "The product name should match the input value.");
        assertEquals(productTags, product.getTags(), "The tags map should match the input map.");
        assertTrue(product.getTags().containsKey("Tag1"), "The tags map should contain 'Tag1'.");
        assertEquals(0.8, product.getTags().get("Tag1"), 0.001, "The value of 'Tag1' should match the input value.");
    }


    @Test
    public void testEmptyTags() {
        String productName = "Product1";
        Map<String, Double> emptyTags = new TreeMap<>();

        ProductDTO product = new ProductDTO(productName, emptyTags);

        assertEquals(productName, product.getName(), "The product name should match the input value.");
        assertTrue(product.getTags().isEmpty(), "The tags map should be empty.");
    }


    @Test
    public void testNullTags() {
        String productName = "Product1";

        ProductDTO product = new ProductDTO(productName, null);

        assertEquals(productName, product.getName(), "The product name should match the input value.");
        assertNull(product.getTags(), "The tags map should be null if null was provided.");
    }


    @Test
    public void testImmutableTagsMap() {
        String productName = "Product1";
        Map<String, Double> tags = new TreeMap<>();
        tags.put("Tag1", 1.0);

        ProductDTO product = new ProductDTO(productName, tags);
        tags.put("Tag2", 0.5);

        assertFalse(product.getTags().containsKey("Tag2"), "The tags map in ProductDTO should not reflect changes to the original map.");
    }
}