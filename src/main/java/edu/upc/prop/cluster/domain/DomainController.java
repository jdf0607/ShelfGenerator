package edu.upc.prop.cluster.domain;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.algorithms.Algorithm;
import edu.upc.prop.cluster.domain.algorithms.Backtracking;
import edu.upc.prop.cluster.domain.algorithms.ProAlgorithm;
import edu.upc.prop.cluster.domain.data.*;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.dto.TagDTO;
import edu.upc.prop.cluster.persistence.PersistenceController;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Controlador de dominio para gestionar productos, etiquetas, estanterías y similitudes.
 * Interactúa con el controlador de persistencia para realizar operaciones sobre los datos.
 * @author Alex Meca Moñino
 */
public class DomainController {
    private final PersistenceController persistenceController;


    /**
     * Constructor del controlador de dominio con un controlador de persistencia específico.
     *
     * @param persistenceController Controlador de persistencia utilizado para interactuar con la base de datos.
     */
    public DomainController(PersistenceController persistenceController) {
        this.persistenceController = persistenceController;
    }


    /**
     * Constructor del controlador de dominio que inicializa el controlador de persistencia por defecto.
     */
    public DomainController() {
        this.persistenceController = new PersistenceController();
    }


    /**
     * Obtiene todos los productos almacenados.
     *
     * @return Lista de objetos {@link ProductDTO} representando todos los productos.
     */
    public List<ProductDTO> getAllProducts() { return persistenceController.getAllProducts().stream().map(product -> new ProductDTO(product.getName(), product.getTags())).collect(Collectors.toList()); }


    /**
     * Obtiene productos cuyo nombre comienza con el prefijo dado.
     *
     * @param prefix Prefijo para filtrar los productos.
     * @return Lista de objetos {@link ProductDTO} cuyos nombres comienzan con el prefijo dado.
     */
    public List<ProductDTO> getProductsByPrefix(String prefix) {
        return persistenceController.getProductsByPrefix(prefix).stream().map(product -> new ProductDTO(product.getName(), product.getTags())).collect(Collectors.toList());
    }


    /**
     * Obtiene las etiquetas cuyo nombre comienza con el prefijo dado.
     *
     * @param prefix Prefijo para filtrar las etiquetas.
     * @return Lista de objetos {@link TagDTO} cuyos nombres comienzan con el prefijo dado.
     */
    public List<TagDTO> getTagsByPrefix(String prefix) {
        return persistenceController.getTagsByPrefix(prefix).stream().map(tag -> new TagDTO(tag.getName())).collect(Collectors.toList());
    }


    /**
     * Obtiene un producto específico por su nombre.
     *
     * @param name Nombre del producto a buscar.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un objeto {@link ProductDTO}.
     */
    public Either<String, ProductDTO> getProduct(String name) {
        Product product = persistenceController.getProduct(name);
        if(product == null) return Either.left("El producto con nombre " + name + " no existe.");
        else return Either.right(new ProductDTO(product.getName(), product.getTags()));
    }


    /**
     * Agrega un nuevo producto con un conjunto de etiquetas y sus respectivos pesos.
     *
     * @param name Nombre del producto.
     * @param tags Lista de pares de etiquetas y pesos.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link ProductDTO} del producto creado.
     */
    public Either<String, ProductDTO> addProduct(String name, List<Pair<String,Double>> tags) {
        if(tags.size() > Product.MaxTagNumber()) return Either.left("Numero máximo de tags alcanzado (" + Product.MaxTagNumber() + ").");
        Product product = persistenceController.addProduct(new Product(name,tags,0));
        if(product == null) return Either.left("Ya existe un producto con este nombre.");
        int id = product.getId();
        for(Pair<String,Double> t : tags) {
            Tag tag = persistenceController.getTag(t.first());
            if(tag == null) {
                tag = new Tag(t.first());
                tag.addProduct(t.second(), id);
                persistenceController.addTag(tag);
            }
            else tag.addProduct(t.second(), id);
        }
        return Either.right(new ProductDTO(product.getName(), product.getTags()));
    }


    /**
     * Elimina un producto por su nombre.
     *
     * @param name Nombre del producto a eliminar.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un valor booleano indicando si la eliminación fue exitosa.
     */
    public Either<String, Boolean> removeProduct(String name) {
        Product product = persistenceController.removeProduct(name);
        if (product == null) return Either.left("El producto con nombre " + name + " no existe.");
        persistenceController.removeSimilarities(product.getId());
        Map<String, Double> productTags = product.getTags();
        for (Map.Entry<String, Double> tag : productTags.entrySet()) {
            Tag t = persistenceController.getTag(tag.getKey());
            t.deleteProduct(product.getId());
        }
        return Either.right(true);
    }


    /**
     * Elimina un tag de un producto específico.
     *
     * @param productName Nombre del producto del cual se eliminará el tag.
     * @param tagName Nombre del tag a eliminar.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un valor booleano indicando si la eliminación fue exitosa.
     */
    public Either<String, Boolean> removeTagFromProduct(String productName, String tagName) {
        Product product = persistenceController.getProduct(productName);
        Tag tag = persistenceController.getTag(tagName);

        if(product == null) return Either.left("El producto con nombre " + productName + " no existe.");
        if(tag == null) return Either.left("El tag con nombre " + tagName + " no existe.");
        if(!product.hasTag(tagName)) return Either.left("El producto no contiene el tag " + tagName + ".");

        tag.deleteProduct(product.getId());
        product.removeTag(tagName);
        return Either.right(true);
    }


    /**
     * Agrega un tag a un producto con un peso específico.
     *
     * @param productName Nombre del producto al cual se le agregará el tag.
     * @param tagName Nombre del tag que se agregará al producto.
     * @param weight Peso del tag a agregar al producto.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link ProductDTO} del producto actualizado.
     */
    public Either<String,ProductDTO> addTagToProduct(String productName, String tagName, Double weight) {
        Product product = persistenceController.getProduct(productName);
        Tag tag = persistenceController.getTag(tagName);

        if(product == null) return Either.left("El producto con nombre " + productName + " no existe.");
        if(product.hasTag(tagName)) return Either.left("El producto ya tiene la tag " + tagName + ".");
        if(product.taglistIsFull()) return Either.left("El producto ya tiene el número máximo de tags.");

        if(tag == null) tag = persistenceController.addTag(new Tag(tagName));
        tag.addProduct(weight, product.getId());
        product.addTag(tagName, weight);
        return Either.right(new ProductDTO(product.getName(), product.getTags()));
    }


    /**
     * Edita el peso de un tag en un producto específico.
     *
     * @param productName Nombre del producto donde se editará el tag.
     * @param tagName Nombre del tag a editar.
     * @param weight Nuevo peso para el tag.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link ProductDTO} del producto actualizado.
     */
    public Either<String,ProductDTO> editTagFromProduct(String productName, String tagName, Double weight) {
        Product product = persistenceController.getProduct(productName);
        Tag tag = persistenceController.getTag(tagName);

        if(product == null) return Either.left("El producto con nombre " + productName + " no existe.");
        if(!product.hasTag(tagName)) return Either.left("El producto no tiene la tag " + tagName + ".");
        if(tag == null) return Either.left("El tag con nombre " + tagName + " no existe.");

        tag.changeWeight(weight, product.getId());
        product.changeWeight(tagName, weight);
        return Either.right(new ProductDTO(product.getName(), product.getTags()));
    }


    /**
     * Edita el nombre de un producto.
     *
     * @param productName Nombre del producto que se va a editar.
     * @param newName Nuevo nombre que se asignará al producto.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link ProductDTO} del producto actualizado.
     */
    public Either<String,ProductDTO> editProductName(String productName, String newName) {
        Product product = persistenceController.getProduct(productName);
        Product newNameProduct = persistenceController.getProduct(newName);

        if (newName.equals(productName)) return Either.left("El nombre del producto no ha cambiado.");
        if (product == null) return Either.left("El producto con nombre " + productName + " no existe.");
        if (newNameProduct != null) return Either.left("Ya existe un producto con el nombre " + newName + ".");

        Product p1 = persistenceController.editProductName(productName, newName);
        return Either.right(new ProductDTO(newName, p1.getTags()));
    }


    /**
     * Ejecuta el algoritmo de backtracking para generar una estantería de productos.
     *
     * @return Lista de nombres de productos generados por el algoritmo de backtracking.
     */
    public List<String> backtracking() {
        Backtracking b = new Backtracking();
        return algorithm(b);
    }


    /**
     * Ejecuta el algoritmo eficiente para generar una estantería de productos.
     *
     * @return Lista de nombres de productos generados por el algoritmo eficiente.
     */
    public List<String> proAlgorithm() {
        ProAlgorithm p = new ProAlgorithm();
        return algorithm(p);
    }


    /*
     * Método genérico para ejecutar un algoritmo de agrupamiento de productos.
     *
     * @param algorithm El algoritmo a ejecutar (puede ser backtracking o proAlgorithm).
     * @return Lista de nombres de productos generados por el algoritmo.
     */
    private List<String> algorithm(Algorithm algorithm) {
        List<Product> products = persistenceController.getAllProducts();
        Map<String,Double> tags = persistenceController.getTagsWithProductCount();
        Map<Pair<Integer,Integer>, Double> savedSimilarities = persistenceController.getAllSimilarities();
        List<Product> result = algorithm.execute(products, tags, savedSimilarities);
        List<String> resultNames = result.stream().map(Product::getName).collect(Collectors.toList());;
        persistenceController.saveShelf(resultNames);
        return resultNames;
    }


    /**
     * Obtiene todas las etiquetas ordenadas alfabéticamente.
     *
     * @return Lista de objetos {@link TagDTO} representando todas las etiquetas ordenadas.
     */
    public List<TagDTO> getAllTags() {
        List<Tag> tags = persistenceController.getAllTags();
        List<Tag> orderTags = new ArrayList<>(tags);
        orderTags.sort(Comparator.comparing(Tag::getName));

        List<TagDTO> result = new ArrayList<>();
        for(Tag tag : orderTags) {
            result.add(new TagDTO(tag.getName()));
        }

        return result;
    }


    /**
     * Agrega una nueva etiqueta.
     *
     * @param name Nombre de la nueva etiqueta.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link TagDTO} de la etiqueta creada.
     */
    public Either<String, TagDTO> addTag(String name) {
        Tag tag = persistenceController.addTag(new Tag(name));
        if(tag == null) return Either.left("Ya existe un tag con este nombre.");
        return Either.right(new TagDTO(tag.getName()));
    }


    /**
     * Elimina una etiqueta por su nombre.
     *
     * @param tagName Nombre de la etiqueta a eliminar.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el objeto {@link TagDTO} de la etiqueta eliminada.
     */
    public Either<String,TagDTO> removeTag(String tagName) {
        Tag tag = persistenceController.removeTag(tagName);
        if(tag == null) return Either.left("El tag con nombre " + tagName + " no existe.");
        List<Integer> IDs = tag.getIDs();
        for(Integer id : IDs) {
            Product product = persistenceController.getProduct(id);
            if(product != null) product.removeTag(tagName);
        }
        return Either.right(new TagDTO(tag.getName()));
    }

    /**
     * Intercambia dos productos en la estantería.
     *
     * @param shelf Lista de productos en la estantería.
     * @param product1 Nombre del primer producto a intercambiar.
     * @param product2 Nombre del segundo producto a intercambiar.
     * @return Un objeto {@link Either} que contiene un mensaje de error o la lista actualizada de productos en la estantería.
     */
    public Either<String,List<String>> editShelf(List<String> shelf, String product1, String product2) {
        int index1 = shelf.indexOf(product1);
        int index2 = shelf.indexOf(product2);
        if(index1 == -1) return Either.left("El producto " + product1 + " no se encuentra en la estanteria.");
        if(index2 == -1) return Either.left("El producto " + product2 + " no se encuentra en la estanteria.");
        Collections.swap(shelf, shelf.indexOf(product1), shelf.indexOf(product2));
        return Either.right(shelf);
    }


    /**
     * Obtiene la lista de productos en la estantería.
     *
     * @return Una lista de los nombres de los productos en la estantería.
     */
    public List<String> getShelf() {return new ArrayList<>(persistenceController.getShelf());}


    /**
     * Guarda la lista de productos en la estantería.
     *
     * @param shelf Lista de productos a guardar.
     */
    public void saveShelf(List<String> shelf) { persistenceController.saveShelf(shelf); }


    /**
     * Agrega una similitud entre dos productos con un peso especificado.
     *
     * @param productName1 Nombre del primer producto.
     * @param productName2 Nombre del segundo producto.
     * @param weight Peso de la similitud entre los productos.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un valor booleano indicando si la operación fue exitosa.
     */
    public Either<String, Boolean> addSimilarity(String productName1, String productName2, Double weight) {
        Product product1 = persistenceController.getProduct(productName1);
        Product product2 = persistenceController.getProduct(productName2);

        if(product1 == null) return Either.left("El producto con nombre " + productName1 + " no existe.");
        if(product2 == null) return Either.left("El producto con nombre " + productName2 + " no existe.");
        if(product1.equals(product2)) return Either.left("Los productos son iguales.");
        if(weight <= 0) return Either.left("El peso debe ser mayor que 0.");

        if(persistenceController.addSimilarity(product1.getId(), product2.getId(), weight) == null)
            return Either.left("Ya existe similitud entre " + productName1 + " y " + productName2 + ".");
        return Either.right(true);
    }


    /**
     * Elimina una similitud entre dos productos.
     *
     * @param productName1 Nombre del primer producto.
     * @param productName2 Nombre del segundo producto.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un valor booleano indicando si la eliminación fue exitosa.
     */
    public Either<String, Boolean> removeSimilarity(String productName1, String productName2) {
        Product product1 = persistenceController.getProduct(productName1);
        Product product2 = persistenceController.getProduct(productName2);

        if(product1 == null) return Either.left("El producto con nombre " + productName1 + " no existe.");
        if(product2 == null) return Either.left("El producto con nombre " + productName2 + " no existe.");
        if(product1.equals(product2)) return Either.left("Los productos son iguales.");

        if(persistenceController.removeSimilarity(product1.getId(), product2.getId()) == null)
            return Either.left("No existe similitud entre " + productName1 + " y " + productName2 + ".");
        return Either.right(true);
    }


    /**
     * Obtiene las similitudes guardadas para un producto específico.
     *
     * @param productName Nombre del producto.
     * @return Un objeto {@link Either} que contiene un mensaje de error o la lista de similitudes guardadas del producto.
     */
    public Either<String, List<Pair<String, Double>>> getSavedSimilarities(String productName) {
        Product product = persistenceController.getProduct(productName);

        if(product == null) return Either.left("El producto con nombre " + productName + " no existe.");

        return Either.right(persistenceController.getSavedSimilarities(product.getId()));
    }


    /**
     * Obtiene todas las similitudes guardadas entre productos.
     *
     * @return Un objeto {@link Either} que contiene un mensaje de error o un mapa de similitudes entre productos con sus respectivos pesos.
     */
    public Either<String, Map<Pair<String, String>, Double>> getSavedSimilarities() {
        Map<Pair<Integer, Integer>, Double> similarities = persistenceController.getAllSimilarities();

        Map<Pair<String, String>, Double> namedSimilarities = new HashMap<>();
        for (Pair<Integer, Integer> pair : similarities.keySet()) {
            Product product1 = persistenceController.getProduct(pair.first());
            Product product2 = persistenceController.getProduct(pair.second());

            if (product1 == null)
                return Either.left("El producto con ID " + pair.first() + " no existe.");
            if (product2 == null)
                return Either.left("El producto con ID " + pair.second() + " no existe.");

            // Añadir al mapa con los nombres de los productos
            namedSimilarities.put(
                    new Pair<>(product1.getName(), product2.getName()), similarities.get(pair)
            );
        }
        return Either.right(namedSimilarities);
    }


    /**
     * Obtiene la similitud guardada entre dos productos específicos.
     *
     * @param productName1 Nombre del primer producto.
     * @param productName2 Nombre del segundo producto.
     * @return Un objeto {@link Either} que contiene un mensaje de error o el valor de la similitud entre los productos.
     */
    public Either<String, Double> getSavedSimilarity(String productName1, String productName2) {
        Product product1 = persistenceController.getProduct(productName1);
        Product product2 = persistenceController.getProduct(productName2);

        if(product1 == null) return Either.left("El producto con nombre " + productName1 + " no existe.");
        if(product2 == null) return Either.left("El producto con nombre " + productName2 + " no existe.");
        if(product1.equals(product2)) return Either.left("Los productos son iguales.");
        Double similarity = persistenceController.getSavedSimilarity(product1.getId(), product2.getId());
        if (similarity == null) return Either.left("No existe similitud entre " + productName1 + " y " + productName2 + ".");
        return Either.right(similarity);
    }


    /**
     * Edita la similitud entre dos productos, eliminándola y luego agregando una nueva con el peso especificado.
     *
     * @param productName1 Nombre del primer producto.
     * @param productName2 Nombre del segundo producto.
     * @param weight Nuevo peso para la similitud.
     * @return Un objeto {@link Either} que contiene un mensaje de error o un valor booleano indicando si la operación fue exitosa.
     */
    public Either<String, Boolean> editSimilarity(String productName1, String productName2, Double weight) {
        Either<String,Boolean> a = removeSimilarity(productName1, productName2);
        String b = a.fold(left -> left, ignored -> null);
        if (b != null) return Either.left(b);
        return addSimilarity(productName1, productName2, weight);
    }


    /**
     * Carga los datos desde archivos JSON para productos, tags, estantería y similitudes.
     */
    public void loadFromJSON() {
        persistenceController.loadProducts();
        persistenceController.loadTags();
        persistenceController.loadShelf();
        persistenceController.loadSimilarities();
    }


    /**
     * Guarda los datos de productos, tags, estantería y similitudes a archivos JSON con una fecha específica en su nombre.
     *
     * @param date Fecha que se usará para nombrar los archivos.
     */
    public void saveToJSONDate(String date) {
        persistenceController.saveTags("saves/" + date + ".backup");
        persistenceController.saveProducts("saves/" + date + ".backup");
        persistenceController.saveShelfToFile("saves/" + date + ".backup");
        persistenceController.saveSimilarities("saves/" + date + ".backup");
        System.out.println(date);
    }


    /**
     * Guarda los datos de productos, tags, estantería y similitudes a archivos JSON con un nombre fijo.
     */
    public void saveToJSON() {
        persistenceController.saveTags("main.shelfgenerator");
        persistenceController.saveProducts("main.shelfgenerator");
        persistenceController.saveShelfToFile("main.shelfgenerator");
        persistenceController.saveSimilarities("main.shelfgenerator");
    }


    /**
     * Elimina todos los datos almacenados.
     */
    public void clearAll() { persistenceController.clearAll(); }


    /**
     * Edita el límite máximo de tags en un producto.
     *
     * @param maxTag Nuevo límite máximo de tags.
     */
    public void editMaxTag(int maxTag) {
        Product p = new Product();
        p.setTagLimit(maxTag);
    }
}
