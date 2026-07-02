package edu.upc.prop.cluster.domain.data;

import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.common.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Product class with mocked dependencies.
 *
 * @Author Alex Meca Moñino
 */
public class ProductTest {

    private Product product;

    @Mock
    private Pair<String, Double> mockTag1;
    @Mock
    private Pair<String, Double> mockTag2;
    @Mock
    private Pair<String, Double> mockTag3;
    @Mock
    private Pair<String, Double> mockTag4;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
        List<Pair<String, Double>> tagList = new ArrayList<>();

        // Se configuran los mocks
        when(mockTag1.first()).thenReturn("Tag1");
        when(mockTag1.second()).thenReturn(2.5);

        when(mockTag2.first()).thenReturn("Tag2");
        when(mockTag2.second()).thenReturn(3.0);

        when(mockTag3.first()).thenReturn("Tag3");
        when(mockTag3.second()).thenReturn(2.5);

        when(mockTag4.first()).thenReturn("Tag4");
        when(mockTag4.second()).thenReturn(3.0);

        tagList.add(mockTag1);
        tagList.add(mockTag2);

        product = new Product("Test Product", tagList, 1);
        Product.setTagLimit(10);
    }

    @Test
    public void testProductConstructorWithTags() {
        assertEquals("Test Product", product.getName());
        assertEquals(1, product.getId());
        assertEquals(2, product.getTagCount());
        assertEquals(3.0, product.getMaxWeight());
    }

    @Test
    public void testAddTag() {
        product.addTag(mockTag3.first(), mockTag3.second());
        assertEquals(3, product.getTagCount());
        assertTrue(product.hasTag("Tag3"));
        product.addTag(mockTag3.first(), mockTag3.second());
        assertEquals(3, product.getTagCount());
    }

    @Test
    public void testRemoveTag() {
        assertTrue(product.getTags().containsKey("Tag1"));
        product.removeTag("Tag1");
        assertFalse(product.getTags().containsKey("Tag1"));
        assertEquals(1, product.getTagCount());
    }

    @Test
    public void testClearTags() {
        product.clearTags();
        assertEquals(0, product.getTagCount());
        assertEquals(0.0, product.getMaxWeight());
    }

    @Test
    public void testTagListIsFull() {
        Product.setTagLimit(2);
        assertTrue(product.taglistIsFull());

        product.addTag("Tag3", 1.0);
        assertTrue(product.taglistIsFull());
        assertEquals(2, product.getTagCount());
    }

    @Test
    public void testHasTag() {
        assertTrue(product.hasTag("Tag1"));
        assertFalse(product.hasTag("Tag3"));
    }

    @Test
    public void testSetTagsList() {
        List<Pair<String, Double>> newTags = new ArrayList<>();
        newTags.add(mockTag3);
        newTags.add(mockTag4);
        product.setTagsList(newTags);
        assertEquals(2, product.getTagCount());
        assertTrue(product.hasTag("Tag3"));
        assertTrue(product.hasTag("Tag4"));
        assertFalse(product.hasTag("Tag1"));
        assertFalse(product.hasTag("Tag2"));
    }

    @Test
    public void testMaxWeight() {
        assertEquals(3.0, product.getMaxWeight());
        product.addTag("Tag3", 4.5);
        assertEquals(4.5, product.getMaxWeight());
    }

    @Test
    public void testProductToString() {
        String expected = "Product [id=1, name=Test Product, tags={Tag1=2.5, Tag2=3.0}]";
        assertEquals(expected, product.toString());
    }

    @Test
    public void testSetAndGetTagLimit() {
        assertEquals(10, product.MaxTagNumber());
        Product.setTagLimit(2);
        assertEquals(2, product.MaxTagNumber());
        Product.setTagLimit(3);
        assertEquals(3, product.MaxTagNumber());
    }

    @Test
    public void testSetNameAndId() {
        product.setName("Test Product");
        assertEquals("Test Product", product.getName());
        product.setName("Test Product2");
        assertEquals("Test Product2", product.getName());

        product.setId(1);
        assertEquals(1, product.getId());
        product.setId(2);
        assertEquals(2, product.getId());
    }

    @Test
    public void testSetTags() {
        Map<String, Double> tags = new HashMap<>();
        tags.put("Tag1", 2.5);
        tags.put("Tag2", 3.0);
        product.setTags(tags);

        assertEquals(2, product.getTagCount());
        assertTrue(product.hasTag("Tag1"));
        assertTrue(product.hasTag("Tag2"));
        assertEquals(3.0, product.getMaxWeight());
    }

    @Test
    public void testGetTagWeight() {
        product.addTag("Tag1", 2.5);
        product.addTag("Tag2", 3.0);

        assertEquals(2.5, product.getTagWeight("Tag1"));
        assertEquals(3.0, product.getTagWeight("Tag2"));
    }

    @Test
    public void testGetTagList() {
        product.addTag("Tag1", 2.5);
        product.addTag("Tag2", 3.0);

        List<Pair<String,Double>> tags = product.getTagList();
        assertEquals(2, tags.size());
        assertTrue(tags.contains(new Pair<>("Tag1", 2.5)));
        assertTrue(tags.contains(new Pair<>("Tag2", 3.0)));
    }

    @Test
    void testEquals() {
        List<Pair<String, Double>> tags1 = new ArrayList<>();
        List<Pair<String, Double>> tags2 = new ArrayList<>();
        tags1.add(mockTag1);
        tags1.add(mockTag2);
        tags2.add(mockTag2);
        tags2.add(mockTag3);

        Product p1 = new Product("p1", tags1, 1);
        Product p2 = new Product("p2", tags2, 2);

        assertEquals(p1, p1);
        assertNotEquals(p1, p2);
    }
}
