package edu.upc.prop.cluster.persistence;


import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.persistence.data.Tree;
import edu.upc.prop.cluster.persistence.ProductRepository.IProductRepository;
import edu.upc.prop.cluster.persistence.ProductRepository.InMemoryProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Jorge Vico Lora
 */
public class InMemoryProductRepositoryTest {
    IProductRepository repository;

    @Mock
    private Tree mockTree;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        File file = new File("main.shelfgenerator");
        if (file.exists()) { file.delete(); }
        repository = new InMemoryProductRepository(mockTree);
    }

    // Dado un repositorio vacío puedo añadir un Producto nuevo sin tags
    @Test
    void shouldAddProductToRepository() {
        Product expectedProduct = new Product("Ejemplo", Collections.emptyList(),1);

        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct = repository.addProduct("Ejemplo", Collections.emptyList());

        assertEquals(expectedProduct, addedProduct);
    }

    // Dado un repositorio vacío puedo añadir un Producto con tags nuevo
    @Test
    void shouldAddProductWithTagsToRepository() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        Product expectedProduct = new Product("Ejemplo", tags,1);

        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct = repository.addProduct("Ejemplo", tags);

        assertEquals(expectedProduct, addedProduct);
    }

    // Dado un repositorio vacío añadir dos veces un producto crea dos diferentes
    @Test
    void shouldNotAddTwiceProductWithTagsToRepository() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        Product expectedProduct1 = new Product("Ejemplo", tags,1);
        Product expectedProduct2 = new Product("Ejemplo", tags,2);

        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct1 = repository.addProduct("Ejemplo", tags);

        when(mockTree.getProductId("Ejemplo")).thenReturn(1);
        Product addedProduct2 = repository.addProduct("Ejemplo", tags);

        assertEquals(expectedProduct1, addedProduct1);
        assertNull(addedProduct2);
        assertNotNull(repository.getProduct(1));
        assertNull(repository.getProduct(2));
        assertEquals(List.of(expectedProduct1), repository.getAllProducts());
        assertEquals(List.of(addedProduct1), repository.getAllProducts());
    }

    // Dado un repositorio vacío puedo añadir un Producto y encontrarlo
    @Test
    void shouldFindProductInRepository() {
        Product expectedProduct = new Product("Ejemplo", Collections.emptyList(),1);

        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct = repository.addProduct("Ejemplo", Collections.emptyList());

        assertEquals(expectedProduct, repository.getProduct(1));
        assertEquals(addedProduct, repository.getProduct(1));
    }

    // Dado un repositorio vacío puedo añadir un Producto, eliminarlo y que no exista
    @Test
    void shouldDeleteProductInRepository() {
        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct = repository.addProduct("Ejemplo", Collections.emptyList());

        Product deletedProduct = repository.removeProductById(1);

        assertEquals(addedProduct, deletedProduct);
        assertNull(repository.getProduct(1));
    }

    // Dado un repositorio vacío, eliminar un Producto que no existe no elimina nada
    @Test
    void shouldNotDeleteProductInRepository() {
        Product deletedProduct = repository.removeProductById(1);
        assertNull(deletedProduct);
        assertNull(repository.getProduct(1));
    }

    // Dado un repositorio vacío pedir una lista con todos los productos retorna una lista vacía
    @Test
    void shouldNotFindAnyProductInRepository() {
        assertNull(repository.getProduct(1));
        assertEquals(0, repository.getAllProducts().size());
        assertEquals(Collections.emptyList(), repository.getAllProducts());
    }

    // Dado un repositorio vacío puedo añadir multiples productos y obtenerlos todos en una lista
    @Test
    void shouldAddProductsWithTagsToRepository() {
        List<Pair<String, Double>> tags1 = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        List<Pair<String, Double>> tags2 = List.of(new Pair<>("tag1", 1.1), new Pair<>("tag2", 2.4), new Pair<>("tag3", 3.6));
        List<Pair<String, Double>> tags3 = List.of(new Pair<>("tag1", 1.2), new Pair<>("tag2", 2.3), new Pair<>("tag3", 3.5));
        Product expectedProduct1 = new Product("Ejemplo1", tags1,1);
        Product expectedProduct2 = new Product("Ejemplo2", tags2,2);
        Product expectedProduct3 = new Product("Ejemplo3", tags3,3);

        when(mockTree.getProductId(any())).thenReturn(null);
        Product addedProduct1 = repository.addProduct("Ejemplo1", tags1);
        Product addedProduct2 = repository.addProduct("Ejemplo2", tags2);
        Product addedProduct3 = repository.addProduct("Ejemplo3", tags3);

        assertEquals(expectedProduct1, addedProduct1);
        assertEquals(expectedProduct2, addedProduct2);
        assertEquals(expectedProduct3, addedProduct3);
        assertEquals(3, repository.getAllProducts().size());
        assertEquals(List.of(expectedProduct1, expectedProduct2, expectedProduct3), repository.getAllProducts());
        assertEquals(List.of(addedProduct1, addedProduct2, addedProduct3), repository.getAllProducts());
    }

    // Dado un repositorio con productos, puedo eliminarlos y obtener una lista vacía
    @Test
    void shouldDeleteProductsWithTagsToRepository() {
        List<Pair<String, Double>> tags1 = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        List<Pair<String, Double>> tags2 = List.of(new Pair<>("tag1", 1.1), new Pair<>("tag2", 2.4), new Pair<>("tag3", 3.6));
        List<Pair<String, Double>> tags3 = List.of(new Pair<>("tag1", 1.2), new Pair<>("tag2", 2.3), new Pair<>("tag3", 3.5));

        when(mockTree.getProductId(any())).thenReturn(null);
        repository.addProduct("Ejemplo1", tags1);
        repository.addProduct("Ejemplo2", tags2);
        repository.addProduct("Ejemplo3", tags3);


        repository.removeProductById(1);
        repository.removeProductById(2);
        repository.removeProductById(3);

        assertNull(repository.getProduct(1));
        assertNull(repository.getProduct(2));
        assertNull(repository.getProduct(3));
        assertEquals(0, repository.getAllProducts().size());
        assertEquals(Collections.emptyList(), repository.getAllProducts());
    }

    // Dado un repositorio con multiples productos, puedo eliminar uno, y al volver a añadirlo, el id corresponde
    @Test
    void shouldAddProductWithTagsInPlaceToRepository() {
        List<Pair<String, Double>> tags1 = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        List<Pair<String, Double>> tags2 = List.of(new Pair<>("tag1", 1.1), new Pair<>("tag2", 2.4), new Pair<>("tag3", 3.6));
        List<Pair<String, Double>> tags3 = List.of(new Pair<>("tag1", 1.2), new Pair<>("tag2", 2.3), new Pair<>("tag3", 3.5));
        Product expectedProduct1 = new Product("Ejemplo1", tags1,1);
        Product expectedProduct2 = new Product("Ejemplo2", tags2,2);
        Product expectedProduct3 = new Product("Ejemplo3", tags3,3);

        when(mockTree.getProductId(any())).thenReturn(null);
        repository.addProduct("Ejemplo1", tags1);
        repository.addProduct("Ejemplo2", tags2);
        repository.addProduct("Ejemplo3", tags3);
        repository.removeProductById(2);

        assertEquals(expectedProduct1, repository.getProduct(1));
        assertNull(repository.getProduct(2));
        assertEquals(expectedProduct3, repository.getProduct(3));
        assertEquals(2, repository.getAllProducts().size());
        assertEquals(List.of(expectedProduct1, expectedProduct3), repository.getAllProducts());

        repository.addProduct("Ejemplo2", tags2);

        assertEquals(expectedProduct1, repository.getProduct(1));
        assertEquals(expectedProduct2, repository.getProduct(2));
        assertEquals(expectedProduct3, repository.getProduct(3));
        assertEquals(3, repository.getAllProducts().size());
        assertEquals(List.of(expectedProduct1, expectedProduct2, expectedProduct3), repository.getAllProducts());
    }

    // Dado un repositorio con un producto, puedo obtenerlo dado su nombre
    @Test
    void shouldGetProductByName() {
        List<Pair<String, Double>> tags1 = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5), new Pair<>("tag3", 3.7));
        Product expectedProduct1 = new Product("Ejemplo1", tags1,1);

        when(mockTree.getProductId(any())).thenReturn(null);
        repository.addProduct("Ejemplo1", tags1);

        when(mockTree.getProductId("Ejemplo1")).thenReturn(1);
        assertEquals(expectedProduct1, repository.getProductByName("Ejemplo1"));
    }

    // Dado un repositorio vacío, no puedo ningun producto por su nombre
    @Test
    void shouldNotGetProductByName() {
        when(mockTree.getProductId("Ejemplo1")).thenReturn(null);

        assertNull(repository.getProductByName("Ejemplo1"));
    }


    @Test
    void shouldGetProductsByPrefix() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5));
        when(mockTree.getProductId(any())).thenReturn(null);
        repository.addProduct("Apple", tags);
        repository.addProduct("Banana", tags);
        repository.addProduct("Orange", tags);
        repository.addProduct("Apricot", tags);
        repository.addProduct("Berry", tags);

        when(mockTree.searchProducts("App")).thenReturn(List.of(1));
        when(mockTree.searchProducts("Ap")).thenReturn(List.of(1, 4));
        when(mockTree.searchProducts("B")).thenReturn(List.of(2, 5));
        when(mockTree.searchProducts("X")).thenReturn(List.of());
        List<Product> results1 = repository.getProductsByPrefix("App"); // "Apple"
        List<Product> results2 = repository.getProductsByPrefix("Ap");  // "Apple", "Apricot"
        List<Product> results3 = repository.getProductsByPrefix("B");   // "Banana", "Berry"
        List<Product> results4 = repository.getProductsByPrefix("X");   // Nothing

        assertEquals(1, results1.size());
        assertTrue(results1.stream().anyMatch(p -> p.getName().equals("Apple")));

        assertEquals(2, results2.size());
        assertTrue(results2.stream().anyMatch(p -> p.getName().equals("Apple")));
        assertTrue(results2.stream().anyMatch(p -> p.getName().equals("Apricot")));

        assertEquals(2, results3.size());
        assertTrue(results3.stream().anyMatch(p -> p.getName().equals("Banana")));
        assertTrue(results3.stream().anyMatch(p -> p.getName().equals("Berry")));

        assertTrue(results4.isEmpty());
    }


    @Test
    void shouldSaveProductsToJson() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5));
        List<Pair<String, Double>> tags2 = List.of(new Pair<>("tag2", 1.0), new Pair<>("tag3", 4.5));

        when(mockTree.getProductId(any())).thenReturn(null);
        Product p1 = repository.addProduct("Producto1", tags);
        Product p2 = repository.addProduct("Producto2", tags2);

        repository.saveToJSON("products.json");

        // Verificamos si el JSON se ha creado
        File json = new File("products.json");
        assertTrue(json.exists());
    }


    @Test
    void shouldLoadProductsFromJson() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5));
        List<Pair<String, Double>> tags2 = List.of(new Pair<>("tag2", 1.0), new Pair<>("tag3", 4.5));

        when(mockTree.getProductId(any())).thenReturn(null);
        Product p1 = repository.addProduct("Producto1", tags);
        Product p2 = repository.addProduct("Producto2", tags2);

        repository.saveToJSON("main.shelfgenerator");

        // Creamos nuevo repositorio
        repository = new InMemoryProductRepository(new Tree());
        repository.loadFromJSON();

        // Comprobamos si se han cargado los datos correctamente
        assertTrue(p1.equals(repository.getProduct(p1.getId())));
        assertTrue(p2.equals(repository.getProduct(p2.getId())));
        assertEquals(2, repository.getAllProducts().size());
    }


    @Test
    void shouldSaveAvailableIDs() {
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5));

        when(mockTree.getProductId(any())).thenReturn(null);
        Product p1 = repository.addProduct("Producto1", tags);
        Product p2 = repository.addProduct("Producto2", tags);
        Product p3 = repository.addProduct("Producto3", tags);
        Product p4 = repository.addProduct("Producto4", tags);
        repository.removeProductById(1);
        repository.removeProductById(3);

        repository.saveToJSON("main.shelfgenerator");

        repository = new InMemoryProductRepository(new Tree());
        repository.loadFromJSON();

        Product p5 = repository.addProduct("Producto5", tags);
        Product p6 = repository.addProduct("Producto6", tags);
        Product p7 = repository.addProduct("Producto7", tags);

        assertEquals(1, p5.getId());
        assertEquals(3, p6.getId());
        assertEquals(5, p7.getId());
    }

    @Test
    public void testEditProductName() {
        InMemoryProductRepository repo = new InMemoryProductRepository();
        List<Pair<String, Double>> tags = List.of(new Pair<>("tag1", 1.0), new Pair<>("tag2", 2.5));

        Product p1 = repo.addProduct("p1", tags);
        assertNotNull(p1);
        assertEquals("p1", p1.getName());

        repo.editProductName("p1", "p2");

        assertFalse(repo.getAllProducts().stream().anyMatch(p -> p.getName().equals("p1")));
        assertTrue(repo.getAllProducts().stream().anyMatch(p -> p.getName().equals("p2")));
    }


    @AfterEach
    void cleanUp() {
        File jsonFile = new File("products.json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }
}