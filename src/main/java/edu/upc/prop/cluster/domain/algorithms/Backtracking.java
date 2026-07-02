package edu.upc.prop.cluster.domain.algorithms;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Algoritmo de backtracking para encontrar la mejor distribución de productos en una estantería circular,
 * maximizando la suma de las similitudes entre productos adyacentes.
 *
 * @author Alejandro Ruiz Patón
 */
public class Backtracking extends Algorithm {
    private List<Product> mejorDistribucion;
    private double mejorValor = 0.0;
    private List<Double> resultados = new ArrayList<>();

    public Backtracking() {
        super();
        mejorDistribucion = new ArrayList<>();
        mejorValor = 0.0;
    }


    /*
     * Algoritmo principal que busca la mejor distribución posible de los productos mediante un enfoque de fuerza bruta (backtracking).
     *
     * @param solucionParcial Una lista que contiene los productos seleccionados hasta el momento.
     * @param productos Lista de productos restantes por asignar en la distribución.
     * @param valor Valor acumulado de la similitud de los productos en la solución parcial.
     */
    private void backtrack(List<Product> solucionParcial, List<Product> productos, double valor) {
        if (productos.isEmpty()) {
            valor += obtenerSimilitud(solucionParcial.getFirst(), solucionParcial.getLast());
            if (valor >= mejorValor) {
                mejorValor = valor;
                resultados.add(valor);
                mejorDistribucion = new ArrayList<>(solucionParcial);
            }
        } else {
            for (int i = 0; i < productos.size(); i++) {
                Product product = productos.get(i);

                solucionParcial.add(product);   // Añado el producto a la solución parcial y lo elimino de productos disponibles
                productos.remove(i);

                double valorNuevo = valor + obtenerSimilitud(solucionParcial.get(solucionParcial.size() - 2), product);
                backtrack(solucionParcial, productos, valorNuevo);

                productos.add(i, product);
                solucionParcial.remove(solucionParcial.size() - 1);
            }
        }

    }


    /**
     * Este método ejecuta el algoritmo de backtracking para encontrar la distribución óptima de productos.
     * Utiliza las similitudes precalculadas entre productos.
     *
     * @param products Lista de productos a distribuir.
     * @param tags Mapa de tags y su respectivo peso.
     * @param savedSimilarities Similitudes entre productos ya precalculadas.
     * @return La mejor distribución de productos encontrada.
     */
    @Override
    public List<Product> execute(List<Product> products, Map<String, Double> tags, Map<Pair<Integer,Integer>, Double> savedSimilarities) {
        if (products.isEmpty())  return products;
        this.similFijadas = savedSimilarities;
        calcularSimilitudes(products, tags);
        List<Product> solucionParcial = new ArrayList<>();
        solucionParcial.add(products.getFirst());  // Fijamos el primer producto para aprovechar la propiedad de que la estantería es circular
        products.removeFirst();

        backtrack(solucionParcial, products, 0.0);
        return mejorDistribucion;
    }




    //Funciones para testear. NO DEBERIAN SER INVOCADAS NUNCA

    /**
     * - Método para testear, NO DEBERIA SER INVOCADO NUNCA -
     * Método de prueba que calcula el resultado (suma de similitudes) para una distribución de productos dada.
     *
     * @param products Lista de productos.
     * @param tags Mapa de tags y sus pesos.
     * @param savedSimilarities Similitudes entre productos.
     * @return La suma de las similitudes de la distribución de productos.
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

    /**
     * - Método para testear, NO DEBERIA SER INVOCADO NUNCA -
     * Método de prueba que devuelve todos los resultados obtenidos durante la ejecución del algoritmo.
     *
     * @param products Lista de productos.
     * @param tags Mapa de tags y sus pesos.
     * @param savedSimilarities Similitudes precalculadas.
     * @return Lista con todos los resultados de similitud obtenidos.
     */
    public List<Double> getAllResultadosForTest(List<Product> products, Map<String, Double> tags, Map<Pair<Integer,Integer>, Double> savedSimilarities) {
        execute(products, tags, savedSimilarities);
        return resultados;
    }
}

