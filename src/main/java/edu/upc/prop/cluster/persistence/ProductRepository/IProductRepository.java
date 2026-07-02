package edu.upc.prop.cluster.persistence.ProductRepository;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;

import java.util.List;


/**
 * Interfaz que define las operaciones básicas de un repositorio de productos.
 * Proporciona métodos para gestionar productos, incluyendo la adición, eliminación,
 * edición y obtención de productos, así como la persistencia en formato JSON.
 *
 * <p>Esta interfaz es utilizada por las implementaciones de repositorios de productos, como
 * el repositorio en memoria.</p>
 *
 * @author Jorge Vico Lora
 */
public interface IProductRepository {


    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto a obtener.
     * @return El producto con el ID especificado, o null si no existe.
     */
    Product getProduct(int id);


    /**
     * Obtiene un producto por su nombre.
     *
     * @param name Nombre del producto a obtener.
     * @return El producto con el nombre especificado, o null si no existe.
     */
    Product getProductByName(String name);


    /**
     * Añade un nuevo producto al repositorio.
     *
     * @param name Nombre del producto.
     * @param tags Lista de pares de etiquetas y pesos asociados al producto.
     * @return El producto recién añadido, o null si el producto ya existía.
     */
    Product addProduct(String name, List<Pair<String, Double>> tags);


    /**
     * Elimina un producto del repositorio por su ID.
     *
     * @param id ID del producto a eliminar.
     * @return El producto eliminado, o null si no se encontraba el producto.
     */
    Product removeProductById(Integer id);


    /**
     * Obtiene una lista de todos los productos almacenados en el repositorio.
     *
     * @return Lista de todos los productos.
     */
    List<Product> getAllProducts();


    /**
     * Limpia el repositorio, eliminando todos los productos y reiniciando las estructuras de datos.
     */
    void clear();


    /**
     * Edita el nombre de un producto existente.
     *
     * @param oldName Nombre actual del producto.
     * @param newName Nuevo nombre para el producto.
     * @return El producto actualizado.
     */
    Product editProductName(String oldName, String newName);


    /**
     * Carga los productos desde un archivo JSON.
     *
     * @see #saveToJSON(String fileName)
     */
    void loadFromJSON();


    /**
     * Guarda los productos actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo JSON donde se guardarán los productos.
     */
    void saveToJSON(String fileName);


    /**
     * Obtiene una lista de productos cuyo nombre empieza con el prefijo especificado.
     *
     * @param prefix Prefijo de los productos a buscar.
     * @return Lista de productos que empiezan con el prefijo especificado.
     */
    List<Product> getProductsByPrefix(String prefix);
}
