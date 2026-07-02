package edu.upc.prop.cluster.persistence.AlgorithmRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación en memoria del repositorio de algoritmos.
 * Esta clase maneja el almacenamiento y la carga de los resultados de los algoritmos en memoria,
 * así como la serialización y deserialización de los mismos en formato JSON.
 *
 * <p>La clase mantiene una lista de resultados, representada como una lista de cadenas, y proporciona métodos
 * para manipular esos resultados, como obtener la estantería, guardar y cargar datos desde un archivo JSON.</p>
 *
 * @author José Durán Foix
 */
public class InMemoryAlgorithmRepository implements IAlgorithmRepository {
    private List<String> results;
    private final String filePath;//


    /**
     * Constructor que inicializa el repositorio con una lista vacía de resultados
     * y carga los datos desde el archivo JSON configurado.
     */
    public InMemoryAlgorithmRepository() {
        this.results = new ArrayList<String>();;
        this.filePath = "main.shelfgenerator";
        loadFromJSON();
    }


    /**
     * Obtiene la lista de resultados almacenados en el repositorio.
     *
     * @return Lista de resultados (etiquetas) almacenados en el repositorio.
     */
    public List<String> getShelf() {
        return results;
    }


    /**
     * Guarda la lista proporcionada de resultados (etiquetas) en el repositorio.
     *
     * @param shelf Lista de resultados (etiquetas) a guardar.
     */
    public void saveShelf(List<String> shelf) {
        this.results = shelf;
    }


    /**
     * Borra los resultados almacenados en el repositorio y devuelve la lista antigua de resultados.
     *
     * @return Lista de resultados eliminados.
     */
    public List<String> clear() {
        List<String> oldResults = results;
        results = new ArrayList<String>();
        return oldResults;
    }


    /**
     * Verifica si el repositorio contiene un resultado con el nombre especificado.
     *
     * @param name El nombre del resultado a buscar.
     * @return true si el nombre existe en los resultados, false en caso contrario.
     */
    public boolean inResult(String name) {
        return results.contains(name);
    }


    /**
     * Carga los resultados desde el archivo JSON especificado en {@link #filePath}.
     * Si el archivo existe y contiene datos, estos serán deserializados en la lista de resultados.
     */
    public void loadFromJSON() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File reader = new File(filePath);
            if (reader.exists()) {
                JsonNode rootNode = mapper.readTree(reader);
                JsonNode sectionNode = rootNode.get("shelf");
                if(sectionNode != null) results = mapper.convertValue(sectionNode, new TypeReference<List<String>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Guarda los resultados actuales en un archivo JSON con el nombre especificado.
     *
     * @param fileName El nombre del archivo donde se guardarán los resultados.
     */
    public void saveToJSON(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File writer = new File(fileName);
            ObjectNode rootNode;
            if (writer.exists()) { rootNode = (ObjectNode) mapper.readTree(writer); }
            else { rootNode = mapper.createObjectNode(); }
            JsonNode sectionNode = mapper.valueToTree(results);
            rootNode.set("shelf", sectionNode);
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, rootNode);
        } catch (IOException e) {
            System.err.println("Error escribint al fitxer: " + e.getMessage());
        }
    }
}
