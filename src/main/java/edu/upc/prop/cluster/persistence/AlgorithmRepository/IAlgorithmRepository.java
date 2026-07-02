package edu.upc.prop.cluster.persistence.AlgorithmRepository;

import java.util.List;

/**
 * Interfaz para la gestión de los resultados de los algoritmos relacionados con productos y estanterías.
 * Define los métodos para manejar los resultados de un algoritmo, incluyendo la carga, guardado y limpieza de datos,
 * así como la verificación de la presencia de un producto en los resultados.
 *
 * @author José Durán Foix
 */
public interface IAlgorithmRepository {

    /**
     * Limpia los resultados almacenados y devuelve la lista eliminada.
     *
     * @return Una lista de los resultados eliminados.
     */
    List<String> clear();


    /**
     * Obtiene la lista de productos en la estantería.
     *
     * @return Una lista de nombres de productos en la estantería.
     */
    List<String> getShelf();


    /**
     * Guarda la lista de productos en la estantería.
     *
     * @param shelf Lista de nombres de productos a guardar en la estantería.
     */
    void saveShelf(List<String> shelf);


    /**
     * Carga los datos desde un archivo JSON.
     */
    void loadFromJSON();


    /**
     * Guarda los datos en un archivo JSON con el nombre especificado.
     *
     * @param fileName Nombre del archivo donde se guardarán los datos.
     */
    void saveToJSON(String fileName);


    /**
     * Verifica si un producto está presente en los resultados.
     *
     * @param name Nombre del producto a verificar.
     * @return `true` si el producto está en los resultados, `false` en caso contrario.
     */
    boolean inResult(String name);

}
