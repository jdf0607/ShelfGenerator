package edu.upc.prop.cluster.domain;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.dto.TagDTO;
import edu.upc.prop.cluster.persistence.PersistenceController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for DomainController class.
 * Contains unit tests for every functionality of the DomainController.
 *
 * @author Alex Meca Moñino
 */

public class DomainControllerTest {

    @Test
    public void saveShelfTest() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);

        List<String> testShelf = Arrays.asList("Product1", "Product2", "Product3");
        domainController.saveShelf(testShelf);

        Mockito.verify(mockedPersistenceController, Mockito.times(1)).saveShelf(testShelf);
    }

    @Test
    public void getAllProductsTest() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);

        List<Pair<String,Double>> emptyList = new ArrayList<>();
        List<Product> products = Arrays.asList(new Product("Product1", emptyList, 0), new Product("Product2", emptyList, 1),
                new Product("Product3", emptyList, 2));
        when(mockedPersistenceController.getAllProducts()).thenReturn(products);
        DomainController domainController = new DomainController(mockedPersistenceController);
        List<ProductDTO> productDTOList = domainController.getAllProducts();

        assertEquals(products.size(), productDTOList.size(), "Size of lists does not match");

        for (int i = 0; i < products.size(); i++) {
            assertEquals(products.get(i).getName(), productDTOList.get(i).getName(), "Product names does not match");
        }
    }

    @Test
    public void getProduct_validExistingProductName_shouldReturnProductDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 1.0));
        tags.add(new Pair<>("tag2", 0.5));
        Product product = new Product(productName, tags, 0);
        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.getProduct(productName);
        assertEquals(productName, result.fold(error -> "", ProductDTO::getName), "Product names do not match");
    }

    @Test
    public void getProduct_nonExistingProductName_shouldReturnErrorMessage() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "NonExistingProduct";
        when(mockedPersistenceController.getProduct(productName)).thenReturn(null);
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.getProduct(productName);
        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(error -> error, productDTO -> ""), "Error messages do not match");
    }

    @Test
    public void addProduct_validInput_ShouldReturnRight() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 1.0));
        tags.add(new Pair<>("tag2", 0.5));
        String productName = "Product1";
        Product product = new Product(productName, tags, 0);
        when(mockedPersistenceController.addProduct(Mockito.any(Product.class))).thenReturn(product);
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.addProduct(productName, tags);
        assertEquals(productName, result.fold(error -> "", ProductDTO::getName), "Product names do not match");
    }

    @Test
    public void addProduct_tooManyTags_ShouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        List<Pair<String, Double>> tags = Stream.generate(() -> new Pair<>("tag", 0.5))
                .limit(Product.MaxTagNumber() + 1)
                .collect(Collectors.toList());
        String productName = "Product1";
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.addProduct(productName, tags);
        String errorMsg = "Numero máximo de tags alcanzado (" + Product.MaxTagNumber() + ").";
        assertEquals(errorMsg, result.fold(error -> error, productDTO -> ""), "Error messages do not match");
    }

    @Test
    public void removeProduct_existingProductName_productRemoved() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "ExistingProduct";
        List<Pair<String, Double>> tags = new ArrayList<>();
        Product product = new Product(productName, tags, 0);

        when(mockedPersistenceController.removeProduct(productName)).thenReturn(product);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeProduct(productName);

        assertEquals(true, result.fold(error -> false, success -> success), "Product removal failed");
    }

    @Test
    public void removeProduct_nonExistingProductName_errorMessageReturned() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "NonExistingProduct";

        when(mockedPersistenceController.removeProduct(productName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeProduct(productName);

        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(error -> error, success -> ""), "Error messages do not match");
    }

    @Test
    public void removeProduct_existingProductNameWithTags_tagsUpdated() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "ExistingProduct";
        List<Pair<String, Double>> tags = Arrays.asList(new Pair<>("tag1", 1.0));
        Product product = new Product(productName, tags, 0);
        String tagName = "tag1";
        Tag tag = new Tag(tagName);
        tag.addProduct(1.0, 0);

        when(mockedPersistenceController.removeProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeProduct(productName);

        assertFalse(tag.getIDs().contains(product.getId()), "Product not removed from tag");
    }

    @Test
    public void removeTagFromProduct_productAndTagExist_removedSuccessfully() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "ExistingProduct";
        String tagName = "ExistingTag";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("ExistingTag", 0.5));
        Product product = new Product(productName, tags, 0);
        Tag tag = new Tag(tagName);
        product.addTag(tagName, 1.0);
        tag.addProduct(1.0, 0);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeTagFromProduct(productName, tagName);

        assertFalse(product.getTags().containsKey(tagName), "Tag not removed from product");
        assertFalse(tag.getIDs().contains(product.getId()), "Product not removed from tag");
        assertEquals(true, result.fold(error -> false, success -> success), "Tag removal from product failed");
    }

    @Test
    public void removeTagFromProduct_nonExistingProduct_errorMessageReturned() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "NonExistingProduct";
        String tagName = "ExistingTag";

        when(mockedPersistenceController.getProduct(productName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeTagFromProduct(productName, tagName);

        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(error -> error, success -> ""), "Error messages do not match");
    }

    @Test
    public void removeTagFromProduct_nonExistingTag_errorMessageReturned() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "ExistingProduct";
        String tagName = "NonExistingTag";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));
        Product product = new Product(productName, tags, 0);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeTagFromProduct(productName, tagName);

        assertEquals("El tag con nombre " + tagName + " no existe.", result.fold(error -> error, success -> ""), "Error messages do not match");
    }

    @Test
    public void removeTagFromProduct_productDoesNotContainTag_errorMessageReturned() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "ExistingProduct";
        String tagName = "ExistingTag";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));
        Product product = new Product(productName, tags, 0);
        Tag tag = new Tag(tagName);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, Boolean> result = domainController.removeTagFromProduct(productName, tagName);

        assertEquals("El producto no contiene el tag " + tagName + ".", result.fold(error -> error, success -> ""), "Error messages do not match");
    }

    @Test
    public void addTagToProduct_validInput_shouldReturnProductDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        String tagName = "tag2";
        double weight = 0.8;
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));
        Product product = new Product(productName, tags, 0);
        Tag tag = new Tag(tagName);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.addTagToProduct(productName, tagName, weight);

        assertTrue(product.getTags().containsKey(tagName));
        assertEquals(weight, product.getTags().get(tagName));
        assertEquals(productName, result.fold(error -> "", ProductDTO::getName));
    }

    @Test
    public void addTagToProduct_nonExistingProduct_shouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "NonExistingProduct";
        String tagName = "tag1";
        double weight = 0.7;

        when(mockedPersistenceController.getProduct(productName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.addTagToProduct(productName, tagName, weight);

        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(error -> error, productDTO -> ""));
    }

    @Test
    public void addTagToProduct_productAlreadyHasTag_shouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        String tagName = "tag1";
        double weight = 0.6;
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>(tagName, weight));
        Product product = new Product(productName, tags, 0);
        Tag tag = new Tag(tagName);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.addTagToProduct(productName, tagName, weight);

        assertEquals("El producto ya tiene la tag " + tagName + ".", result.fold(error -> error, productDTO -> ""));
    }

    @Test
    public void editTagFromProduct_validInput_shouldReturnProductDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        String tagName = "tag1";
        double weight = 0.5;
        Product product = new Product(productName, Arrays.asList(new Pair<>(tagName, weight)), 0);
        Tag tag = new Tag(tagName);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);
        when(mockedPersistenceController.getTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editTagFromProduct(productName, tagName, weight);

        assertEquals(weight, product.getTags().get(tagName), "Tag weight not updated in product");
        assertEquals(productName, result.fold(String::valueOf, ProductDTO::getName));
    }

    @Test
    public void editTagFromProduct_nonExistingProduct_shouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "NonExistingProduct";
        String tagName = "ExistingTag";
        double weight = 0.5;

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editTagFromProduct(productName, tagName, weight);

        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(String::valueOf, ProductDTO::getName));
    }

    @Test
    public void editTagFromProduct_productDoesNotHaveTag_shouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        String tagName = "NonExistingTag";
        double weight = 0.5;
        Product product = new Product(productName, new ArrayList<>(), 0);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editTagFromProduct(productName, tagName, weight);

        assertEquals("El producto no tiene la tag " + tagName + ".", result.fold(String::valueOf, ProductDTO::getName));
    }

    @Test
    public void editTagFromProduct_nonExistingTag_shouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String productName = "Product1";
        String tagName = "NonExistingTag";
        double weight = 0.5;
        Product product = new Product(productName, Arrays.asList(new Pair<>(tagName, weight)), 0);

        when(mockedPersistenceController.getProduct(productName)).thenReturn(product);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editTagFromProduct(productName, tagName, weight);

        assertEquals("El tag con nombre " + tagName + " no existe.", result.fold(String::valueOf, ProductDTO::getName));
    }

    @Test
    public void editProductName_validInputs_ShouldReturnProductDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String originalProductName = "Product1";
        String newProductName = "Product2";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));

        Product product1 = new Product(originalProductName, tags, 0);

        when(mockedPersistenceController.getProduct(originalProductName)).thenReturn(product1);
        when(mockedPersistenceController.getProduct(newProductName)).thenReturn(null);

        product1.setName(newProductName);
        when(mockedPersistenceController.editProductName(originalProductName, newProductName)).thenReturn(product1);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editProductName(originalProductName, newProductName);

        assertEquals(newProductName, result.fold(error -> "", ProductDTO::getName), "Product names do not match");
    }

    @Test
    public void editProductName_sameName_ShouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String originalProductName = "Product1";
        String newProductName = "Product1";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));
        Product product1 = new Product(originalProductName, tags, 0);

        when(mockedPersistenceController.getProduct(originalProductName)).thenReturn(product1);
        when(mockedPersistenceController.getProduct(newProductName)).thenReturn(product1);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editProductName(originalProductName, newProductName);

        assertEquals("El nombre del producto no ha cambiado.", result.fold(error -> error, ProductDTO::getName), "Error messages do not match");
    }

    @Test
    public void editProductName_nonExistingProduct_ShouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String originalProductName = "NonExistingProduct";
        String newProductName = "Product2";

        when(mockedPersistenceController.getProduct(originalProductName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editProductName(originalProductName, newProductName);

        assertEquals("El producto con nombre " + originalProductName + " no existe.", result.fold(error -> error, ProductDTO::getName), "Error messages do not match");
    }

    @Test
    public void editProductName_newProductNameExists_ShouldReturnLeft() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String originalProductName = "Product1";
        String newProductName = "Product2";
        List<Pair<String, Double>> tags = new ArrayList<>();
        tags.add(new Pair<>("tag1", 0.5));

        Product product1 = new Product(originalProductName, tags, 0);
        Product product2 = new Product(newProductName, tags, 1);

        when(mockedPersistenceController.getProduct(originalProductName)).thenReturn(product1);
        when(mockedPersistenceController.getProduct(newProductName)).thenReturn(product2);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, ProductDTO> result = domainController.editProductName(originalProductName, newProductName);

        assertEquals("Ya existe un producto con el nombre " + newProductName + ".", result.fold(error -> error, ProductDTO::getName), "Error messages do not match");
    }

    @Test
    public void addTag_newTag_shouldReturnTagDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String tagName = "NewTag";
        when(mockedPersistenceController.addTag(new Tag(tagName))).thenReturn(new Tag(tagName));
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, TagDTO> result = domainController.addTag(tagName);
        assertEquals(tagName, result.fold(error -> "", TagDTO::getName), "Tag names do not match");
    }

    @Test
    public void addTag_existingTag_shouldReturnErrorMessage() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String tagName = "ExistingTag";
        when(mockedPersistenceController.addTag(new Tag(tagName))).thenReturn(null);
        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, TagDTO> result = domainController.addTag(tagName);
        assertEquals("Ya existe un tag con este nombre.", result.fold(error -> error, TagDTO::getName), "Error messages do not match");
    }

    @Test
    public void removeTag_tagDoesNotExist_shouldReturnErrorMessage() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String tagName = "UnexistingTag";

        when(mockedPersistenceController.removeTag(tagName)).thenReturn(null);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, TagDTO> result = domainController.removeTag(tagName);

        assertEquals("El tag con nombre " + tagName + " no existe.", result.fold(error -> error, TagDTO::getName), "Error messages do not match");
    }

    @Test
    public void removeTag_tagExists_shouldReturnTagDTO() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        String tagName = "ExistingTag";
        Tag tag = new Tag(tagName);

        when(mockedPersistenceController.removeTag(tagName)).thenReturn(tag);

        DomainController domainController = new DomainController(mockedPersistenceController);
        Either<String, TagDTO> result = domainController.removeTag(tagName);

        assertEquals(tagName, result.fold(error -> "", TagDTO::getName), "Tag names do not match");
    }

    @Test
    public void getShelf_test() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);

        List<String> expectedResult = Arrays.asList("Product1", "Product2", "Product3");
        when(mockedPersistenceController.getShelf()).thenReturn(expectedResult);
        List<String> result = domainController.getShelf();

        assertEquals(expectedResult, result);
    }

    @Test
    public void getShelf_emptyShelf() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);

        List<String> expectedResult = new ArrayList<>();
        when(mockedPersistenceController.getShelf()).thenReturn(expectedResult);
        List<String> result = domainController.getShelf();

        assertEquals(expectedResult, result);
    }

    @Test
    public void editShelf_emptyShelf_shouldReturnNoProductFound() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);
        List<String> shelf = new ArrayList<>();
        String product1 = "product1";
        String product2 = "product2";
        String expectedResult = "El producto " + product1 + " no se encuentra en la estanteria.";
        assertEquals(expectedResult, domainController.editShelf(shelf,product1,product2).fold(error -> error,right -> right), "Error messages do not match");
    }

    @Test
    public void editShelf_product1Shelf_shouldReturnNoProduct2Found() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);
        List<String> shelf = new ArrayList<>();
        String product1 = "product1";
        shelf.add(product1);
        String product2 = "product2";
        String expectedResult = "El producto " + product2 + " no se encuentra en la estanteria.";
        assertEquals(expectedResult, domainController.editShelf(shelf,product1,product2).fold(error -> error,right -> right), "Error messages do not match");
    }

    @Test
    public void editShelf_shouldReturnValid() {
        PersistenceController mockedPersistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(mockedPersistenceController);
        List<String> shelf = new ArrayList<>();
        String product1 = "product1";
        shelf.add(product1);
        String product2 = "product2";
        shelf.add(product2);
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(product2);
        expectedResult.add(product1);
        assertEquals(expectedResult, domainController.editShelf(shelf,product1,product2).fold(error -> error,right -> right), "Lists do not match.");
    }

    @Test
    public void testAddSimilarityProductNotFound() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product1";

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(null);

        Either<String, Boolean> result = domainController.addSimilarity(productName1, productName2, 0.5);

        assertEquals("El producto con nombre " + productName1 + " no existe.", result.fold(error ->
            error , success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testAddSimilaritySameProducts() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";
        Product product = new Product(productName, new ArrayList<>(), 1);

        Mockito.when(persistenceController.getProduct(productName)).thenReturn(product);

        Either<String, Boolean> result = domainController.addSimilarity(productName, productName, 0.5);

        assertEquals("Los productos son iguales.", result.fold(error ->
                error , success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testAddSimilarityWeightLessOrEqualsToZero() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "ProductOne";
        String productName2 = "ProductTwo";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);

        Either<String, Boolean> result = domainController.addSimilarity(productName1, productName2, 0.0);

        assertEquals("El peso debe ser mayor que 0.", result.fold(error ->
                error , success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testAddSimilaritySuccess() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "ProductOne";
        String productName2 = "ProductTwo";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);
        Mockito.when(persistenceController.addSimilarity(product1.getId(), product2.getId(), 0.5)).thenReturn(0.5);

        Either<String, Boolean> result = domainController.addSimilarity(productName1, productName2, 0.5);

        assertEquals("Success", result.fold(error -> null, success -> "Success"), "Operation not succeded." );
    }

    @Test
    public void testRemoveSimilarityProductNotFound() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product1";

        when(persistenceController.getProduct(productName1)).thenReturn(null);

        Either<String, Boolean> result = domainController.removeSimilarity(productName1, productName2);

        assertEquals("El producto con nombre " + productName1 + " no existe.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testRemoveSimilaritySameProducts() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";
        Product product = new Product(productName, new ArrayList<>(), 1);

        when(persistenceController.getProduct(productName)).thenReturn(product);

        Either<String, Boolean> result = domainController.removeSimilarity(productName, productName);

        assertEquals("Los productos son iguales.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testRemoveSimilaritySuccess() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "ProductOne";
        String productName2 = "ProductTwo";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        when(persistenceController.getProduct(productName1)).thenReturn(product1);
        when(persistenceController.getProduct(productName2)).thenReturn(product2);
        when(persistenceController.removeSimilarity(product1.getId(), product2.getId())).thenReturn(0.5);

        Either<String, Boolean> result = domainController.removeSimilarity(productName1, productName2);

        assertEquals("Success", result.fold(error -> null, success -> "Success"), "Operation not succeded.");
    }

    @Test
    public void testGetSavedSimilaritiesProductNotFound() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";

        when(persistenceController.getProduct(productName)).thenReturn(null);

        Either<String, List<Pair<String, Double>>> result = domainController.getSavedSimilarities(productName);

        assertEquals("El producto con nombre " + productName + " no existe.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testGetSavedSimilaritiesSuccess() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";
        Product product = new Product(productName, new ArrayList<>(), 1);

        Pair<String, Double> pair1 = new Pair<>("p1", 0.2);
        Pair<String, Double> pair2 = new Pair<>("p2", 0.8);
        List<Pair<String, Double>> pairs = Arrays.asList(pair1, pair2);

        when(persistenceController.getProduct(productName)).thenReturn(product);
        when(persistenceController.getSavedSimilarities(product.getId())).thenReturn(pairs);

        Either<String, List<Pair<String, Double>>> result = domainController.getSavedSimilarities(productName);

        assertEquals(pairs, result.fold(error -> null, success -> success), "Operation not succeded.");
    }

@Test
    public void testGetSavedSimilarityProductNotFound() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(null);

        Either<String, Double> result = domainController.getSavedSimilarity(productName1, productName2);

        assertEquals("El producto con nombre " + productName1 + " no existe.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testGetSavedSimilaritySameProducts() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";
        Product product = new Product(productName, new ArrayList<>(), 1);

        Mockito.when(persistenceController.getProduct(productName)).thenReturn(product);

        Either<String, Double> result = domainController.getSavedSimilarity(productName, productName);

        assertEquals("Los productos son iguales.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testGetSavedSimilarityNonExistentSimilarity() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);
        Mockito.when(persistenceController.getSavedSimilarity(product1.getId(), product2.getId())).thenReturn(null);

        Either<String, Double> result = domainController.getSavedSimilarity(productName1, productName2);

        assertEquals("No existe similitud entre " + productName1 + " y " + productName2 + ".", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testGetSavedSimilaritySuccess() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);
        Mockito.when(persistenceController.getSavedSimilarity(product1.getId(), product2.getId())).thenReturn(0.8);

        Either<String, Double> result = domainController.getSavedSimilarity(productName1, productName2);

        assertEquals(0.8, result.fold(error -> null, success -> success), "Operation not succeded.");
    }

    @Test
    public void testEditSimilarityProductNotFound() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(null);

        Either<String, Boolean> result = domainController.editSimilarity(productName1, productName2, 0.5);

        assertEquals("El producto con nombre " + productName1 + " no existe.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testEditSimilaritySameProducts() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName = "Product";
        Product product = new Product(productName, new ArrayList<>(), 1);

        Mockito.when(persistenceController.getProduct(productName)).thenReturn(product);

        Either<String, Boolean> result = domainController.editSimilarity(productName, productName, 0.5);

        assertEquals("Los productos son iguales.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testEditSimilarityWeightLessOrEqualsToZero() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "ProductOne";
        String productName2 = "ProductTwo";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);

        Either<String, Boolean> result = domainController.editSimilarity(productName1, productName2, 0.0);

        assertEquals("El peso debe ser mayor que 0.", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testEditSimilarityNonExistentSimilarity() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);
        Mockito.when(persistenceController.editSimilarity(product1.getId(), product2.getId(), 0.8)).thenReturn(null);
        Mockito.when(persistenceController.removeSimilarity(product1.getId(), product2.getId())).thenReturn(null);
        Either<String, Boolean> result = domainController.editSimilarity(productName1, productName2, 0.8);

        assertEquals("No existe similitud entre " + productName1 + " y " + productName2 + ".", result.fold(error -> error, success -> "Success"), "Error messages do not match");
    }

    @Test
    public void testEditSimilaritySuccess() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        String productName1 = "Product1";
        String productName2 = "Product2";
        Product product1 = new Product(productName1, new ArrayList<>(), 1);
        Product product2 = new Product(productName2, new ArrayList<>(), 2);

        Mockito.when(persistenceController.getProduct(productName1)).thenReturn(product1);
        Mockito.when(persistenceController.getProduct(productName2)).thenReturn(product2);
        Mockito.when(persistenceController.addSimilarity(product1.getId(), product2.getId(), 0.8)).thenReturn(0.8);
        Mockito.when(persistenceController.removeSimilarity(product1.getId(), product2.getId())).thenReturn(0.8);
        Either<String, Boolean> result = domainController.editSimilarity(productName1, productName2, 0.8);

        assertEquals(true, result.fold(error -> null, success -> success), "Operation not succeeded.");
    }

    @Test
    public void testClearAll() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        doNothing().when(persistenceController).clearAll();

        domainController.clearAll();

        verify(persistenceController,times(1)).clearAll();
    }

    @Test
    public void testEditMaxTag() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        Product p1 = new Product();
        assertEquals(10, p1.MaxTagNumber());

        domainController.editMaxTag(2);
        assertEquals(2, p1.MaxTagNumber());
    }

    @Test
    public void testGetProductsByPrefix() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        List<Pair<String,Double>> tags = new ArrayList<>();
        domainController.addProduct("patata", tags);
        domainController.addProduct("pat", tags);
        Product product1 = new Product("patata", tags, 1);
        Product product2 = new Product("pat", tags, 2);

        Mockito.when(persistenceController.getProductsByPrefix("patata")).thenReturn(List.of(product1));
        Mockito.when(persistenceController.getProductsByPrefix("pat")).thenReturn(List.of(product1, product2));

        List<ProductDTO> p1 = domainController.getProductsByPrefix("patata");
        List<ProductDTO> p2 = domainController.getProductsByPrefix("pat");

        assertEquals(1, p1.size());
        assertEquals(2, p2.size());
    }

    @Test
    public void testGetTagsByPrefix() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        domainController.addTag("tag1");
        domainController.addTag("tag2");

        Mockito.when(persistenceController.getTagsByPrefix("tag1")).thenReturn(List.of(new Tag("tag1")));
        Mockito.when(persistenceController.getTagsByPrefix("tag")).thenReturn(List.of(new Tag("tag1"), new Tag("tag2")));

        List<TagDTO> t1 = domainController.getTagsByPrefix("tag1");
        List<TagDTO> t2 = domainController.getTagsByPrefix("tag");

        assertEquals(1, t1.size());
        assertEquals(2, t2.size());
    }

    @Test
    public void testGetAllTags() {
        PersistenceController persistenceController = Mockito.mock(PersistenceController.class);
        DomainController domainController = new DomainController(persistenceController);

        domainController.addTag("b");
        domainController.addTag("c");
        domainController.addTag("a");

        Mockito.when(persistenceController.getAllTags()).thenReturn(List.of(new Tag("b"), new Tag("c"), new Tag("a")));
        List<TagDTO> t1 = domainController.getAllTags();

        assertEquals(3, t1.size());
        assertEquals("a", t1.get(0).getName());
        assertEquals("b", t1.get(1).getName());
        assertEquals("c", t1.get(2).getName());
    }
}
