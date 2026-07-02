package edu.upc.prop.cluster.persistence.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un nodo en un árbol de búsqueda de prefijos (Trie).
 * Cada nodo puede contener varios hijos, indicados por un mapa de caracteres a nodos, y
 * puede marcar el final de una etiqueta o el almacenamiento de un ID de producto.
 *
 * @author José Durán Foix
 */
public class TreeNode {
    /**
     * Mapa de caracteres a nodos hijos, representando los caracteres que siguen en la etiqueta o nombre del producto.
     */
    public Map<Character, TreeNode> children; // Hijos del nodo

    /**
     * Indica si el nodo es el final de una etiqueta o nombre de producto.
     */
    public boolean isEndOfTag; // Marca si este nodo es el final de un tag


    /**
     * ID del producto asociado a este nodo, si aplica.
     */
    public Integer productId;


    /**
     * Constructor que inicializa el nodo con un mapa vacío de hijos,
     * y marca el nodo como no siendo el final de una etiqueta ni conteniendo un ID de producto.
     */
    public TreeNode() {
        children = new HashMap<>();
        isEndOfTag = false;
        productId = null;
    }
}
