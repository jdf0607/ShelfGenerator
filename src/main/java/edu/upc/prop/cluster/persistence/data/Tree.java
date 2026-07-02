package edu.upc.prop.cluster.persistence.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Representa un árbol de búsqueda de prefijos (Trie).
 * Este árbol almacena etiquetas o nombres de productos, permitiendo buscar, agregar y eliminar etiquetas o productos de manera eficiente.
 * Además, permite realizar búsquedas por prefijo tanto para etiquetas como para productos.
 *
 * @author José Durán Foix
 */
public class Tree {
    private final TreeNode root;

    //PÚBLICAS

    /**
     * Constructor que inicializa el árbol con un nodo raíz vacío.
     */
    public Tree() {
        root = new TreeNode();
    }


    /**
     * Agrega una nueva etiqueta al árbol.
     * Recorre el árbol carácter por carácter, creando nodos intermedios según sea necesario.
     *
     * @param tag La etiqueta que se va a agregar al árbol.
     */
    public void addTag(String tag) {
        TreeNode currentNode = root;
        for (char lletra : tag.toCharArray()) {
            currentNode.children.putIfAbsent(lletra, new TreeNode());
            currentNode = currentNode.children.get(lletra);
        }
        currentNode.isEndOfTag = true; // Marca el final del tag
    }


    // Consultora

    /**
     * Obtiene todas las etiquetas almacenadas en el árbol.
     *
     * @return Una lista de todas las etiquetas.
     */
    public List<String> getAllTags() {
        List<String> allTags = new ArrayList<>();
        collectAllTags(root, new StringBuilder(), allTags);
        return allTags;
    }


    /**
     * Busca todas las etiquetas que comienzan con el prefijo dado.
     *
     * @param prefix El prefijo con el que deben comenzar las etiquetas.
     * @return Una lista de etiquetas que comienzan con el prefijo dado.
     */
    public List<String> searchTags(String prefix) {
        List<String> results = new ArrayList<>();
        TreeNode currentNode = root;

        // Navegar por el Tree hasta el final del prefijo
        for (char lletra : prefix.toCharArray()) {
            if (!currentNode.children.containsKey(lletra)) {
                //de momento retorna vacio, más adelante igual un either con error
                return results;

            }
            currentNode = currentNode.children.get(lletra);
        }

        // Recoger todos los tags que cuelgan de este nodo
        collectAllTags(currentNode, new StringBuilder(prefix), results);
        return results;
    }


    /**
     * Elimina una etiqueta del árbol.
     * Retorna verdadero si la etiqueta fue eliminada, falso en caso contrario.
     *
     * @param tag La etiqueta que se desea eliminar.
     * @return true si la etiqueta fue eliminada, false si no se encontraba.
     */
    public boolean deleteTag(String tag) {
        AtomicBoolean modificado = new AtomicBoolean(false);
        deleteTag (root, tag, 0, modificado);
        return modificado.get();
    }


    /*
     * Recolecta todas las etiquetas que cuelgan de un nodo dado, agregándolas a la lista de resultados.
     *
     * @param node    El nodo desde el cual se comienza a recolectar etiquetas.
     * @param prefix  El prefijo actual.
     * @param results La lista de resultados donde se almacenarán las etiquetas.
     */
    private void collectAllTags(TreeNode node, StringBuilder prefix, List<String> results) {
        if (node.isEndOfTag) {
            results.add(prefix.toString()); // Si es el final de un tag, añadirlo a los resultados
        }
        for (Map.Entry<Character, TreeNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey()); // Añadir el siguiente carácter
            collectAllTags(entry.getValue(), prefix, results); // Recursión para hijos
            prefix.deleteCharAt(prefix.length() - 1); // Deshacer el cambio para explorar otros hijos
        }
    }


    /*
     * Elimina una etiqueta recursivamente del árbol.
     *
     * @param node      El nodo actual.
     * @param tag       La etiqueta que se desea eliminar.
     * @param i         El índice actual de la etiqueta.
     * @param modificado Un objeto AtomicBoolean que indica si se ha realizado alguna modificación.
     * @return true si el nodo puede ser eliminado (es una hoja sin hijos), false en caso contrario.
     */
    private boolean deleteTag (TreeNode node, String tag, int i, AtomicBoolean modificado) {
        if ( i == tag.length())   {
            // si el tag es el final de un tag, eliminarlo
            if (node.isEndOfTag) {
                    node.isEndOfTag = false;
                    modificado.set(true);
                    return node.children.isEmpty(); // Si no queda ningún hijo, devolver true
            }
            else {
                    return false; // No se puede eliminar el tag, el nodo no es una hoja
            }
        }

        char letra = tag.charAt(i);
        TreeNode hijo = node.children.get(letra);

        if (hijo == null) {
            return false; // No se encuentra el tag
        }

        boolean puedeSerEliminado = deleteTag(hijo, tag, i + 1, modificado);

        if (puedeSerEliminado) {
            // Elimino al hijo y devuelvo al padre que no elimine nada más
            node.children.remove(letra);
            return node.children.isEmpty(); // Si no queda ningún hijo, devolver true
        }

        return false;
    }


    /* ADAPTACIÓ PER A LA CERCA DE PRODUCTES */

    /**
     * Agrega un producto al árbol, asociando su nombre con su ID.
     *
     * @param name El nombre del producto.
     * @param id   El ID del producto.
     */
    public void addProduct(String name, Integer id) {
        TreeNode currentNode = root;
        for (char lletra : name.toCharArray()) {
            currentNode.children.putIfAbsent(lletra, new TreeNode());
            currentNode = currentNode.children.get(lletra);
        }
        currentNode.isEndOfTag = true; // Marca el final del tag
        currentNode.productId = id;
    }


    /**
     * Verifica si un producto con el nombre dado está presente en el árbol.
     *
     * @param name El nombre del producto.
     * @return true si el producto está presente, false en caso contrario.
     */
    public boolean contains(String name) {
        TreeNode currentNode = root;

        // Navegar por el Tree hasta el final del prefijo
        for (char lletra : name.toCharArray()) {
            if (!currentNode.children.containsKey(lletra)) {
                //de momento retorna vacio, más adelante igual un either con error
                return false;
            }
            currentNode = currentNode.children.get(lletra);
        }
        return true;
    }


    /**
     * Busca los IDs de los productos que comienzan con el prefijo dado.
     *
     * @param prefix El prefijo con el que deben comenzar los productos.
     * @return Una lista de IDs de productos que comienzan con el prefijo dado.
     */
    public List<Integer> searchProducts(String prefix) {
        List<Integer> results = new ArrayList<>();
        TreeNode currentNode = root;

        // Navegar por el Tree hasta el final del prefijo
        for (char lletra : prefix.toCharArray()) {
            if (!currentNode.children.containsKey(lletra)) {
                //de momento retorna vacio, más adelante igual un either con error
                return results;

            }
            currentNode = currentNode.children.get(lletra);
        }

        // Recoger todos los tags que cuelgan de este nodo
        collectAllIdsProd(currentNode, new StringBuilder(prefix), results);
        return results;
    }


    /**
     * Obtiene el ID del producto asociado con un nombre dado.
     *
     * @param name El nombre del producto.
     * @return El ID del producto, o null si no se encuentra.
     */
    public Integer getProductId(String name) {
        TreeNode currentNode = root;

        // Navegar por el Tree hasta el final del prefijo
        for (char lletra : name.toCharArray()) {
            if (!currentNode.children.containsKey(lletra)) {
                //de momento retorna vacio, más adelante igual un either con error
                return null;
            }
            currentNode = currentNode.children.get(lletra);
        }
        return currentNode.productId;
    }


    /*
     * Recolecta todos los IDs de productos que cuelgan de un nodo dado, agregándolos a la lista de resultados.
     *
     * @param node    El nodo desde el cual se comienza a recolectar los IDs.
     * @param prefix  El prefijo actual.
     * @param results La lista de resultados donde se almacenarán los IDs de productos.
     */
    private void collectAllIdsProd(TreeNode node, StringBuilder prefix, List<Integer> results) {
        if (node.isEndOfTag) {
            results.add(node.productId); // Si es el final de un tag, añadirlo a los resultados
        }
        for (Map.Entry<Character, TreeNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey()); // Añadir el siguiente carácter
            collectAllIdsProd(entry.getValue(), prefix, results); // Recursión para hijos
            prefix.deleteCharAt(prefix.length() - 1); // Deshacer el cambio para explorar otros hijos
        }
    }


    /**
     * Elimina un producto del árbol, basado en su nombre.
     *
     * @param name El nombre del producto a eliminar.
     */
    public void removeProduct(String name) {
        removeProductAux(name, root, 0);
    }


    /*
     * Elimina un producto de manera recursiva.
     *
     * @param name        El nombre del producto a eliminar.
     * @param currentNode El nodo actual donde se encuentra el producto.
     * @param i           El índice en el nombre del producto.
     */
    private boolean removeProductAux(String name, TreeNode currentNode, Integer i) {
        if (i == name.length())   {
            // si el tag es el final de un tag, eliminarlo
            if (currentNode.isEndOfTag) {
                currentNode.isEndOfTag = false;
                currentNode.productId = null;
                return currentNode.children.isEmpty(); // Si no queda ningún hijo, devolver true
            }
            else {
                return false; // No se puede eliminar el tag, el nodo no es una hoja
            }
        }

        char letra = name.charAt(i);
        TreeNode hijo = currentNode.children.get(letra);

        if (hijo == null) {
            return false; // No se encuentra el tag
        }

        boolean puedeSerEliminado = removeProductAux(name, hijo, i + 1);

        if (puedeSerEliminado) {
            // Elimino al hijo y devuelvo al padre que no elimine nada más
            currentNode.children.remove(letra);
            return currentNode.children.isEmpty(); // Si no queda ningún hijo, devolver true
        }

        return false;
    }
}

