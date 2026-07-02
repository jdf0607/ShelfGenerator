package edu.upc.prop.cluster.persistence;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.persistence.AlgorithmRepository.IAlgorithmRepository;
import edu.upc.prop.cluster.persistence.AlgorithmRepository.InMemoryAlgorithmRepository;
import edu.upc.prop.cluster.persistence.ProductRepository.IProductRepository;
import edu.upc.prop.cluster.persistence.ProductRepository.InMemoryProductRepository;
import edu.upc.prop.cluster.persistence.SimilarityRepository.ISimilarityRepository;
import edu.upc.prop.cluster.persistence.SimilarityRepository.InMemorySimilarityRepository;
import edu.upc.prop.cluster.persistence.TagRepository.ITagRepository;
import edu.upc.prop.cluster.persistence.TagRepository.InMemoryTagRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Controlador de persistencia que maneja la interacción con los repositorios de productos, tags, similitudes
 * y algoritmos en el sistema. Esta clase proporciona métodos para cargar, guardar, añadir, eliminar y modificar
 * los datos de los repositorios correspondientes.
 * <p>La clase se encarga de orquestar las operaciones relacionadas con la persistencia de datos de productos,
 * tags, similitudes y algoritmos, permitiendo la manipulación eficiente de estos recursos.</p>
 *
 * @author Jorge Vico Lora
 */
public class PersistenceController {
    private final IProductRepository productRepository;
    private final ITagRepository tagRepository;
    private final ISimilarityRepository similarityRepository;
    private final IAlgorithmRepository algorithmRepository;


    /**
     * Constructor que inicializa el controlador con los repositorios proporcionados.
     *
     * @param tagRepository Repositorio de tags.
     * @param productRepository Repositorio de productos.
     * @param similarityRepository Repositorio de similitudes.
     * @param algorithmRepository Repositorio de algoritmos.
     */
    public PersistenceController(ITagRepository tagRepository, IProductRepository productRepository, ISimilarityRepository similarityRepository, IAlgorithmRepository algorithmRepository) {
        this.tagRepository = tagRepository;
        this.productRepository = productRepository;
        this.similarityRepository = similarityRepository;
        this.algorithmRepository = algorithmRepository;
    }


    /**
     * Constructor por defecto que inicializa repositorios en memoria.
     */
    public PersistenceController() {
        tagRepository = new InMemoryTagRepository();
        productRepository = new InMemoryProductRepository();
        similarityRepository = new InMemorySimilarityRepository();
        algorithmRepository = new InMemoryAlgorithmRepository();
    }


    // Funciones relacionadas con tagRepository

    /**
     * Obtiene todas las tags almacenadas en el repositorio.
     *
     * @return Lista de todas las tags.
     */
    public List<Tag> getAllTags() {
        return tagRepository.getAllTags();
    }


    /**
     * Elimina todas las tags y devuelve una lista de las tags eliminadas.
     *
     * @return Lista de tags eliminadas.
     */
    public List<Tag> clearTags() {
        List<Tag> tags = tagRepository.getAllTags();
        tagRepository.clear();
        return tags;
    }


    /**
     * Obtiene una tag por su nombre.
     *
     * @param tagName Nombre de la tag.
     * @return La tag correspondiente o null si no existe.
     */
    public Tag getTag(String tagName) {
        return tagRepository.getTag(tagName);
    }


    /**
     * Añade una nueva tag al repositorio.
     *
     * @param tag La tag a añadir.
     * @return La tag añadida, o null si ya existe una tag con el mismo nombre.
     */
    public Tag addTag(Tag tag) {
        String tagName = tag.getName();
        if (tagRepository.getTag(tagName) != null)
            return null;

        return tagRepository.addTag(tag);
    }


    /**
     * Elimina una tag por su nombre y devuelve la tag eliminada.
     *
     * @param tagName Nombre de la tag a eliminar.
     * @return La tag eliminada.
     */
    public Tag removeTag(String tagName) {
        return tagRepository.removeTag(tagName);
    }


    /**
     * Obtiene un mapa con las tags y la cantidad de productos asociados a cada una.
     *
     * @return Mapa de nombres de tags y su cantidad de productos asociados.
     */
    public Map<String, Double> getTagsWithProductCount() {
        List<Tag> allTags = tagRepository.getAllTags();
        Map<String, Double> productCounts = new HashMap<>();
        for (Tag tag : allTags) {
            productCounts.put(tag.getName(), (double) tag.getProductCount());
        }
        return productCounts;
    }


    /**
     * Obtiene las tags cuyo nombre comienza con un prefijo determinado.
     *
     * @param prefix Prefijo a buscar en los nombres de las tags.
     * @return Lista de tags cuyo nombre comienza con el prefijo.
     */
    public List<Tag> getTagsByPrefix(String prefix) {
        return tagRepository.getPrefix(prefix);
    }


    /**
     * Carga las tags desde el archivo JSON.
     */
    public void loadTags() {
        tagRepository.loadFromJSON();
    }


    /**
     * Guarda las tags en un archivo JSON.
     *
     * @param fileName Nombre del archivo donde se guardarán las tags.
     */
    public void saveTags(String fileName) {
        tagRepository.saveToJSON(fileName);
    }

    // Funciones relacionadas con productRepository

    /**
     * Obtiene todos los productos almacenados en el repositorio.
     *
     * @return Lista de todos los productos.
     */
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }


    /**
     * Elimina todos los productos y devuelve una lista de los productos eliminados.
     *
     * @return Lista de productos eliminados.
     */
    public List<Product> clearProducts() {
        List<Product> products = productRepository.getAllProducts();
        productRepository.clear();
        return products;
    }


    /**
     * Obtiene un producto por su nombre.
     *
     * @param productName Nombre del producto.
     * @return El producto correspondiente o null si no existe.
     */
    public Product getProduct(String productName) {
        return productRepository.getProductByName(productName);
    }


    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto.
     * @return El producto correspondiente o null si no existe.
     */
    public Product getProduct(int id) {
        return productRepository.getProduct(id);
    }


    /**
     * Añade un nuevo producto al repositorio.
     *
     * @param product El producto a añadir.
     * @return El producto añadido.
     */
    public Product addProduct(Product product) {
        String productName = product.getName();
        List<Pair<String, Double>> tags = product.getTagList();
        return productRepository.addProduct(productName, tags);
    }


    /**
     * Elimina un producto por su nombre y devuelve el producto eliminado.
     *
     * @param productName Nombre del producto a eliminar.
     * @return El producto eliminado o null si no existe.
     */
    public Product removeProduct(String productName) {
        Product toDelete = productRepository.getProductByName(productName);
        if (toDelete == null)
            return null;

        return productRepository.removeProductById(toDelete.getId());
    }


    /**
     * Obtiene los productos cuyo nombre comienza con un prefijo determinado.
     *
     * @param prefix Prefijo a buscar en los nombres de los productos.
     * @return Lista de productos cuyo nombre comienza con el prefijo.
     */
    public List<Product> getProductsByPrefix(String prefix) {
        return productRepository.getProductsByPrefix(prefix);
    }


    /**
     * Carga los productos desde el archivo JSON.
     */
    public void loadProducts() {
        productRepository.loadFromJSON();
    }


    /**
     * Guarda los productos en un archivo JSON.
     *
     * @param fileName Nombre del archivo donde se guardarán los productos.
     */
    public void saveProducts(String fileName) {
        productRepository.saveToJSON(fileName);
    }


    /**
     * Modifica el nombre de un producto.
     *
     * @param oldName Nombre antiguo del producto.
     * @param newName Nombre nuevo del producto.
     * @return El producto con el nuevo nombre.
     */
    public Product editProductName(String oldName, String newName) {
        return productRepository.editProductName(oldName, newName);
    }


    // Funciones relacionadas con similarityRepository


    /**
     * Añade una similitud entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param weight Peso de la similitud.
     * @return El peso de la similitud añadida.
     */
    public Double addSimilarity(int id1, int id2, Double weight) {
        return similarityRepository.addSimilarity(id1, id2, weight);
    }


    /**
     * Edita la similitud entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param weight Nuevo peso de la similitud.
     * @return El nuevo peso de la similitud.
     */
    public Double editSimilarity(int id1, int id2, Double weight) {
        return similarityRepository.editSimilarity(id1, id2, weight);
    }


    /**
     * Obtiene la similitud guardada entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return El peso de la similitud entre los dos productos, o null si no existe.
     */
    public Double getSavedSimilarity(int id1, int id2) {
        return similarityRepository.getSimilarity(id1, id2);
    }


    /**
     * Elimina una similitud entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return El peso de la similitud eliminada.
     */
    public Double removeSimilarity(int id1, int id2) {
        return similarityRepository.removeSimilarity(id1, id2);
    }


    /**
     * Elimina todas las similitudes asociadas a un producto.
     *
     * @param id ID del producto.
     */
    public void removeSimilarities(int id) {
        similarityRepository.removeSimilarities(id);
    }


    /**
     * Obtiene las similitudes guardadas de un producto con los nombres de los productos relacionados.
     *
     * @param id ID del producto.
     * @return Lista de pares con los nombres de los productos relacionados y sus similitudes.
     */
    public List<Pair<String, Double>> getSavedSimilarities(int id) {
        List<Pair<Integer, Double>> unnamedSimilarities = similarityRepository.getSimilarities(id);
        List<Pair<String, Double>> namedSimilarities = new ArrayList<>();
        for (Pair<Integer, Double> IDs : unnamedSimilarities) {
            Product product = productRepository.getProduct(IDs.first());
            if (product != null) namedSimilarities.add(new Pair<>(product.getName(), IDs.second()));
        }
        return namedSimilarities;
    }


    /**
     * Obtiene todas las similitudes guardadas entre productos.
     *
     * @return Un mapa con las similitudes entre los productos.
     */
    public Map<Pair<Integer,Integer>, Double> getAllSimilarities() {
        return similarityRepository.getAll();
    }


    /**
     * Elimina todas las similitudes guardadas y devuelve el mapa con las similitudes eliminadas.
     *
     * @return Un mapa con las similitudes eliminadas.
     */
    public Map<Pair<Integer,Integer>, Double> clearSimilarities() {
        Map<Pair<Integer,Integer>, Double> similarities = similarityRepository.getAll();
        similarityRepository.clear();
        return similarities;
    }


    /**
     * Carga las similitudes desde un archivo JSON.
     */
    public void loadSimilarities() {
        similarityRepository.loadFromJSON();
    }


    /**
     * Guarda las similitudes en un archivo JSON.
     *
     * @param fileName Nombre del archivo donde se guardarán las similitudes.
     */
    public void saveSimilarities(String fileName) {
        similarityRepository.saveToJSON(fileName);
    }


    // Métodos relacionados con algorithmRepository

    /**
     * Carga el contenido del estante desde un archivo JSON.
     */
    public void loadShelf() { algorithmRepository.loadFromJSON(); }


    /**
     * Guarda el contenido del estante en un archivo JSON con el nombre proporcionado.
     *
     * @param fileName Nombre del archivo donde se guardará el estante.
     */
    public void saveShelfToFile(String fileName) {
        algorithmRepository.saveToJSON(fileName);
    }


    /**
     * Elimina todos los datos de los repositorios y guarda el estado actual en archivos JSON.
     * <p>
     * Se limpia el archivo main.shelfgenerator.
     */
    public void clearAll() {
        tagRepository.clear();
        productRepository.clear();
        similarityRepository.clear();
        algorithmRepository.clear();

        productRepository.saveToJSON("main.shelfgenerator");
        tagRepository.saveToJSON("main.shelfgenerator");
        algorithmRepository.saveToJSON("main.shelfgenerator");
        similarityRepository.saveToJSON("main.shelfgenerator");
    }


    /**
     * Guarda el estante de algoritmos en el repositorio y lo almacena en un archivo JSON.
     *
     * @param shelf Lista de algoritmos que representan el estante.
     */
    public void saveShelf(List<String> shelf) {
        algorithmRepository.saveShelf(shelf);
        algorithmRepository.saveToJSON("main.shelfgenerator");
    }


    /**
     * Obtiene el estante de algoritmos guardado en el repositorio.
     *
     * @return Lista de algoritmos del estante.
     */
    public List<String> getShelf() {
        return algorithmRepository.getShelf();
    }
}
