package edu.upc.prop.cluster.persistence;


import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.ProductPair;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.persistence.data.Tree;
import edu.upc.prop.cluster.persistence.TagRepository.InMemoryTagRepository;
import edu.upc.prop.cluster.persistence.TagRepository.ITagRepository;
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
public class InMemoryTagRepositoryTest {
    ITagRepository repository;

    @Mock
    private Tree mockTree;

    @BeforeEach
    void setUp() {
        File file = new File("main.shelfgenerator");
        if (file.exists()) { file.delete(); }


        MockitoAnnotations.openMocks(this);

        repository = new InMemoryTagRepository(mockTree);
    }

    // Dado un repositorio vacío, puedo añadirle una tag
    @Test
    void shouldAddTagToRepository() {
        String tagName = "Ejemplo";
        Tag toAdd = new Tag(tagName);

        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        Tag addedTag = repository.addTag(toAdd);

        assertNotNull(addedTag);
        assertEquals(addedTag, toAdd);
    }

    // Dado un repositorio con una tag, puedo buscarla
    @Test
    void shouldReturnTagWhenExists() {
        String tagName = "Ejemplo";
        Tag toAdd = new Tag(tagName);

        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        Tag addedTag = repository.addTag(toAdd);
        Tag foundTag = repository.getTag(tagName);

        assertNotNull(foundTag);
        assertEquals(toAdd, foundTag);
        assertEquals(addedTag, foundTag);
    }

    // Dado un repositorio vacío, buscar una tag no la encuentra
    @Test
    void shouldNotReturnTagWhenNotExists() {
        String tagName = "Ejemplo";

        Tag foundTag = repository.getTag(tagName);

        assertNull(foundTag);
    }

    // Dado un repositorio con una tag, eliminarla la elimina
    @Test
    void testRemoveTag() {
        Tag tag = new Tag("removableTag");

        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        repository.addTag(tag);

        when(mockTree.deleteTag("removableTag")).thenReturn(true);
        Tag removedTag = repository.removeTag("removableTag");

        assertNotNull(removedTag, "The tag should be removed successfully.");
        assertEquals(tag, removedTag, "The removed tag should match the original tag.");
        assertFalse(repository.getAllTags().contains(tag), "The repository should no longer contain the removed tag.");
    }

    // Dado un repositorio vacío, intentar eliminar una tag retorna null
    @Test
    void testRemoveTagNotFound() {
        when(mockTree.deleteTag("removableTag")).thenReturn(false);
        Tag removedTag = repository.removeTag("nonexistentTag");
        assertNull(removedTag, "Removing a non-existent tag should return null.");
    }
    

    // Dado un repositorio con multiples tags, puedo obtener una lista con todas
    @Test
    void shouldReturnAllTags() {
        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        Tag tag3 = new Tag("Tag3");

        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        Tag addedTag1 = repository.addTag(tag1);
        Tag addedTag2 = repository.addTag(tag2);
        Tag addedTag3 = repository.addTag(tag3);

        assertNotNull(repository.getAllTags());
        assertTrue(repository.getAllTags().contains(addedTag1));
        assertTrue(repository.getAllTags().contains(addedTag2));
        assertTrue(repository.getAllTags().contains(addedTag3));
        assertEquals(3, repository.getAllTags().size());
    }

    // Dado un repositorio vacío, pedir todas las tags retorna una lista vacía
    @Test
    void shouldReturnEmptyListWhenNoTagsExist() {
        assertNotNull(repository.getAllTags());
        assertEquals(0, repository.getAllTags().size());
        assertEquals(Collections.emptyList(), repository.getAllTags());
    }

    // Dado un repositorio con multiples tags y borrar uno, pedirlos todos enseña solo los que no se han eliminado
    @Test
    void shouldReturnAlmostAllTags() {
        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        Tag tag3 = new Tag("Tag3");
        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        Tag addedTag1 = repository.addTag(tag1);
        Tag addedTag2 = repository.addTag(tag2);
        Tag addedTag3 = repository.addTag(tag3);

        assertNotNull(repository.getAllTags());
        assertTrue(repository.getAllTags().contains(addedTag1));
        assertTrue(repository.getAllTags().contains(addedTag2));
        assertTrue(repository.getAllTags().contains(addedTag3));
        assertEquals(3, repository.getAllTags().size());

        when(mockTree.deleteTag("Tag2")).thenReturn(true);
        Tag removedTag = repository.removeTag("Tag2");

        assertEquals(addedTag2, removedTag);
        assertNotNull(repository.getAllTags());
        assertTrue(repository.getAllTags().contains(addedTag1));
        assertFalse(repository.getAllTags().contains(addedTag2));
        assertTrue(repository.getAllTags().contains(addedTag3));
        assertEquals(2, repository.getAllTags().size());
    }

    @Test
    void testGetPrefix() {
        Tag tag1 = new Tag("prefixTest1");
        Tag tag2 = new Tag("prefixTest2");
        Tag tag3 = new Tag("anotherTag");
        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        repository.addTag(tag1);
        repository.addTag(tag2);
        repository.addTag(tag3);

        when(mockTree.searchTags("prefixTest")).thenReturn(List.of("prefixTest1", "prefixTest2"));

        List<Tag> prefixTags = repository.getPrefix("prefixTest");
        assertNotNull(prefixTags, "The prefix search should not return null.");
        assertEquals(2, prefixTags.size(), "The prefix search should return the correct number of tags.");
        assertTrue(prefixTags.stream().allMatch(tag -> tag.getName().startsWith("prefixTest")),
                "All returned tags should start with the given prefix.");
    }

    @Test
    void testGetPrefixNoMatch() {
        List<Tag> prefixTags = repository.getPrefix("nonexistentPrefix");
        assertNotNull(prefixTags);
        assertEquals(Collections.emptyList(), prefixTags);
    }

    @Test
    void testGetAllTags() {
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");

        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        repository.addTag(tag1);
        repository.addTag(tag2);

        List<Tag> allTags = repository.getAllTags();
        assertEquals(2, allTags.size(), "The repository should contain the correct number of tags.");
        assertTrue(allTags.contains(tag1) && allTags.contains(tag2), "The repository should contain all added tags.");
    }

    @Test
    void testClear() {
        when(mockTree.searchTags(any())).thenReturn(Collections.emptyList());
        repository.addTag(new Tag("tag1"));
        repository.addTag(new Tag("tag2"));

        repository.clear();
        assertTrue(repository.getAllTags().isEmpty(), "The repository should be empty after clearing.");
    }

    @Test
    void testGetProductCount() {
        Tag tag = new Tag("productCountTag");
        tag.addProduct(0.3,1); // Simulating product association
        tag.addProduct(0.5, 2);

        int productCount = repository.getProductCount(tag);
        assertEquals(2, productCount, "The product count should match the number of products associated with the tag.");
    }

  // Por corregir
   @Test
    void SaveToJson() {

        Tag t1 = new Tag ("t1");
        Tag t2 = new Tag ("t2");

        t1.addProduct(1.5,1);
        t1.addProduct(5.0,2);
        t2.addProduct(2.0,2);
        t2.addProduct(0.5,3 );
       when(mockTree.getProductId(any())).thenReturn(null);
        repository.addTag(t1);
        repository.addTag(t2);

        repository.saveToJSON("main.shelfgenerator");

        repository = new InMemoryTagRepository();
        repository.loadFromJSON();

        assertTrue(t1.equals(repository.getTag("t1")));
        assertTrue(t2.equals(repository.getTag("t2")));
        assertEquals(2, repository.getAllTags().size());
    }

    @Test
    void LoadFromJson() {
        Tag t1 = new Tag ("t1");
        Tag t2 = new Tag ("t2");
        Tag t3 = new Tag ("t3");
        Tag t4 = new Tag ("t4");
        t1.addProduct(1.5,1);
        t1.addProduct(5.0,4);
        t2.addProduct(2.0,2);
        t2.addProduct(0.5,3 );
        t3.addProduct(0.5,3 );
        t4.addProduct(3.0,4 );

        when(mockTree.getProductId(any())).thenReturn(null);
        Tag addedT1 = repository.addTag(t1);
        Tag addedT2 = repository.addTag(t2);
        Tag addedT3 = repository.addTag(t3);
        Tag addedT4 = repository.addTag(t4);

        when(mockTree.deleteTag("t2")).thenReturn(true);
        Tag deletet2 = repository.removeTag("t2");
        when(mockTree.deleteTag("t3")).thenReturn(true);
        Tag deletet3 = repository.removeTag("t3");

        repository.saveToJSON("main.shelfgenerator");
        repository = new InMemoryTagRepository(new Tree());
        repository.loadFromJSON();

        assertEquals(2, repository.getAllTags().size());
        Tag t5 = new Tag ("t5");
        Tag t6 = new Tag ("t6");
        Tag t7 = new Tag ("t7");

        t5.addProduct(1.0,3);
        t6.addProduct(2.0,4);
        t7.addProduct(0.5,5);

        repository.addTag(t5);
        repository.addTag(t6);
        repository.addTag(t7);

        assertEquals(5, repository.getAllTags().size());

    }

    @Test
    void testInTags() {
        Tag t1 = new Tag ("t1");
        Tag t2 = new Tag ("t2");

        repository.addTag(t1);

        assertTrue(repository.inTags(t1));
        assertFalse(repository.inTags(t2));
    }
}



