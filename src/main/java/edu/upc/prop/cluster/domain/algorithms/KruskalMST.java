package edu.upc.prop.cluster.domain.algorithms;


import edu.upc.prop.cluster.common.Pair;

import java.util.*;


/**
 * Implementación del algoritmo de Kruskal para encontrar el Árbol de Expansión Mínima (MST) en un grafo ponderado.
 * El grafo está representado por un conjunto de aristas entre productos, donde las aristas tienen un valor de similitud asociado.
 *
 * <p>El algoritmo de Kruskal selecciona las aristas de mayor peso (similitud) y las agrega al árbol, asegurándose de no formar ciclos,
 * utilizando la estructura de datos Union-Find para gestionar la unión de componentes disjuntos.</p>
 *
 * @author Alejandro Ruiz Patón
 */
public class KruskalMST {

    /**
     * Constructor vacío de la clase {@code KruskalMST}.
     */
    public KruskalMST() {}

    /*
     * Implementación de la clase {@code UnionFind}, que utiliza el algoritmo de unión por rangos
     * y compresión de caminos para gestionar la unión y la búsqueda de componentes disjuntos.
     */
    static class UnionFind {
        private int[] parent;

        /**
         * Constructor de la clase {@code UnionFind} que inicializa el array de padres.
         * Cada nodo es su propio padre inicialmente.
         *
         * @param size El número total de nodos (productos) en el grafo.
         */
        public UnionFind(int size) {
            parent = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        /**
         * Método para encontrar el representante o líder del conjunto al que pertenece un nodo
         * utilizando compresión de caminos para optimizar futuras búsquedas.
         *
         * @param x Nodo cuyo conjunto se desea encontrar.
         * @return El líder o representante del conjunto al que pertenece el nodo.
         */
        private int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        /**
         * Realiza la unión de dos nodos. Si los nodos pertenecen a conjuntos diferentes, los fusiona.
         *
         * @param x Nodo 1.
         * @param y Nodo 2.
         * @return true si los nodos fueron unidos, false si ya estaban en el mismo conjunto.
         */
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                parent[rootX] = rootY;
                return true;
            }
            return false;
        }
    }

    /**
     * Algoritmo de Kruskal para calcular el Árbol de Expansión Mínima (MST) en un grafo de similitudes entre productos.
     *
     * <p>El algoritmo selecciona las aristas con la mayor similitud entre productos y las agrega al MST sin formar ciclos,
     * utilizando la estructura de datos Union-Find.</p>
     *
     * @param similitudes Un mapa de aristas representadas como pares de productos y sus similitudes.
     * @return Un mapa de aristas seleccionadas en el MST con sus respectivas similitudes.
     */
    public Map<Pair<Integer,Integer>, Double> calculateMST(Map<Pair<Integer,Integer>, Double> similitudes) {
        // Crear un mapeo de índices originales a índices contiguos
        Map<Integer, Integer> indexMap = new HashMap<>();
        int currentIndex = 0;

        for (Pair<Integer, Integer> pair : similitudes.keySet()) {
            if (!indexMap.containsKey(pair.first())) {
                indexMap.put(pair.first(), currentIndex++);
            }
            if (!indexMap.containsKey(pair.second())) {
                indexMap.put(pair.second(), currentIndex++);
            }
        }

        int numProducts = indexMap.size(); // El número total de nodos únicos

        // Crear el mapeo inverso para restaurar índices originales en el resultado
        Map<Integer, Integer> reverseIndexMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : indexMap.entrySet()) {
            reverseIndexMap.put(entry.getValue(), entry.getKey());
        }

        // Convertir las aristas a índices contiguos
        List<Map.Entry<Pair<Integer, Integer>, Double>> sortedEdges = new ArrayList<>(similitudes.entrySet());
        sortedEdges.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Inicialización del UnionFind y el grafo resultado
        UnionFind uf = new UnionFind(numProducts);
        Map<Pair<Integer, Integer>, Double> result = new HashMap<>();

        // Aplicación de Kruskal
        for (Map.Entry<Pair<Integer, Integer>, Double> edge : sortedEdges) {
            Pair<Integer, Integer> originalPair = edge.getKey();
            int id1 = indexMap.get(originalPair.first());
            int id2 = indexMap.get(originalPair.second());
            double similarity = edge.getValue();

            if (uf.union(id1, id2)) { // Si se encuentran en estructuras distintas
                // Restaurar los índices originales al guardar el resultado
                Pair<Integer, Integer> resultPair = new Pair<>(reverseIndexMap.get(id1), reverseIndexMap.get(id2));
                result.put(resultPair, similarity);

                // El número de aristas de un árbol mínimo es |V| - 1
                if (result.size() == numProducts - 1) break;
            }
        }

        return result;
    }
}