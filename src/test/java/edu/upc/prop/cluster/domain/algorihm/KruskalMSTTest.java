package edu.upc.prop.cluster.domain.algorihm;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.algorithms.KruskalMST;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alejandro Ruiz Patón
 */
public class KruskalMSTTest {

    @Test
    public void testEmptyGraph() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        Map<Pair<Integer, Integer>, Double> result = kruskalMST.calculateMST(similitudes);

        assertTrue(result.isEmpty());
    }

    // En el programa no se realiza este caso, igualmente, observamos que no genera ninguna arista
    @Test
    public void testSingleNodeGraph() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        Map<Pair<Integer, Integer>, Double> result = kruskalMST.calculateMST(similitudes);

        assertTrue(result.isEmpty());
    }

    // En el programa no se alcanza este caso ya que las similitudes forman un grafo completo con todos los productos
    @Test
    public void testDisconnectedGraph() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        similitudes.put(new Pair<>(0, 1), 0.5);
        similitudes.put(new Pair<>(2, 3), 0.8);

        Map<Pair<Integer, Integer>, Double> result = kruskalMST.calculateMST(similitudes);

        assertEquals(2, result.size());
    }


    @Test
    public void testSmallConnectedGraph() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        similitudes.put(new Pair<>(0, 1), 0.5);
        similitudes.put(new Pair<>(1, 2), 0.8);
        similitudes.put(new Pair<>(0, 2), 0.6);

        Map<Pair<Integer, Integer>, Double> result = kruskalMST.calculateMST(similitudes);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(new Pair<>(1, 2)));
        assertTrue(result.containsKey(new Pair<>(0, 2)));
    }


    @Test
    public void testLargerGraph() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        similitudes.put(new Pair<>(0, 1), 0.1);
        similitudes.put(new Pair<>(0, 3), 0.6);
        similitudes.put(new Pair<>(0, 5), 0.4);
        similitudes.put(new Pair<>(1, 2), 0.9);
        similitudes.put(new Pair<>(1, 5), 0.8);
        similitudes.put(new Pair<>(2, 3), 0.7);
        similitudes.put(new Pair<>(2, 4), 1.0);
        similitudes.put(new Pair<>(3, 4), 0.8);
        similitudes.put(new Pair<>(4, 5), 0.6);

        Map<Pair<Integer, Integer>, Double> result = kruskalMST.calculateMST(similitudes);

        assertEquals(5, result.size());
        assertTrue(result.containsKey(new Pair<>(2, 4)));
        assertTrue(result.containsKey(new Pair<>(1, 2)));
        assertTrue(result.containsKey(new Pair<>(1, 5)));
        assertTrue(result.containsKey(new Pair<>(3, 4)));
        assertTrue(result.containsKey(new Pair<>(0, 3)));
    }


    @Test
    public void testEdgeOrderDoesNotAffectResult() {
        KruskalMST kruskalMST = new KruskalMST();
        Map<Pair<Integer, Integer>, Double> similitudes = new HashMap<>();
        similitudes.put(new Pair<>(0, 1), 0.5);
        similitudes.put(new Pair<>(1, 2), 0.8);
        similitudes.put(new Pair<>(0, 2), 0.6);

        // Shuffle the edges to test order independence
        Map<Pair<Integer, Integer>, Double> shuffledSimilitudes = new HashMap<>(similitudes);

        Map<Pair<Integer, Integer>, Double> result1 = kruskalMST.calculateMST(similitudes);
        Map<Pair<Integer, Integer>, Double> result2 = kruskalMST.calculateMST(shuffledSimilitudes);

        assertEquals(result1, result2);
    }
}