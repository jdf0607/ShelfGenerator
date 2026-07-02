package edu.upc.prop.cluster.persistence;


import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.persistence.AlgorithmRepository.InMemoryAlgorithmRepository;
import edu.upc.prop.cluster.persistence.SimilarityRepository.InMemorySimilarityRepository;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.persistence.ProductRepository.InMemoryProductRepository;
import edu.upc.prop.cluster.persistence.TagRepository.InMemoryTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


/**
 * @author Jorge Vico Lora
 */
public class PersistenceControllerTest {

    @Mock
    InMemoryTagRepository tagRepository;  // Mock para TagRepository

    @Mock
    InMemoryProductRepository productRepository;  // Mock para ProductRepository

    @Mock
    InMemorySimilarityRepository similarityRepository;  // Mock para ProductRepository

    @Mock
    InMemoryAlgorithmRepository algorithmRepository;    // Mock para AlgorithmRepository


    @InjectMocks
    PersistenceController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // Dado un controlador nuevo, pedir todas las Tags retorna una lista vacía
    @Test
    void shouldReturnEmptyList1() {
        when(tagRepository.getAllTags()).thenReturn(Collections.emptyList());

        List<Tag> tags = controller.getAllTags();
        assertNotNull(tags);
        assertEquals(Collections.emptyList(), tags);
    }

    // Dado un controlador con varias Tags, pedir todas las Tags las devuelve
    @Test
    void shouldReturnList1() {
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        Tag tag3 = new Tag("tag3");
        when(tagRepository.getAllTags()).thenReturn(List.of(tag1, tag2, tag3));

        List<Tag> tags = controller.getAllTags();
        assertNotNull(tags);
        assertEquals(List.of(tag1, tag2, tag3), tags);
    }

    // Dado un controlador nuevo, pedir todas las Tags y aplicar clear de Tags no hace nada
    @Test
    void shouldClearNothing1() {
        when(tagRepository.getAllTags()).thenReturn(Collections.emptyList());

        List<Tag> tags = controller.clearTags();
        assertNotNull(tags);
        assertEquals(Collections.emptyList(), tags);
    }

    // Dado un controlador con Tags, aplicar clear de Tags limpias las tags
    @Test
    void shouldClearAll1() {
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        Tag tag3 = new Tag("tag3");
        when(tagRepository.getAllTags()).thenReturn(List.of(tag1, tag2, tag3));

        List<Tag> tags = controller.clearTags();
        assertNotNull(tags);
        assertEquals(List.of(tag1, tag2, tag3), tags);
    }

    // Dado un controlador nuevo, intentar obtener un Tag, devuelve null
    @Test
    void shouldNotGetTag() {
        when(tagRepository.getTag("tag1")).thenReturn(null);

        Tag foundTag = controller.getTag("tag1");
        assertNull(foundTag);
    }

    // Dado un controller con un Tag, intentar obtenerlo lo devuelve
    @Test
    void shouldGetTag() {
        Tag tag1 = new Tag("tag1");
        when(tagRepository.getTag("tag1")).thenReturn(tag1);

        Tag foundTag = controller.getTag("tag1");
        assertNotNull(foundTag);
        assertEquals(tag1, foundTag);
    }

    // Dado un controller nuevo, crear un tag lo crea
    @Test
    void shouldAddTag() {
        Tag tag1 = new Tag("tag1");
        when(tagRepository.getTag("tag1")).thenReturn(null);
        when(tagRepository.addTag(tag1)).thenReturn(tag1);

        Tag addedTag = controller.addTag(tag1);
        assertNotNull(addedTag);
        assertEquals(tag1, addedTag);
    }

    // Dado un controller con una tag, crearla de nuevo retorna null
    @Test
    void shouldNotAddTag() {
        Tag tag1 = new Tag("tag1");

        when(tagRepository.getTag("tag1")).thenReturn(tag1);
        when(tagRepository.addTag(tag1)).thenReturn(null);

        Tag addedTag = controller.addTag(tag1);
        assertNull(addedTag);
    }

    // Dado un controlador con una tag, eliminar una tag, retorna la tag
    @Test
    void shouldRemoveTag() {
        Tag tag1 = new Tag("tag1");
        tag1.addProduct(0.2, 2);
        tag1.addProduct(0.3, 3);
        when(tagRepository.removeTag("tag1")).thenReturn(tag1);

        Tag removedTag = controller.removeTag("tag1");

        assertNotNull(removedTag);
        assertEquals(tag1, removedTag);

        when(tagRepository.getTag("tag1")).thenReturn(null);
        Tag exists = controller.getTag("tag1");
        assertNull(exists);
    }

    // Dado un controlador nuevo, pedir todas los Products retorna una lista vacía
    @Test
    void shouldReturnEmptyList2() {
        when(productRepository.getAllProducts()).thenReturn(Collections.emptyList());

        List<Product> products = controller.getAllProducts();
        assertNotNull(products);
        assertEquals(Collections.emptyList(), products);
    }

    // Dado un controlador con varias products, pedir todos los products los devuelve
    @Test
    void shouldReturnList2() {
        Product product1 = new Product("product1");
        Product product2 = new Product("product2");
        Product product3 = new Product("product3");
        when(productRepository.getAllProducts()).thenReturn(List.of(product1, product2, product3));

        List<Product> products = controller.getAllProducts();
        assertNotNull(products);
        assertEquals(List.of(product1, product2, product3), products);
    }

    // Dado un controlador nuevo, pedir todas las products y aplicar clear de products no hace nada
    @Test
    void shouldClearNothing2() {
        when(productRepository.getAllProducts()).thenReturn(Collections.emptyList());

        List<Product> products = controller.clearProducts();
        assertNotNull(products);
        assertEquals(Collections.emptyList(), products);
    }

    // Dado un controlador con product, aplicar clear de products limpias los products
    @Test
    void shouldClearAll2() {
        Product product1 = new Product("product1");
        Product product2 = new Product("product2");
        Product product3 = new Product("product3");
        when(productRepository.getAllProducts()).thenReturn(List.of(product1, product2, product3));

        List<Product> products = controller.clearProducts();
        assertNotNull(products);
        assertEquals(List.of(product1, product2, product3), products);
    }

    // Dado un controlador nuevo, intentar obtener un product, devuelve null
    @Test
    void shouldNotGetProduct() {
        when(productRepository.getProductByName("product1")).thenReturn(null);

        Product foundProduct = controller.getProduct("product1");
        assertNull(foundProduct);
    }

    // Dado un controller con un product, intentar obtenerlo lo devuelve
    @Test
    void shouldGetProduct() {
        Product product1 = new Product("product1");
        when(productRepository.getProductByName("product1")).thenReturn(product1);

        Product foundProduct = controller.getProduct("product1");
        assertNotNull(foundProduct);
        assertEquals(product1, foundProduct);
    }

    // Dado un controller nuevo, crear un product lo crea
    @Test
    void shouldAddProduct() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        Product product1 = new Product("product1", tags, 1);
        when(productRepository.getProductByName("product1")).thenReturn(null);
        when(productRepository.addProduct("product1", tags)).thenReturn(product1);

        Product addedProduct = controller.addProduct(product1);
        assertNotNull(addedProduct);
        assertEquals(product1, addedProduct);
    }

    // Dado un controller con un product, crearla de nuevo retorna null
    @Test
    void shouldNotAddProduct() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        Product product1 = new Product("product1", tags, 1);

        when(productRepository.getProductByName("product1")).thenReturn(product1);
        when(productRepository.addProduct("product1", tags)).thenReturn(null);

        Product addedProduct = controller.addProduct(product1);
        assertNull(addedProduct);
    }

    // Dado un controller con un product, eliminarlo devuelve el producto
    @Test
    void shouldRemoveProduct() {
        Product product1 = mock(Product.class);
        when(product1.getName()).thenReturn("product1");
        when(product1.getId()).thenReturn(1);
        when(product1.getTags()).thenReturn(Map.of("tag1", 0.2, "tag2", 0.3));

        when(productRepository.getProductByName("product1")).thenReturn(product1);
        when(productRepository.removeProductById(1)).thenReturn(product1);

        Product removedProduct = controller.removeProduct("product1");

        assertNotNull(removedProduct);
        assertEquals(product1, removedProduct);

        when(productRepository.getProductByName("product1")).thenReturn(null);

        Product exists = controller.getProduct("product1");
        assertNull(exists);
    }


    // Dado un controlador nuevo, pedir todas las similarities de un producto que no existe, retorna una lista vacía
    @Test
    void shouldReturnEmptyList3() {
        when(similarityRepository.getSimilarities(1)).thenReturn(Collections.emptyList());

        List<Pair<String, Double>> similarities = controller.getSavedSimilarities(1);
        assertNotNull(similarities);
        assertEquals(Collections.emptyList(), similarities);
    }

    // Dado un controlador con similarities, pedir todas las similarities de un producto que existe, retorna una lista
    @Test
    void shouldReturnList3() {
        when(similarityRepository.getSimilarities(1)).thenReturn(List.of(new Pair<>(1, 0.3), new Pair<>(2, 0.2), new Pair<>(3, 0.1)));
        when(productRepository.getProduct(1)).thenReturn(new Product("product1"));
        when(productRepository.getProduct(2)).thenReturn(new Product("product2"));
        when(productRepository.getProduct(3)).thenReturn(new Product("product3"));

        List<Pair<String, Double>> similarities = controller.getSavedSimilarities(1);
        assertNotNull(similarities);
        assertEquals(List.of(new Pair<>("product1", 0.3), new Pair<>("product2", 0.2), new Pair<>("product3", 0.1)), similarities);
    }

    // Dado un controller nuevo, pedir todas las similarities retorna un mapa vacío
    @Test
    void shouldGetAllSimilaritiesEmpty() {
        when(similarityRepository.getAll()).thenReturn(Collections.emptyMap());

        Map<Pair<Integer, Integer>, Double> similarities = controller.getAllSimilarities();

        assertNotNull(similarities);
        assertEquals(Collections.emptyMap(), similarities);
    }

    // Dado un controller con varias similarities, puedo obtenerlas todas
    @Test
    void shouldGetAllSimilarities() {
        when(similarityRepository.getAll()).thenReturn(Map.of(new Pair<>(1, 2), 0.2, new Pair<>(1, 3), 0.3));

        Map<Pair<Integer, Integer>, Double> similarities = controller.getAllSimilarities();

        assertNotNull(similarities);
        assertEquals(Map.of(new Pair<>(1, 2), 0.2, new Pair<>(1, 3), 0.3), similarities);
    }

    // Dado un controlador nuevo, pedir todas las Tags y aplicar clear de Tags no hace nada
    @Test
    void shouldClearNothing3() {
        when(similarityRepository.getAll()).thenReturn(Collections.emptyMap());

        Map<Pair<Integer, Integer>, Double> similarities = controller.clearSimilarities();
        assertNotNull(similarities);
        assertEquals(Collections.emptyMap(), similarities);
    }

    // Dado un controlador con similarities, aplicar clear de similarities limpias las similarities
    @Test
    void shouldClearAll3() {
        when(similarityRepository.getAll()).thenReturn(Map.of(new Pair<>(1, 2), 0.2));

        Map<Pair<Integer, Integer>, Double> similarities = controller.clearSimilarities();
        assertNotNull(similarities);
        assertEquals(Map.of(new Pair<>(1, 2), 0.2), similarities);
    }

    // Dado un controlador nuevo, intentar obtener una similarity, devuelve null
    @Test
    void shouldNotGetSimilarity() {
        when(similarityRepository.getSimilarity(1, 2)).thenReturn(null);

        Double foundSimilarity = controller.getSavedSimilarity(1, 2);
        assertNull(foundSimilarity);
    }

    // Dado un controller con una similarity, intentar obtenerla la devuelve
    @Test
    void shouldGetSimilarity() {
        when(similarityRepository.getSimilarity(1, 2)).thenReturn(0.2);

        Double foundSimilarity = controller.getSavedSimilarity(1, 2);
        assertNotNull(foundSimilarity);
        assertEquals(0.2, foundSimilarity);
    }

    // Dado un controller nuevo, crear una similarity la crea
    @Test
    void shouldAddSimilarity() {
        when(similarityRepository.addSimilarity(1, 2, 0.3)).thenReturn(0.3);

        Double addedSimilarity = controller.addSimilarity(1, 2, 0.3);
        assertNotNull(addedSimilarity);
        assertEquals(0.3, addedSimilarity);
    }

    // Dado un controller con una similarity, crearla de nuevo retorna null
    @Test
    void shouldNotAddSimilarity() {
        when(similarityRepository.addSimilarity(1, 2, 0.3)).thenReturn(null);

        Double addedSimilarity = controller.addSimilarity(1, 2, 0.3);
        assertNull(addedSimilarity);
    }

    // Dado un controlador con una similarity, eliminar una similarity, retorna la tag y la elimina
    @Test
    void shouldRemoveSimilarity() {
        when(similarityRepository.removeSimilarity(1, 2)).thenReturn(0.3);

        Double removedSimilarity = controller.removeSimilarity(1, 2);

        assertNotNull(removedSimilarity);
        assertEquals(0.3, removedSimilarity);

        when(similarityRepository.removeSimilarity(1, 2)).thenReturn(null);

        removedSimilarity = controller.removeSimilarity(1, 2);
        assertNull(removedSimilarity);
    }

    // Dado un controlador nuevo, tratar de editar una similarity que no existe, retorna null
    @Test
    void shouldNotEditSimilarity() {
        when(similarityRepository.editSimilarity(1, 2, 0.3)).thenReturn(null);

        Double editedSimilarity = controller.editSimilarity(1, 2, 0.3);

        assertNull(editedSimilarity);
    }

    // Dado un controlador con una similraity, tratar de editarla retorna el nuevo valor
    @Test
    void shouldEditSimilarity() {
        when(similarityRepository.editSimilarity(1, 2, 0.3)).thenReturn(0.3);

        Double editedSimilarity = controller.editSimilarity(1, 2, 0.3);

        assertEquals(0.3, editedSimilarity);
    }

    @Test
    void testGetTagsWithProductCount() {
        Tag t1 = new Tag("tag1");
        Tag t2 = new Tag("tag2");
        Tag t3 = new Tag("tag3");

        t1.addProduct(0.5, 1);
        t1.addProduct(0.5, 2);
        t2.addProduct(0.5, 3);

        List<Tag> mockTags = Arrays.asList(t1, t2, t3);

        when(tagRepository.getAllTags()).thenReturn(mockTags);

        Map<String, Double> result = controller.getTagsWithProductCount();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2.0, result.get("tag1"));
        assertEquals(1.0, result.get("tag2"));
        assertEquals(0.0, result.get("tag3"));

        verify(tagRepository).getAllTags();
    }

    @Test
    void testGetPrefixByPrefix() {
        controller.getTagsByPrefix("prefix");
        verify(tagRepository).getPrefix("prefix");
    }

    @Test
    void testEditProductName() {
        controller.editProductName("productName", "newName");
        verify(productRepository).editProductName("productName", "newName");
    }

    @Test
    void testShelfToFile() {
        controller.saveShelfToFile("shelfToFile.json");
        verify(algorithmRepository).saveToJSON("shelfToFile.json");
    }

    @Test
    void testSaveShelf() {
        controller.saveShelf(List.of("p1", "p2", "p3"));
        verify(algorithmRepository).saveShelf(List.of("p1", "p2", "p3"));
        verify(algorithmRepository).saveToJSON("main.shelfgenerator");
    }

    @Test
    void testGetShelf() {
        controller.getShelf();
        verify(algorithmRepository).getShelf();
    }


    @Test
    void loadTags() {
        controller.loadTags();
        verify(tagRepository, times(1)).loadFromJSON();
    }

    @Test
    void saveTags() {
        String fileName = "file";
        controller.saveTags(fileName);
        verify(tagRepository, times(1)).saveToJSON(fileName);
    }

    @Test
    void getProductId() {
        int id = 1;
        controller.getProduct(id);
        verify(productRepository, times(1)).getProduct(id);
    }

    @Test
    void loadProducts() {
        controller.loadProducts();
        verify(productRepository, times(1)).loadFromJSON();
    }

    @Test
    void saveProducts() {
        String fileName = "file";
        controller.saveProducts(fileName);
        verify(productRepository, times(1)).saveToJSON(fileName);
    }

    @Test
    void loadSimilarities() {
        controller.loadSimilarities();
        verify(similarityRepository, times(1)).loadFromJSON();
    }

    @Test
    void saveSimilarities() {
        String fileName = "file";
        controller.saveSimilarities(fileName);
        verify(similarityRepository, times(1)).saveToJSON(fileName);
    }

    @Test
    void loadShelf() {
        controller.loadShelf();
        verify(algorithmRepository, times(1)).loadFromJSON();
    }


}
