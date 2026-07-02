package edu.upc.prop.cluster.persistence.TagRepository;

import edu.upc.prop.cluster.domain.data.Tag;

import java.util.List;

/**
 * Interfaz que define los métodos para gestionar las tags en un repositorio.
 * <p>Esta interfaz proporciona operaciones básicas para añadir, eliminar, buscar y recuperar tags,
 * así como la capacidad de cargar y guardar datos desde y hacia archivos JSON.</p>
 *
 * <p>Implementaciones de esta interfaz deben gestionar las tags de forma eficiente y permitir su
 * acceso y modificación de acuerdo con los requerimientos del sistema.</p>
 *
 * @author Jorge Vico Lora
 */
public interface ITagRepository {

    /**
     * Devuelve todas las tags cuyos nombres comienzan con el prefijo dado.
     *
     * @param prefix Prefijo a buscar en los nombres de las tags.
     * @return Lista de tags cuyos nombres comienzan con el prefijo dado.
     */
    List<Tag> getPrefix(String prefix);

    /**
     * Devuelve la tag correspondiente al nombre dado.
     *
     * @param name Nombre de la tag a buscar.
     * @return La tag correspondiente al nombre dado, o null si no existe.
     */
    Tag getTag(String name);

    /**
     * Añade una nueva tag al repositorio.
     *
     * @param tag La tag a añadir al repositorio.
     * @return La tag añadida, o null si ya existe una tag con el mismo nombre.
     */
    Tag addTag(Tag tag);

    /**
     * Elimina una tag del repositorio por su nombre.
     *
     * @param tag Nombre de la tag a eliminar.
     * @return La tag eliminada, o null si no existía una tag con el nombre dado.
     */
    Tag removeTag(String tag);

    /**
     * Devuelve todas las tags almacenadas en el repositorio.
     *
     * @return Lista de todas las tags almacenadas.
     */
    List<Tag> getAllTags();

    /**
     * Devuelve el número de productos asociados a la tag dada.
     *
     * @param tag La tag de la cual obtener el número de productos asociados.
     * @return El número de productos asociados a la tag.
     */
    int getProductCount(Tag tag);

    /**
     * Limpia todas las tags del repositorio.
     */
    void clear();

    /**
     * Verifica si la tag dada existe en el repositorio.
     *
     * @param tag La tag a verificar.
     * @return true si la tag está presente en el repositorio, false en caso contrario.
     */
    boolean inTags(Tag tag);

    /**
     * Carga las tags desde un archivo JSON.
     */
    void loadFromJSON();

    /**
     * Guarda las tags actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo donde se guardarán las tags.
     */
    void saveToJSON(String fileName);
}
