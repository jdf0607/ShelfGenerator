package edu.upc.prop.cluster.persistence.SimilarityRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.common.PairKeyDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Implementación en memoria de un repositorio de similitudes entre productos.
 * <p>Esta clase gestiona un mapa de similitudes entre pares de productos.
 * Proporciona métodos para obtener, añadir, editar, eliminar similitudes y manipular los datos
 * almacenados en memoria. Además, permite cargar y guardar las similitudes desde y hacia un archivo JSON.</p>
 *
 * @author Jorge Vico Lora
 */
public class InMemorySimilarityRepository implements ISimilarityRepository {
    private Map<Pair<Integer, Integer>, Double> similarities; // En el pair, el id menor va a la izquierda


    /**
     * Constructor que inicializa el repositorio en memoria y carga los datos desde el archivo JSON.
     */
    public InMemorySimilarityRepository() {
        similarities = new HashMap<>();
        loadFromJSON();
    }


    /**
     * Obtiene la similitud entre dos productos dados sus IDs.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return La similitud entre los dos productos, o null si no existe una similitud registrada.
     */
    public Double getSimilarity(Integer id1, Integer id2) {
        return similarities.get(new Pair<>(min(id1, id2), max(id1, id2)));
    }


    /**
     * Añade una nueva similitud entre dos productos, si no existe previamente.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param similarity Valor de la similitud entre los productos.
     * @return La similitud añadida, o null si ya existía una similitud registrada entre los productos.
     */
    public Double addSimilarity(Integer id1, Integer id2, Double similarity) {
        Pair<Integer, Integer> key = new Pair<>(min(id1, id2), max(id1, id2));
        if (similarities.containsKey(key)) return null;
        similarities.put(key, similarity);
        return similarities.get(key);
    }


    /**
     * Edita la similitud entre dos productos existentes.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @param newSimilarity Nueva similitud que se establecerá.
     * @return La nueva similitud, o null si no existía una similitud registrada entre los productos.
     */
    public Double editSimilarity(Integer id1, Integer id2, Double newSimilarity) {
        Pair<Integer, Integer> key = new Pair<>(min(id1, id2), max(id1, id2));
        if (!similarities.containsKey(key)) return null;
        similarities.replace(key, newSimilarity);
        return similarities.get(key);
    }


    /**
     * Elimina la similitud entre dos productos.
     *
     * @param id1 ID del primer producto.
     * @param id2 ID del segundo producto.
     * @return La similitud eliminada, o null si no existía una similitud registrada entre los productos.
     */
    public Double removeSimilarity(Integer id1, Integer id2) {
        Pair<Integer, Integer> key = new Pair<>(min(id1, id2), max(id1, id2));
        if (!similarities.containsKey(key)) return null;
        return similarities.remove(key);
    }


    /**
     * Elimina todas las similitudes asociadas a un producto.
     *
     * @param id ID del producto cuyas similitudes serán eliminadas.
     */
    public void removeSimilarities(Integer id) {
        Iterator<Map.Entry<Pair<Integer, Integer>, Double>> iterator = similarities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<Integer, Integer>, Double> entry = iterator.next();
            Pair<Integer, Integer> key = entry.getKey();
            if (key.first().equals(id) || key.second().equals(id)) {
                iterator.remove();
            }
        }
    }


    /**
     * Obtiene todas las similitudes asociadas a un producto específico.
     *
     * @param id ID del producto del cual se desean obtener las similitudes.
     * @return Lista de pares que contienen el ID del producto similar y la similitud asociada.
     */
    public List<Pair<Integer, Double>> getSimilarities(Integer id) {
        List<Pair<Integer, Double>> result = new ArrayList<>();
        for (Map.Entry<Pair<Integer, Integer>, Double> entry : similarities.entrySet()) {
            Pair<Integer, Integer> key = entry.getKey();
            if (key.first().equals(id))
                result.add(new Pair<>(key.second(), entry.getValue()));
            else if (key.second().equals(id))
                result.add(new Pair<>(key.first(), entry.getValue()));
        }
        return result;
    }


    /**
     * Obtiene todas las similitudes almacenadas en el repositorio.
     *
     * @return Un mapa de similitudes, donde la clave es un par de IDs de productos,
     *         y el valor es la similitud entre ellos.
     */
    public Map<Pair<Integer, Integer>, Double> getAll() {
        return similarities;
    }


    /**
     * Limpia todas las similitudes almacenadas en el repositorio.
     */
    public void clear() {
        similarities.clear();
    }


    /**
     * Carga las similitudes desde un archivo JSON.
     *
     * @see #saveToJSON(String fileName)
     */
    public void loadFromJSON() {
        ObjectMapper mapper = new ObjectMapper();

        // Registrar el deserializador para Pair como clave
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Pair.class, new PairKeyDeserializer());
        mapper.registerModule(module);
        try {
            File file = new File("main.shelfgenerator");
            if (file.exists()) {
                JsonNode rootNode = mapper.readTree(file);
                JsonNode sectionNode = rootNode.get("similarities");
                if(sectionNode != null) similarities = mapper.convertValue(sectionNode, new TypeReference<Map<Pair<Integer, Integer>, Double>>() {});
            } else {
                System.err.println("El archivo no existe: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
        }

    }


    /**
     * Guarda todas las similitudes actuales en un archivo JSON.
     *
     * @param fileName Nombre del archivo JSON donde se guardarán las similitudes.
     */
    public void saveToJSON(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(fileName);
            ObjectNode rootNode;
            if (file.exists()) { rootNode = (ObjectNode) mapper.readTree(file); }
            else { rootNode = mapper.createObjectNode(); }
            JsonNode sectionNode = mapper.valueToTree(similarities);
            rootNode.set("similarities", sectionNode);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
        }
    }
}