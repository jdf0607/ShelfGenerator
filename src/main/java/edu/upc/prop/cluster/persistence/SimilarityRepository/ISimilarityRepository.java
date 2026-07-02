package edu.upc.prop.cluster.persistence.SimilarityRepository;

import edu.upc.prop.cluster.common.Pair;

import java.util.List;
import java.util.Map;


/**
 * Interfaz que define los métodos para gestionar las similitudes entre productos.
 * <p>Esta interfaz proporciona las operaciones básicas para obtener, añadir, editar,
 * eliminar y consultar similitudes entre productos a través de sus IDs. Además, permite
 * cargar y guardar las similitudes en un archivo JSON.</p>
 *
 * @author Jorge Vico Lora
 */
public interface ISimilarityRepository {

    /**
     * Obtiene la similitud entre dos productos dados sus IDs.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return La similitud entre los dos productos, o null si no existe una similitud registrada.
     */
    Double getSimilarity(Integer id1, Integer id2);

    /**
     * Añade una nueva similitud entre dos productos, si no existe previamente.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param similarity Valor de la similitud entre los productos.
     * @return La similitud añadida, o null si ya existía una similitud registrada entre los productos.
     */
    Double addSimilarity(Integer id1, Integer id2, Double similarity);

    /**
     * Edita la similitud entre dos productos existentes.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param newSimilarity Nueva similitud que se establecerá.
     * @return La nueva similitud, o null si no existía una similitud registrada entre los productos.
     */
    Double editSimilarity(Integer id1, Integer id2, Double newSimilarity);

    /**
     * Elimina la similitud entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return La similitud eliminada, o null si no existía una similitud registrada entre los productos.
     */
    Double removeSimilarity(Integer id1, Integer id2);

    /**
     * Elimina todas las similitudes asociadas a un producto.
     *
     * @param id ID del producto cuyas similitudes serán eliminadas.
     */
    void removeSimilarities(Integer id);

    /**
     * Obtiene todas las similitudes asociadas a un producto específico.
     *
     * @param id ID del producto del cual se desean obtener las similitudes.
     * @return Lista de pares que contienen el ID del producto similar y la similitud asociada.
     */
    List<Pair<Integer, Double>> getSimilarities(Integer id);

    /**
     * Obtiene todas las similitudes almacenadas en el repositorio.
     *
     * @return Un mapa de similitudes, donde la clave es un par de IDs de productos,
     *         y el valor es la similitud entre ellos.
     */
    Map<Pair<Integer, Integer>, Double> getAll();

    /**
     * Limpia todas las similitudes almacenadas en el repositorio.
     */
    void clear();

    /**
     * Carga las similitudes desde un archivo JSON.
     */
    void loadFromJSON();

    /**
     * Guarda todas las similitudes actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo JSON donde se guardarán las similitudes.
     */
    void saveToJSON(String fileName);
}
