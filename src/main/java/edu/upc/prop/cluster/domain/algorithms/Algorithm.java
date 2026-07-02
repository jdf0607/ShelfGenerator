package edu.upc.prop.cluster.domain.algorithms;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.data.Product;

import java.util.*;

/**
 * Clase abstracta que define el comportamiento común para los algoritmos de agrupamiento.
 * Proporciona métodos para calcular similitudes entre productos y gestionar las similitudes personalizadas.
 *
 * @author Alejandro Ruiz Patón
 */
public abstract class Algorithm {
    /**
     * Mapa que guarda la similitud entre dos productos similitud entre 2 productos indetificados
     * con su identificador
     */
    protected Map<Pair<Integer,Integer>, Double> similitudes;
    /**
     * Mapa que guarda las similitudes fijadas entre dos productos,
     * permitiendo establecer similitudes previamente establecidas entre productos.
     */
    protected Map<Pair<Integer,Integer>, Double> similFijadas;

    /**
     * Constructor que inicializa las tablas de similitudes.
     */
    public Algorithm() {
        this.similitudes = new HashMap<>();
        this.similFijadas = new HashMap<>();
    }

    /**
     * Ejecuta el algoritmo de agrupamiento sobre una lista de productos.
     *
     * @param products la lista de productos sobre los que se ejecutará el algoritmo
     * @param tags un mapa que contiene las tags y su peso asociado
     * @param savedSimilarities las similitudes previamente guardadas entre productos
     * @return una lista de productos procesados según el algoritmo
     */
    public abstract List<Product> execute(List<Product> products, Map<String, Double> tags, Map<Pair<Integer,Integer>, Double> savedSimilarities);


    /*
     * Calcula los vectores de pesos normalizados para cada producto.
     * Los pesos son calculados con una combinación de TF-IDF.
     *
     * @param products la lista de productos
     * @param tags un mapa con las tags y sus pesos asociados
     * @return una lista de pares, donde el primer valor es el ID del producto y el segundo es el mapa con los pesos normalizados de sus tags
     */
    private List<Pair<Integer,Map<String,Double>>> calcularVectoresPesos(List<Product> products, Map<String, Double> tags) { // <ProductId, Map<tag, normalized weight>>
        List<Pair<Integer, Map<String, Double>>> result = new ArrayList<>();

        double nProducts = products.size(); // Total de productos
        // Map<String, Double>  idfcache = new HashMap<>();
        for (Product product : products) {  // Por cada producto se calcula el vector de pesos real que se tendrá en cuenta a la hora de calcular la similitud coseno
            int productId = product.getId();
            Map<String, Double> vectorPesos = product.getTags();
            Map<String, Double> vectorPesosPersonalizado = new TreeMap<>();

            double maxWeight = product.getMaxWeight();
            double pesoTotal = 0.0;

            for (Map.Entry<String, Double> entry : vectorPesos.entrySet()) {  // El peso de una tag para un producto es igual a tf*idf == (weight/maxWeight) * log2(nProducts/nProductsWithTag)
                double weight = entry.getValue();
                String tagName = entry.getKey();

                //Cálculo del peso final para la tag dentro del producto
                double tf = weight / maxWeight;
                double idf = Math.log((nProducts + 1) / tags.get(tagName)) / Math.log(2); // log2(nProducts/nProdsWithTag)
                //double idf = idfCache.computeIfAbsent(tagName, k -> Math.log(nProducts / tag.getProductCount()) /Math.log(2);
                double w = tf * idf;

                pesoTotal += w * w; //acumula la magnitud al cuadrado

                vectorPesosPersonalizado.put(tagName, w);    // Se añade el nuevo peso al vector
            }

            double pesoTotalFinal = Math.sqrt(pesoTotal);
            vectorPesosPersonalizado.forEach((key, value) -> vectorPesosPersonalizado.put(key, value / pesoTotalFinal));

            result.add(new Pair<>(productId, vectorPesosPersonalizado));    // Se añade la id del producto con su vector personalizado
        }

        return result;
    }

    /*
     * Calcula la similitud entre dos productos usando sus vectores de pesos normalizados.
     * La similitud es calculada usando la fórmula del coseno.
     *
     * @param p1 el vector de pesos del primer producto
     * @param p2 el vector de pesos del segundo producto
     * @return la similitud entre los dos productos
     */
    private double calcularSimilitudEntreProductos(Map<String, Double> p1, Map<String, Double> p2) {  // sim(d1,d2) = (d1 * d2) / (|d1| * |d2|)
        return p1.entrySet().stream()
                .filter(entry -> p2.containsKey(entry.getKey()))
                .mapToDouble(entry -> entry.getValue() * p2.get(entry.getKey()))
                .sum();
    }


    /**
     * Calcula y actualiza las similitudes entre todos los productos de la lista proporcionada.
     * Si una similitud ya está fijada, se utiliza ese valor en lugar de recalcularla.
     *
     * @param products la lista de productos para los que se calcularán las similitudes
     * @param tags un mapa con las tags y sus pesos asociados
     */
    protected void calcularSimilitudes(List<Product> products, Map<String, Double> tags) {

        similitudes.clear();
        List<Pair<Integer,Map<String,Double>>> vectoresPesos = calcularVectoresPesos(products, tags);

        for (int i = 0; i < vectoresPesos.size(); i++) {            //por cada combinación posible de productos calculamos su similitud coseno
            for (int j = i + 1; j < vectoresPesos.size(); j++) {

                Pair<Integer, Map<String, Double>> p1 = vectoresPesos.get(i);
                Pair<Integer, Map<String, Double>> p2 = vectoresPesos.get(j);

                int id1 = p1.first();
                int id2 = p2.first();

                Pair<Integer, Integer> idPair = new Pair<>(Math.min(id1, id2), Math.max(id1, id2)); // La id menor a la izquierda

                // Obtenemos la similitud de similFijadas o la calculamos si no existe
                Double similarity = similFijadas.get(idPair);
                if (similarity == null) {
                    similarity = calcularSimilitudEntreProductos(p1.second(), p2.second());
                }

                similitudes.put(idPair, similarity);    // Guardamos la similitud en el mapa de similitudes
            }
        }
    }

    /**
     * Obtiene la similitud entre dos productos a partir de la tabla de similitudes.
     *
     * @param p1 el primer producto
     * @param p2 el segundo producto
     * @return la similitud entre los dos productos
     */
    protected double obtenerSimilitud(Product p1, Product p2) {
        int id1 = p1.getId();
        int id2 = p2.getId();
        return similitudes.getOrDefault(new Pair<>(Math.min(id1, id2), Math.max(id1, id2)), 0.0);
    }

    /**
     * Calcula la suma de las similitudes entre productos si se colocan en una estantería circular.
     * La conexión circular es realizada entre el primer y último producto de la lista.
     *
     * @param distribucion la lista de productos a distribuir en la estantería
     * @return la suma total de similitudes entre productos en la estantería
     */
    protected double calcularSumaSimilitudes(List<Product> distribucion) {
        double sumaSilimiludes = 0.0;

        for (int i = 0; i < distribucion.size(); i++) {
            Product p1 = distribucion.get(i);
            Product p2 = distribucion.get((i + 1) % distribucion.size());   // Hacemos la conexión circular por si estamos en el último caso juntar el inicio y el final
            sumaSilimiludes += obtenerSimilitud(p1, p2);
        }
        return sumaSilimiludes;
    }

    /**
     * Establece una similitud personalizada entre dos productos.
     * El valor de la similitud debe estar en el rango [0,1].
     *
     * @param p1 el primer producto
     * @param p2 el segundo producto
     * @param w la similitud entre los dos productos (en el rango [0,1])
     */
    public void establecerSimilitud(Product p1, Product p2, Double w) {
        int id1 = p1.getId();
        int id2 = p2.getId();

        similFijadas.put(new Pair<>(Math.min(id1, id2), Math.max(id1, id2)), w);
    }

    public Map<Pair<Integer, Integer>, Double> getSimilitudes() {
        return similitudes;
    }
}