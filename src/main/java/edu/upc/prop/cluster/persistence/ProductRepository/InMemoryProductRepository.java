package edu.upc.prop.cluster.persistence.ProductRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.persistence.data.Tree;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implementación en memoria del repositorio de productos, que gestiona productos en una estructura de datos basada en mapas
 * y un árbol de búsqueda de prefijos para realizar búsquedas eficientes por nombre.
 *
 * @author Alejandro Ruiz Patón
 */
public class InMemoryProductRepository implements IProductRepository {
    private Map<Integer, Product> idToProduct;      // Estructura principal que mapea una ID con un producto
    private Tree nameProdToId;                      // Estructura que mapea el nombre de un producto con su ID (útil para buscador)
    private PriorityQueue<Integer> availableIDs;    // Cola para almacenar los IDs libres de menor a mayor
    private static final String JSON_FILE = "main.shelfgenerator";


    /**
     * Constructor que inicializa el repositorio con un árbol de búsqueda dado.
     *
     * @param tree Árbol de búsqueda para asociar nombres de productos con sus IDs.
     */
    public InMemoryProductRepository(Tree tree) {
        idToProduct = new TreeMap<>();
        nameProdToId = tree;
        availableIDs = new PriorityQueue<>(List.of(1));
        loadFromJSON();
    }


    /**
     * Constructor que inicializa el repositorio con un árbol de búsqueda vacío.
     */
    public InMemoryProductRepository() {
        idToProduct = new TreeMap<>();
        nameProdToId = new Tree();
        availableIDs = new PriorityQueue<>(List.of(1));
        loadFromJSON();
    }


    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto a obtener.
     * @return El producto con el ID especificado, o null si no existe.
     */
    public Product getProduct(int id) {
        return idToProduct.get(id);
    }


    /**
     * Obtiene un producto por su nombre.
     *
     * @param name Nombre del producto a obtener.
     * @return El producto con el nombre especificado, o null si no existe.
     */
    public Product getProductByName(String name) {
        Integer id = nameProdToId.getProductId(name);
        if (id == null) return null;
        return idToProduct.get(id);
    }


    /**
     * Devuelve una lista de productos cuyo nombre empieza con el prefijo especificado.
     *
     * @param prefix Prefijo de los productos a buscar.
     * @return Lista de productos que empiezan con el prefijo especificado.
     */
    public List<Product> getProductsByPrefix(String prefix) {
        List<Integer> ids = nameProdToId.searchProducts(prefix);
        List<Product> products = new ArrayList<>();
        for (Integer id : ids) {
            products.add(getProduct(id));
        }
        return products;
    }


    /**
     * Edita el nombre de un producto.
     *
     * @param oldName Nombre actual del producto.
     * @param newName Nuevo nombre para el producto.
     * @return El producto actualizado.
     */
    public Product editProductName(String oldName, String newName) {
        int id = nameProdToId.getProductId(oldName);
        nameProdToId.removeProduct(oldName);

        Product p = idToProduct.get(id);
        p.setName(newName);

        nameProdToId.addProduct(newName, id);

        return p;
    }


    /**
     * Añade un nuevo producto al repositorio.
     *
     * @param name Nombre del producto.
     * @param tags Lista de pares de etiquetas y pesos asociados al producto.
     * @return El producto recién añadido, o null si el producto ya existía.
     */
    public Product addProduct(String name, List<Pair<String,Double>> tags) {
        Integer id = nameProdToId.getProductId(name);
        if (id == null) {   // Si el producto no existe
            id = availableIDs.poll();   // Recupero el primer id libre
            if (availableIDs.isEmpty()) availableIDs.add(id+1);   // Si la cola de IDs se ha quedado vacía inserto el siguiente valor de ID libre
            Product p = new Product(name, tags, id);

            // Inserto el producto y actualizo los mapeos
            idToProduct.put(id,p);
            nameProdToId.addProduct(name,id);
            return p;
        }
        return null;
    }


    /**
     * Elimina un producto del repositorio por su ID.
     *
     * @param id ID del producto a eliminar.
     * @return El producto eliminado, o null si no se encontraba el producto.
     */
    public Product removeProductById(Integer id) {
        Product p = idToProduct.remove(id);
        if (p != null) {    // Si se ha encontrado el producto también se elimina el mapeo de nombre a id
            String name = p.getName();
            nameProdToId.removeProduct(name);
            availableIDs.add(id);   // Esta id se podrá reciclar
        }
        return p;
    }


    /**
     * Obtiene una lista de todos los productos almacenados en el repositorio.
     *
     * @return Lista de todos los productos.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(idToProduct.values());
    }


    /**
     * Limpia el repositorio, eliminando todos los productos y reiniciando las estructuras de datos.
     */
    public void clear() {
        idToProduct.clear();
        availableIDs = new PriorityQueue<>(List.of(1));
        nameProdToId = new Tree();
    };


    // ----- Funciones de lectura y escritura en JSON ----- //


    /**
     * Carga los productos desde un archivo JSON.
     *
     * @see #JSON_FILE
     */
    public void loadFromJSON() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(JSON_FILE);

            if (file.exists()) {
                JsonNode rootNode = mapper.readTree(file);
                JsonNode sectionNode = rootNode.get("products");

                availableIDs.clear();
                if(sectionNode != null) idToProduct = mapper.convertValue(sectionNode, new TypeReference<Map<Integer, Product>>() {});
                int lastId = 0;
                for (Map.Entry<Integer, Product> entry : idToProduct.entrySet()) {
                    int id = entry.getKey();
                    Product p = entry.getValue();

                    // Actualizo las IDs disponibles
                    for (int i = lastId + 1; i < id; i++) {
                        availableIDs.add(i);
                    }
                    lastId = id;

                    // Actualizo el árbol de búsquedas por nombre
                    nameProdToId.addProduct(p.getName(), id);
                }
                availableIDs.add(lastId + 1); // Añado la última ID disponible que es la siguiente a la del producto con ID más grande
            }
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
        }
    }


    /**
     * Guarda los productos actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo JSON donde se guardarán los productos.
     */
    public void saveToJSON(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(fileName);

            ObjectNode rootNode;

            if (file.exists()) { rootNode = (ObjectNode) mapper.readTree(file); }
            else { rootNode = mapper.createObjectNode(); }

            JsonNode sectionNode = mapper.valueToTree(idToProduct);
            rootNode.set("products", sectionNode);

            // mapper ya crea el archivo en el caso de que no exista, luego sobreescribe el nuevo contenido vaciando lo que ya había.
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
        }
    }
}
