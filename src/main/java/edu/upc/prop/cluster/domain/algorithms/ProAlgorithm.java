package edu.upc.prop.cluster.domain.algorithms;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;

import java.util.*;


/**
 * Algoritmo de aproximación basado en Kruskal para resolver el problema de ciclo hamiltoniano
 * en un grafo ponderado de productos, utilizando técnicas de búsqueda local (hill climbing).
 *
 * El algoritmo busca aproximar un ciclo hamiltoniano en el grafo de similitudes entre productos,
 * utilizando el Árbol de Expansión Mínima (MST) y luego optimizando la solución con un enfoque de
 * búsqueda local para mejorar el ciclo encontrado.
 *
 * @author Alejandro Ruiz Patón
 */
public class ProAlgorithm extends Algorithm {
    private KruskalMST kruskalMST;
    private Map<Integer, List<Pair<Integer, Double>>> grafoAprox;

    /**
     * Constructor de la clase {@code ProAlgorithm}.
     * Inicializa el algoritmo de Kruskal y la estructura de grafo aproximado.
     */
    public ProAlgorithm() {
        super();
        kruskalMST = new KruskalMST();
        grafoAprox = new HashMap<>();
    }

    /*
     * Realiza una búsqueda en profundidad (DFS) sobre el grafo para recorrer sus nodos.
     *
     * @param nodoID El nodo actual.
     * @param visited Conjunto de nodos ya visitados.
     * @param orden Lista de nodos visitados en el orden del recorrido.
     */
    private void dfs(int nodoID, Set<Integer> visited, List<Integer> orden) {
        visited.add(nodoID);
        orden.add(nodoID);

        for (Pair<Integer, Double> adyacente : grafoAprox.get(nodoID)) {

            if (visited.size() == grafoAprox.size()) return;    // Ya se ha recorrido el grafo

            int adyacenteID = adyacente.first();

            if (!visited.contains(adyacenteID)) {
                dfs(adyacenteID, visited, orden);
            }
        }
    }

    // Habría que mejorar la búsqueda del ciclo hamiltoniano para encontrar una mejor solución que la primera obtenida desde el nodo inicial
    /*
     * Busca un ciclo hamiltoniano en el grafo de productos. Utiliza DFS para generar un ciclo
     * de recorrido.
     *
     * @return Una lista de nodos representando el ciclo hamiltoniano.
     */
    private List<Integer> buscarCicloHamiltoniano() {
        List<Integer> ciclo = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        // Obtener la clave del primer elemento (Esta forma es muy mejorable a que partimos siempre desde el primero
        Map.Entry<Integer, List<Pair<Integer, Double>>> first = grafoAprox.entrySet().iterator().next();
        dfs(first.getKey(), visited, ciclo);
        return ciclo;
    }

    /*
     * Busca un producto por su ID en la lista de productos.
     * Utiliza una búsqueda binaria para encontrar el producto rápidamente.
     *
     * @param products Lista de productos ordenados por ID.
     * @param id El ID del producto a buscar.
     * @return El producto con el ID correspondiente, o {@code null} si no se encuentra.
     */
    private Product buscarProductoPorId(List<Product> products, int id) {
        int l = 0;
        int r = products.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;
            Product product = products.get(m);

            if (product.getId() == id) {    // elemento encontrado
                return product;
            }
            else if (product.getId() > id) {    // búsqueda por la derecha
                r = m - 1;
            }
            else if (product.getId() < id) {    // búsqueda por la derecha
                l = m + 1;
            }
        }
        return null;
    }

    /*
     * Calcula la suma de similitudes en el ciclo dado.
     *
     * @param orden Lista de nodos representando el ciclo.
     * @return La suma de las similitudes de las aristas en el ciclo.
     */
    private double calcularSuma(List<Integer> orden) {
        double suma = 0.0;
        for (int i = 0; i < orden.size(); i++) {
            int p1 = orden.get(i);
            int p2 = orden.get((i + 1) % orden.size());

            // Recorro la lista de adyacencias
            Pair<Integer, Integer> pair = new Pair<>(Math.min(p1,p2), Math.max(p1,p2));
            suma += similitudes.getOrDefault(pair, 0.0);
        }
        return suma;
    }

    /*
     * Optimiza el ciclo hamiltoniano encontrado utilizando un enfoque de búsqueda local (hill climbing).
     * Intenta mejorar la suma de similitudes intercambiando pares de nodos en el ciclo.
     *
     * @param orden Lista de nodos representando el ciclo.
     * @return El ciclo optimizado.
     */
    private List<Integer> hillClimbing(List<Integer> orden) {
        List<Integer> mejorOrden = new ArrayList<>(orden);
        double mejorSuma = calcularSuma(mejorOrden);

        boolean fin = false;
        while (!fin) {
            fin = true;

            for (int i = 0; i < mejorOrden.size(); i++) {
                for (int j = i + 1; j < mejorOrden.size(); j++) {

                    int nodoAntesI = mejorOrden.get((i - 1 + mejorOrden.size()) % mejorOrden.size());
                    int nodoI = mejorOrden.get(i);
                    int nodoJ = mejorOrden.get(j);
                    int nodoDespJ = mejorOrden.get((j + 1) % mejorOrden.size());

                    // Pares de similitudes actuales
                    Pair<Integer, Integer> parIzqAnt = new Pair<>(Math.min(nodoAntesI, nodoI), Math.max(nodoAntesI, nodoI));
                    Pair<Integer, Integer> parDerAnt = new Pair<>(Math.min(nodoJ, nodoDespJ), Math.max(nodoJ, nodoDespJ));

                    // Pares de similitudes nuevas
                    Pair<Integer, Integer> parIzqNuevo = new Pair<>(Math.min(nodoAntesI, nodoJ), Math.max(nodoAntesI, nodoJ));
                    Pair<Integer, Integer> parDerNuevo = new Pair<>(Math.min(nodoI, nodoDespJ), Math.max(nodoI, nodoDespJ));

                    double sumaLatAnterior = similitudes.getOrDefault(parIzqAnt, 0.0) + similitudes.getOrDefault(parDerAnt, 0.0);
                    double sumaLatNueva = similitudes.getOrDefault(parIzqNuevo, 0.0) + similitudes.getOrDefault(parDerNuevo, 0.0);

                    if (sumaLatNueva > sumaLatAnterior) {
                        Collections.reverse(mejorOrden.subList(i, j + 1));
                        mejorSuma += sumaLatNueva - sumaLatAnterior;
                        fin = false;
                    }
                }
            }
        }

        return mejorOrden;
    }


    /**
     * Ejecuta el algoritmo de aproximación para obtener una ordenación de productos.
     * Utiliza el algoritmo de Kruskal para calcular el MST, seguido de una búsqueda DFS
     * para obtener un ciclo hamiltoniano aproximado.
     *
     * @param products Lista de productos.
     * @param tags Mapa de etiquetas y sus pesos.
     * @param savedSimilarities Mapa de similitudes previamente calculadas.
     * @return La lista de productos ordenados.
     */
    @Override
    public List<Product> execute(List<Product> products, Map<String, Double> tags, Map<Pair<Integer,Integer>, Double> savedSimilarities) { // Los productos están ordenados por id ascendente
        if (products.isEmpty() || products.size() == 1)  return products;

        this.similFijadas = savedSimilarities;
        calcularSimilitudes(products, tags);

        // Construcción del MST que maximiza la suma
        Map<Pair<Integer,Integer>, Double> mst = kruskalMST.calculateMST(similitudes);

        // Modificación del MST para obtener ambos sentidos de la arista
        grafoAprox.clear();
        for (Map.Entry<Pair<Integer,Integer>, Double> entry : mst.entrySet()) {
            Pair<Integer,Integer> pair = entry.getKey();
            int id1 = pair.first();
            int id2 = pair.second();
            double similarity = entry.getValue();

            // grafoAprox guarda la arista en ambos sentidos
            grafoAprox.computeIfAbsent(id1, k -> new ArrayList<>()).add(new Pair<>(id2, similarity));
            grafoAprox.computeIfAbsent(id2, k -> new ArrayList<>()).add(new Pair<>(id1, similarity));
        }

        // Recorrido DFS para encontrar un ciclo hamiltoniano que aproxime
        List<Integer> orden = buscarCicloHamiltoniano();
        orden = hillClimbing(orden);

        // Busco cada producto por su id
        List<Product> result = new ArrayList<Product>();
        for (Integer id : orden) {
            Product product = buscarProductoPorId(products, id);
            result.add(product);

        }
        return result;
    }




    //Funciones para testear. NO DEBERIAN SER INVOCADAS NUNCA

    /**
     * - Método para testear, NO DEBERIA SER INVOCADO NUNCA -
     * Método de prueba para obtener el resultado del algoritmo basado en un ciclo hamiltoniano.
     *
     * @param products Lista de productos.
     * @param tags Mapa de etiquetas y sus pesos.
     * @param savedSimilarities Mapa de similitudes previamente calculadas.
     * @return El valor de similitud total para el ciclo hamiltoniano encontrado.
     */
    public Double getResultadoForTest(List<Product> products, Map<String, Double> tags, Map<Pair<Integer,Integer>, Double> savedSimilarities) {
        if (products.isEmpty() || products.size() == 1) return 0.0;
        List<Product> p = execute(products, tags, savedSimilarities);
        double resultado = 0.0;
        for (int i = 0; i < p.size() - 1; i++) {
            resultado += obtenerSimilitud(p.get(i), p.get(i+1));
        }
        resultado += obtenerSimilitud(p.getFirst(), p.getLast());
        return resultado;
    }
}
