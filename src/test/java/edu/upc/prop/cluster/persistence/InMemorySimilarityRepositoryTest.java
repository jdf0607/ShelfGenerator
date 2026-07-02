package edu.upc.prop.cluster.persistence;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.common.PairKeyDeserializer;
import edu.upc.prop.cluster.persistence.SimilarityRepository.ISimilarityRepository;
import edu.upc.prop.cluster.persistence.SimilarityRepository.InMemorySimilarityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jorge Vico Lora
 */
public class InMemorySimilarityRepositoryTest {
    ISimilarityRepository repository;

    @BeforeEach
    void setUp() {
        File file = new File("main.shelfgenerator");
        if (file.exists()) { file.delete(); }
        repository = new InMemorySimilarityRepository();
    }


    // Dado un repositorio vacío, puedo añadir una similitud
    @Test
    void shouldAddSimilarityToRepository() {
        Integer id1 = 1, id2 = 2;
        Double similarity = 0.9;

        Double addedSimilarity = repository.addSimilarity(id1, id2, similarity);

        assertNotNull(addedSimilarity);
        assertEquals(similarity, addedSimilarity);
        assertEquals(similarity, repository.getSimilarity(id1, id2));
    }

    // Dado un repositorio con una similitud, puedo buscarla
    @Test
    void shouldReturnSimilarityWhenExists() {
        Integer id1 = 1, id2 = 2;
        Double similarity = 0.9;

        repository.addSimilarity(id1, id2, similarity);
        Double foundSimilarity = repository.getSimilarity(id1, id2);

        assertNotNull(foundSimilarity);
        assertEquals(similarity, foundSimilarity);
    }

    // Dado un repositorio vacío, buscar una similitud no la encuentra
    @Test
    void shouldNotReturnSimilarityWhenNotExists() {
        Integer id1 = 1, id2 = 2;

        Double foundSimilarity = repository.getSimilarity(id1, id2);

        assertNull(foundSimilarity);
    }

    // Dado un repositorio con una similitud, puedo editarla
    @Test
    void shouldEditSimilarity() {
        Integer id1 = 1, id2 = 2;
        Double oldSimilarity = 0.8;
        Double newSimilarity = 0.95;
        repository.addSimilarity(id1, id2, oldSimilarity);

        Double updatedSimilarity = repository.editSimilarity(id1, id2, newSimilarity);

        assertNotNull(updatedSimilarity);
        assertEquals(newSimilarity, updatedSimilarity);
        assertEquals(newSimilarity, repository.getSimilarity(id1, id2));
    }

    // Dado un repositorio con una similitud, puedo eliminarla
    @Test
    void shouldRemoveSimilarity() {
        Integer id1 = 1, id2 = 2;
        Double similarity = 0.9;

        repository.addSimilarity(id1, id2, similarity);
        Double removedSimilarity = repository.removeSimilarity(id1, id2);

        assertNotNull(removedSimilarity);
        assertEquals(similarity, removedSimilarity);
        assertNull(repository.getSimilarity(id1, id2));
    }

    // Dado un repositorio vacío, intentar eliminar una similitud retorna null
    @Test
    void shouldReturnNullWhenRemovingNonexistentSimilarity() {
        Integer id1 = 1, id2 = 2;

        Double removedSimilarity = repository.removeSimilarity(id1, id2);

        assertNull(removedSimilarity);
    }

    // Dado un repositorio con varias similitudes, puedo eliminarlas todas asociadas a un ID
    @Test
    void shouldRemoveAllSimilaritiesForId() {
        repository.addSimilarity(1, 2, 0.9);
        repository.addSimilarity(1, 3, 0.8);
        repository.addSimilarity(2, 3, 0.85);

        repository.removeSimilarities(1);

        assertNull(repository.getSimilarity(1, 2));
        assertNull(repository.getSimilarity(1, 3));
        assertNotNull(repository.getSimilarity(2, 3));
    }

    // Dado un repositorio con varias similitudes, puedo obtenerlas todas para un ID
    @Test
    void shouldReturnSimilaritiesForId() {
        repository.addSimilarity(1, 2, 0.9);
        repository.addSimilarity(1, 3, 0.8);

        List<Pair<Integer, Double>> similarities = repository.getSimilarities(1);

        assertNotNull(similarities);
        assertEquals(2, similarities.size());
        assertTrue(similarities.contains(new Pair<>(2, 0.9)));
        assertTrue(similarities.contains(new Pair<>(3, 0.8)));
    }

    // Dado un repositorio vacío, pedir las similitudes para un ID retorna una lista vacía
    @Test
    void shouldReturnEmptyListForNonexistentId() {
        List<Pair<Integer, Double>> similarities = repository.getSimilarities(1);

        assertNotNull(similarities);
        assertTrue(similarities.isEmpty());
    }

    // Dado un repositorio con varias similitudes, puedo obtener todas las entradas
    @Test
    void shouldReturnAllSimilarities() {
        repository.addSimilarity(1, 2, 0.9);
        repository.addSimilarity(2, 3, 0.85);

        Map<Pair<Integer, Integer>, Double> allSimilarities = repository.getAll();

        assertNotNull(allSimilarities);
        assertEquals(2, allSimilarities.size());
        assertEquals(0.9, allSimilarities.get(new Pair<>(1, 2)));
        assertEquals(0.85, allSimilarities.get(new Pair<>(2, 3)));
    }

    // Dado un repositorio lleno, puedo vaciarlo
    @Test
    void shouldClearRepository() {
        repository.addSimilarity(1, 2, 0.9);
        repository.addSimilarity(2, 3, 0.85);

        repository.clear();

        assertTrue(repository.getAll().isEmpty());
    }

    @Test
    public void testSaveToJSON() throws IOException {
        repository.addSimilarity(1, 2, 0.85);
        repository.addSimilarity(2, 3, 0.90);
        repository.addSimilarity(1, 3, 0.75);

        repository.saveToJSON("main.shelfgenerator");

        File file = new File("main.shelfgenerator");
        assertTrue(file.exists(), "El archivo JSON debe existir después de guardar");

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Pair.class, new PairKeyDeserializer());
        mapper.registerModule(module);
        JsonNode rootNode = mapper.readTree(file);
        JsonNode sectionNode = rootNode.get("similarities");

        Map<Pair<Integer, Integer>, Double> similaritiesFromFile;
        similaritiesFromFile = mapper.convertValue(sectionNode, new TypeReference<Map<Pair<Integer, Integer>, Double>>() {});

        assertEquals(3, similaritiesFromFile.size(), "El mapa deserializado debe contener 3 elementos");
        assertEquals(0.85, similaritiesFromFile.get(new Pair<>(1, 2)), 0.01);
        assertEquals(0.90, similaritiesFromFile.get(new Pair<>(2, 3)), 0.01);
        assertEquals(0.75, similaritiesFromFile.get(new Pair<>(1, 3)), 0.01);
    }

    @Test
    public void testLoadFromJSON() throws IOException {
        repository.addSimilarity(1, 2, 0.85);
        repository.addSimilarity(2, 3, 0.90);
        repository.addSimilarity(1, 3, 0.75);
        repository.saveToJSON("main.shelfgenerator");

        repository.removeSimilarities(1);
        repository.removeSimilarities(2);

        repository.loadFromJSON();

        Map<Pair<Integer, Integer>, Double> similarities = repository.getAll();

        assertNotNull(similarities);
        assertEquals(3, similarities.size());
        assertTrue(similarities.containsKey(new Pair<>(1, 2)));
        assertTrue(similarities.containsKey(new Pair<>(2, 3)));
        assertTrue(similarities.containsKey(new Pair<>(1, 3)));
    }

    @AfterEach
    public void tearDown() {
        // Borrar el archivo de pruebas después de cada test
        File file = new File("main.shelfgenerator");
        if (file.exists()) {
            file.delete();
        }
    }
}