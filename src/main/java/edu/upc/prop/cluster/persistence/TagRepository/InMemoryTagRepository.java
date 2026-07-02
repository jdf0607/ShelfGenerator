package edu.upc.prop.cluster.persistence.TagRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.persistence.data.Tree;


/**
 * Repositorio en memoria para gestionar las tags.
 * <p>Esta clase implementa la interfaz {@link ITagRepository} y permite la gestión de tags en memoria, incluyendo la
 * adición, eliminación, búsqueda y almacenamiento de tags en formato JSON.</p>
 * <p>El repositorio utiliza un árbol de búsqueda para gestionar las tags de forma eficiente por nombre.</p>
 *
 * @author José Durán Foix
 */
public class InMemoryTagRepository implements ITagRepository {

    private Tree tree;
    private Map<String, Tag> tags; //name Tag para identificar/Tag
    private final String filePath = "main.shelfgenerator";

    /**
     * Constructor que inicializa el repositorio con un árbol de búsqueda.
     *
     * @param tree Árbol de búsqueda utilizado para gestionar las tags.
     */
    public InMemoryTagRepository(Tree tree) {
        this.tree = tree;
        this.tags = new HashMap<>();
        loadFromJSON();
    }


    /**
     * Constructor por defecto que inicializa el repositorio con un nuevo árbol de búsqueda.
     */
    public InMemoryTagRepository() {
        this.tree = new Tree();
        this.tags = new HashMap<>();
        loadFromJSON();
    }



    /**
     * Obtiene todas las tags que comienzan con el prefijo dado.
     *
     * @param prefix Prefijo de búsqueda.
     * @return Lista de tags cuyo nombre comienza con el prefijo dado.
     */
    public List<Tag> getPrefix (String prefix) {
        List<Tag> result = new ArrayList<>();
        List<String> tagNames = tree.searchTags(prefix);
        for (String tag : tagNames) {
            Tag tagObj = tags.get(tag);
            if (tagObj != null) result.add(tagObj);
        }
        return result;
    }


    /**
     * Obtiene la tag correspondiente al nombre dado.
     *
     * @param name Nombre de la tag.
     * @return La tag correspondiente al nombre dado, o null si no existe.
     */
    public Tag getTag (String name) {
        return tags.get(name);
    }


    /**
     * Añade una nueva tag al repositorio si no existe previamente.
     *
     * @param tag La tag a añadir.
     * @return La tag añadida, o null si ya existía una tag con el mismo nombre.
     */
    public Tag addTag(Tag tag) {
        //comprobar que Tag no existe
        String name = tag.getName();
        if (tree.searchTags(name).contains(name)) {
            return null;
        }
        //afegir el nom del tag al Tree i el tag al set
        tree.addTag(name);
        tags.put(name, tag);
        return tags.get(name);
    }


    /**
     * Elimina una tag del repositorio por su nombre.
     *
     * @param tagName Nombre de la tag a eliminar.
     * @return La tag eliminada, o null si no existía una tag con el nombre dado.
     */
    public Tag removeTag(String tagName) {
        Tag tag = getTag(tagName);
        if (tree.deleteTag(tagName)) {
            //eliminar del Tree
            tags.remove(tagName);
            return tag;
        }
        return null;
    }


    /**
     * Obtiene todas las tags almacenadas en el repositorio.
     *
     * @return Lista de todas las tags almacenadas.
     */
    public List<Tag> getAllTags() {
        return new ArrayList<>(tags.values());
    }


    /**
     * Obtiene el número de productos asociados a una tag.
     *
     * @param tag La tag de la cual obtener el número de productos asociados.
     * @return El número de productos asociados a la tag.
     */
    public int getProductCount(Tag tag) {
        return tag.getProductCount();
    }


    /**
     * Limpia todas las tags y el árbol de búsqueda en el repositorio.
     */
    public void clear() {
        tags.clear();
        this.tree = new Tree();
    }


    /**
     * Verifica si una tag está presente en el repositorio.
     *
     * @param tag La tag a verificar.
     * @return true si la tag está presente, false en caso contrario.
     */
    public boolean inTags (Tag tag) {
        return tags.containsValue(tag);
    }


    /**
     * Carga las tags desde un archivo JSON.
     */
    public void loadFromJSON () {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                JsonNode rootNode = mapper.readTree(file);
                JsonNode sectionNode = rootNode.get("tags");

                //leemos tags de JSON
                if(sectionNode != null) tags = mapper.convertValue(sectionNode, new TypeReference<Map<String,Tag>>() {});
                //añadimos los tags al Tree para la búsqueda rápida
                this.tree = new Tree();
                for (Map.Entry<String, Tag> item : tags.entrySet()) {
                    tree.addTag(item.getKey());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
        }
    }


    /**
     * Guarda las tags actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo donde se guardarán las tags.
     */
    public void saveToJSON(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(fileName);
            ObjectNode rootNode;

            if (file.exists()) { rootNode = (ObjectNode) mapper.readTree(file); }
            else { rootNode = mapper.createObjectNode(); }
            JsonNode sectionNode = mapper.valueToTree(tags);
            rootNode.set("tags", sectionNode);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
        }  catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON:" + e.getMessage());
        }
    }
}
