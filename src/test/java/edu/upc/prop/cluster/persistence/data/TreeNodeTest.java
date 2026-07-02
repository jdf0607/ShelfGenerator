package edu.upc.prop.cluster.persistence.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alejandro Ruiz Patón
 */
public class TreeNodeTest {

    @Test
    public void testConstructor() {
        TreeNode root = new TreeNode();

        assertNotNull(root.children);
        assertTrue(root.children.isEmpty());
        assertFalse(root.isEndOfTag);
        assertNull(root.productId);
    }


    @Test
    public void testAddChild() {
        TreeNode root = new TreeNode();
        TreeNode child = new TreeNode();

        root.children.put('a', child);

        assertTrue(root.children.containsKey('a'));
        assertEquals(child, root.children.get('a'));
        assertEquals(1, root.children.size());

        root.children.put('b', child);
        assertTrue(root.children.containsKey('b'));
        assertEquals(child, root.children.get('b'));
        assertEquals(2, root.children.size());
    }


    @Test
    public void testRemoveChild() {
        TreeNode root = new TreeNode();
        TreeNode child = new TreeNode();

        root.children.put('a', child);

        assertTrue(root.children.containsKey('a'));
        assertEquals(child, root.children.get('a'));
        assertEquals(1, root.children.size());

        root.children.remove('a');
        assertTrue(root.children.isEmpty());
    }


    @Test
    public void testSetIsEndOfTag() {
        TreeNode root = new TreeNode();

        root.isEndOfTag = true;

        assertTrue(root.isEndOfTag);
    }


    @Test
    public void testSetProductId() {
        TreeNode root = new TreeNode();

        root.productId = 1;

        assertNotNull(root.productId);
        assertTrue(root.productId == 1);
    }


    @Test
    public void testComplexTree() {
        TreeNode root = new TreeNode();
        TreeNode child = new TreeNode();
        TreeNode child2 = new TreeNode();

        root.children.put('a', child);
        root.children.put('b', child2);

        child.isEndOfTag = true;
        child.productId = 2;

        // Root
        assertTrue(root.children.containsKey('a'));
        assertTrue(root.children.containsKey('b'));
        assertEquals(2, root.children.size());
        assertFalse(root.isEndOfTag);
        assertNull(root.productId);

        // Child
        assertTrue(child.children.isEmpty());
        assertTrue(child.isEndOfTag);
        assertEquals(2, child.productId);

        // Child2
        assertTrue(child2.children.isEmpty());
        assertFalse(child2.isEndOfTag);
        assertNull(child2.productId);
    }
}