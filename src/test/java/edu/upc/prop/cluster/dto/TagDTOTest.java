package edu.upc.prop.cluster.dto;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alejandro Ruiz Patón
 */
public class TagDTOTest {

    @Test
    public void testConstructorAndGetName() {
        String expectedName = "Tag1";
        TagDTO tag = new TagDTO(expectedName);

        assertNotNull(tag);
        assertEquals(expectedName, tag.getName());
    }


    @Test
    public void testNullName() {
        String nullName = null;
        TagDTO tag = new TagDTO(nullName);

        assertNotNull(tag);
        assertNull(tag.getName());
    }

    @Test
    public void testEmptyName() {
        String emptyName = "";
        TagDTO tag = new TagDTO(emptyName);

        assertNotNull(tag);
        assertEquals(emptyName, tag.getName());
    }


    @Test
    public void testSetBehavior() {
        TagDTO tag1 = new TagDTO("Tag1");
        TagDTO tag2 = new TagDTO("Tag2");
        TagDTO tagDuplicate = new TagDTO("Tag1");

        Set<TagDTO> tagSet = new HashSet<>();
        tagSet.add(tag1);
        tagSet.add(tag2);
        tagSet.add(tagDuplicate);

        // Verificamos que el conjunto tiene todos los elementos
        assertEquals(3, tagSet.size());
    }

}
