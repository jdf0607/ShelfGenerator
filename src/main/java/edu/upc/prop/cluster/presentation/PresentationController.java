package edu.upc.prop.cluster.presentation;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.DomainController;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.dto.TagDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de presentación que actúa como intermediario entre la interfaz de usuario (vistas)
 * y la lógica de negocio (controlador de dominio). Este controlador gestiona las interacciones del usuario
 * y delega las operaciones de negocio en el {@link DomainController}.
 *
 */
public class PresentationController {
    /* El marco principal de la aplicación */
    private final MainFrame mainFrame;

    /* El controlador de dominio que maneja la lógica de negocio */
    private final DomainController domainController;


    /**
     * Constructor de la clase {@code PresentationController}.
     * Inicializa el controlador de dominio y el marco principal de la aplicación.
     */
    public PresentationController() {
        this.domainController = new DomainController();
        mainFrame = new MainFrame(this);
    }

    /**
     * Inicia la aplicación mostrando la ventana principal.
     */
    public void start() {
        mainFrame.show();
        // Establecer bool cargado de estantería en caso de que lo esté
    }


    /**
     * Obtiene todos los productos.
     *
     * @return Lista de objetos {@link ProductDTO} representando todos los productos.
     */
    public List<ProductDTO> getAllProducts() {
        return this.domainController.getAllProducts();
    }


    /**
     * Obtiene los productos cuyo nombre empieza con el prefijo dado.
     *
     * @param prefix El prefijo con el que deben comenzar los productos.
     * @return Lista de objetos {@link ProductDTO} que coinciden con el prefijo.
     */
    public List<ProductDTO> getProductByPrefix(String prefix) {
        return domainController.getProductsByPrefix(prefix);
    }


    /**
     * Obtiene un producto por su nombre.
     *
     * @param name El nombre del producto.
     * @return Un objeto {@link Either} que contiene el producto si se encuentra, o un mensaje de error si no.
     */
    public Either<String, ProductDTO> getProduct(String name) {
        return domainController.getProduct(name);
    }


    /**
     * Añade un producto con el nombre y las etiquetas dadas.
     *
     * @param name El nombre del producto.
     * @param tags Lista de pares de etiquetas y sus respectivos pesos.
     * @return Un objeto {@link Either} que contiene el producto si se añade correctamente, o un mensaje de error.
     */
    public Either<String, ProductDTO> addProduct(String name, List<Pair<String,Double>> tags) {
        return domainController.addProduct(name, tags);
    }


    /**
     * Elimina un producto por su nombre.
     *
     * @param name El nombre del producto a eliminar.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, Boolean> removeProduct(String name) {
        return domainController.removeProduct(name);
    }


    /**
     * Elimina una etiqueta de un producto.
     *
     * @param productName El nombre del producto.
     * @param tagName El nombre de la etiqueta a eliminar.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, Boolean> removeTagFromProduct(String productName, String tagName) {
        return domainController.removeTagFromProduct(productName, tagName);
    }


    /**
     * Añade una etiqueta a un producto con un peso dado.
     *
     * @param productName El nombre del producto.
     * @param tagName El nombre de la etiqueta.
     * @param weight El peso asociado a la etiqueta.
     * @return Un objeto {@link Either} que contiene el producto si se añade correctamente, o un mensaje de error.
     */
    public Either<String, ProductDTO> addTagToProduct(String productName, String tagName, Double weight) {
        return domainController.addTagToProduct(productName, tagName, weight);
    }


    /**
     * Edita una etiqueta de un producto, cambiando su peso.
     *
     * @param productName El nombre del producto.
     * @param tagName El nombre de la etiqueta a editar.
     * @param weight El nuevo peso de la etiqueta.
     * @return Un objeto {@link Either} que contiene el producto si se edita correctamente, o un mensaje de error.
     */
    public Either<String, ProductDTO> editTagFromProduct(String productName, String tagName, Double weight) {
        return domainController.editTagFromProduct(productName, tagName, weight);
    }


    /**
     * Edita el nombre de un producto.
     *
     * @param productName El nombre del producto.
     * @param newName El nuevo nombre para el producto.
     * @return Un objeto {@link Either} que contiene el producto si se edita correctamente, o un mensaje de error.
     */
    public Either<String, ProductDTO> editProductName(String productName, String newName) {
        return domainController.editProductName(productName, newName);
    }


    /**
     * Ejecuta el algoritmo de backtracking para encontrar soluciones.
     *
     * @return Lista de soluciones encontradas por el algoritmo de backtracking.
     */
    public List<String> executeBacktracking() {
        return domainController.backtracking();
    }


    /**
     * Ejecuta el algoritmo Pro para encontrar soluciones.
     *
     * @return Lista de soluciones encontradas por el algoritmo Pro.
     */
    public List<String> executeProAlgorithm() {
        return domainController.proAlgorithm();
    }


    /**
     * Obtiene todas las etiquetas existentes.
     *
     * @return Lista de objetos {@link TagDTO} representando todas las etiquetas.
     */
    public List<TagDTO> getAllTags() {
        return domainController.getAllTags();
    }


    /**
     * Añade una nueva etiqueta.
     *
     * @param name El nombre de la etiqueta.
     * @return Un objeto {@link Either} que contiene la etiqueta si se añade correctamente, o un mensaje de error.
     */
    public Either<String, TagDTO> addTag(String name) {
        return domainController.addTag(name);
    }


    /**
     * Elimina una etiqueta.
     *
     * @param name El nombre de la etiqueta a eliminar.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, TagDTO> removeTag(String name) {
        return domainController.removeTag(name);
    }


    /**
     * Edita la estantería, intercambiando dos productos dados.
     *
     * @param shelf La lista de productos en la estantería.
     * @param product1 El nombre del primer producto.
     * @param product2 El nombre del segundo producto.
     * @return Un objeto {@link Either} que contiene la nueva estantería si la operación fue exitosa, o un mensaje de error.
     */
    public Either<String, List<String>> editShelf(List<String> shelf, String product1, String product2) {
        return domainController.editShelf(shelf, product1, product2);
    }


    /**
     * Obtiene la estantería actual.
     *
     * @return Lista de nombres de productos en la estantería.
     */
    public List<String> getShelf() {
        return domainController.getShelf();
    }


    /**
     * Añade una similitud entre dos productos con un peso dado.
     *
     * @param product1 El nombre del primer producto.
     * @param product2 El nombre del segundo producto.
     * @param weight El peso de la similitud.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, Boolean> addSimilarity(String product1, String product2, Double weight) {
        return domainController.addSimilarity(product1, product2, weight);
    }


    /**
     * Elimina la similitud entre dos productos.
     *
     * @param product1 El nombre del primer producto.
     * @param product2 El nombre del segundo producto.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, Boolean> removeSimilarity(String product1, String product2) {
        return domainController.removeSimilarity(product1, product2);
    }


    /**
     * Obtiene las similitudes guardadas para un producto.
     *
     * @param productName El nombre del producto.
     * @return Un objeto {@link Either} que contiene una lista de pares de etiquetas y sus pesos, o un mensaje de error.
     */
    public Either<String, List<Pair<String,Double>>> getSavedSimilarities(String productName) {
        return domainController.getSavedSimilarities(productName);
    }


    /**
     * Obtiene la similitud guardada entre dos productos.
     *
     * @param product1 El nombre del primer producto.
     * @param product2 El nombre del segundo producto.
     * @return Un objeto {@link Either} que contiene el valor de la similitud, o un mensaje de error.
     */
    public Either<String, Double> getSavedSimilarity(String product1, String product2) {
        return domainController.getSavedSimilarity(product1, product2);
    }


    /**
     * Edita una similitud entre dos productos, cambiando su peso.
     *
     * @param product1 El nombre del primer producto.
     * @param product2 El nombre del segundo producto.
     * @param weight El nuevo peso de la similitud.
     * @return Un objeto {@link Either} que indica si la operación fue exitosa o no.
     */
    public Either<String, Boolean> editSimilarity(String product1, String product2, Double weight) {
        return domainController.editSimilarity(product1, product2, weight);
    }


    /**
     * Limpia todos los datos, eliminando productos y etiquetas.
     */
    public void clearAll() {
        domainController.clearAll();
    }


    /**
     * Obtiene las etiquetas que empiezan con el prefijo dado.
     *
     * @param prefix El prefijo con el que deben comenzar las etiquetas.
     * @return Lista de nombres de etiquetas que coinciden con el prefijo.
     */
    public List<String> getTagsByPrefix(String prefix) {
        return domainController.getTagsByPrefix(prefix).stream().map(TagDTO::getName).collect(Collectors.toList());
    }


    /**
     * Maneja la adición de un producto con etiquetas y pesos.
     *
     * @param name El nombre del producto.
     * @param tagsWithWeights Lista de pares de etiquetas y pesos.
     * @return Un objeto {@link Either} que contiene el producto si se añade correctamente, o un mensaje de error.
     */
    public Either<String, ProductDTO> handleAddProduct(String name, List<Pair<String, Double>> tagsWithWeights) {
        return domainController.addProduct(name, tagsWithWeights);
    }


    /**
     * Maneja la adición de una nueva etiqueta.
     *
     * @param tag El objeto {@link TagDTO} que representa la etiqueta a añadir.
     * @return Un objeto {@link Either} que contiene la etiqueta si se añade correctamente, o un mensaje de error.
     */
    public Either<String, TagDTO> handleAddTag(TagDTO tag) {
        return domainController.addTag(tag.getName());
    }


    /**
     * Crea una nueva copia de seguridad de los datos de la aplicación con una fecha y hora.
     */
    public void createSave() {
        // Obtener la fecha y hora actual
        LocalDateTime dateTime = LocalDateTime.now();

        // Formatear la fecha con segundos incluidos
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String dateTimeString = dateTime.format(format);

        // Guardar usando el controlador de dominio
        domainController.saveToJSONDate(dateTimeString);
    }


    /**
     * Guarda los datos actuales en el archivo JSON.
     */
    public void save() {
        domainController.saveToJSON();
    }


    /**
     * Edita el número máximo de etiquetas permitidas.
     *
     * @param maxTag El nuevo número máximo de etiquetas.
     */
    public void editMaxTag(int maxTag) {
        domainController.editMaxTag(maxTag);
    }


    /**
     * Obtiene todas las similitudes guardadas.
     *
     * @return Un objeto {@link Either} que contiene un mapa de similitudes entre productos y sus pesos.
     */
    public Either<String, Map<Pair<String, String>, Double>> getSavedSimilarities() {
        return domainController.getSavedSimilarities();
    }

    /**
     * Guarda la estantería actual en un archivo.
     *
     * @param shelf Lista de nombres de productos en la estantería.
     */
    public void saveShelf(List<String> shelf) { domainController.saveShelf(shelf); }

    public void loadFromJSON() {
        domainController.loadFromJSON();
    }

}
