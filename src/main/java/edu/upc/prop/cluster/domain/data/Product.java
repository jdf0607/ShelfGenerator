package edu.upc.prop.cluster.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.upc.prop.cluster.common.Pair;

import java.util.*;

/**
 * La clase Product representa un producto con un ID, nombre y una lista de etiquetas.
 * Proporciona métodos para manipular y recuperar información sobre el producto.
 *
 * <p>Nota: El límite de etiquetas es una variable estática compartida entre todas las instancias de Product.</p>
 *
 * @author Alex Meca Moñino
 */
public class Product {

    private static int tag_limit = 10;

    private int id;
    private String name;
    private Map<String,Double> tags = new TreeMap<String,Double>();
    @JsonIgnore
    private Double maxWeight;

    /**
     * Establece un nuevo límite para el número de etiquetas que un producto puede tener.
     *
     * @param new_limit El nuevo límite de etiquetas.
     */
    public static void setTagLimit(int new_limit) {
        tag_limit = new_limit;
    }

    /**
     * Constructor por defecto.
     */
    public Product() {}


    /**
     * Construye un Producto con el nombre especificado.
     *
     * @param name El nombre del producto.
     */
    public Product(String name) {
        this.name = name;
        this.maxWeight = 0.;
    }


    /**
     * Construye un Producto con el nombre, etiquetas e ID especificados.
     *
     * @param name El nombre del producto.
     * @param tags Una lista de pares que representan las etiquetas y sus respectivos pesos.
     * @param id El ID único del producto.
     */
    public Product(String name, List<Pair<String,Double>> tags, int id) {
        this.name = name;
        this.maxWeight = 0.;
        this.id = id;
        for (Pair<String,Double> tag: tags) { addTag(tag.first(), tag.second()); }
    }


    /**
     * Obtiene el número máximo de etiquetas permitido para todas las instancias de Producto.
     *
     * @return El límite de etiquetas.
     */
    public static int MaxTagNumber() {
        return tag_limit;
    }


    /**
     * Obtiene el nombre del producto.
     *
     * @return El nombre del producto.
     */
    public String getName() {
        return name;
    }


    /**
     * Establece el nombre del producto.
     *
     * @param name El nombre que se asignará al producto.
     */
    public void setName(String name) { this.name = name;}


    /**
     * Obtiene el ID del producto.
     *
     * @return El ID del producto.
     */
    public int getId(){
        return id;
    }


    /**
     * Establece el ID del producto. Es necesario para la serialización en JSON.
     *
     * @param id El ID a asignar al producto.
     */
    public void setId(int id) {this.id = id;}


    /**
     * Añade una etiqueta al producto con la clave y el valor especificados.
     * Si no se ha alcanzado el límite de etiquetas, la etiqueta se añade al producto.
     *
     * @param key La clave de la etiqueta.
     * @param value El peso de la etiqueta.
     */
    public void addTag(String key, Double value) {
        if(!taglistIsFull()) {
            tags.put(key, value);
            if (value > maxWeight) maxWeight = value;
        }
    }


    /**
     * Establece las etiquetas del producto a partir de un mapa de etiquetas y pesos.
     *
     * @param tags Un mapa de nombres de etiquetas a sus respectivos pesos.
     */
    public void setTags(Map<String, Double> tags) {
        this.tags = tags;
        double max = -1;
        for(Double value: tags.values()) {
            if(value > max) {
                max = value;
            }
        }
        this.maxWeight = max;
    }


    /**
     * Establece las etiquetas del producto a partir de una lista de pares de etiqueta-peso.
     *
     * @param tags Una lista de pares etiqueta-peso.
     */
    public void setTagsList(List<Pair<String,Double>> tags) {
        clearTags();
        for (Pair<String, Double> tag : tags) addTag(tag.first(), tag.second());
    }


    /**
     * Obtiene el número de etiquetas asociadas con el producto.
     *
     * @return El número de etiquetas.
     */
    @JsonIgnore
    public int getTagCount() {
        return tags.size();
    }


    /**
     * Verifica si el producto ha alcanzado el número máximo de etiquetas.
     *
     * @return true si la lista de etiquetas está llena, false en caso contrario.
     */
    public Boolean taglistIsFull() {
        return tags.size() >= tag_limit;
    }


    /**
     * Verifica si el producto contiene una etiqueta específica.
     *
     * @param tag La etiqueta a verificar.
     * @return true si la etiqueta existe, false en caso contrario.
     */
    public Boolean hasTag(String tag) {
        return tags.containsKey(tag);
    }


    /**
     * Elimina una etiqueta del producto.
     *
     * @param tag La etiqueta a eliminar.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }


    /**
     * Elimina todas las etiquetas del producto y restablece el peso máximo.
     */
    public void clearTags() {
        tags.clear();
        maxWeight = 0.;
    }


    /**
     * Obtiene el peso de una etiqueta específica.
     *
     * @param tagName El nombre de la etiqueta.
     * @return El peso de la etiqueta, o null si la etiqueta no existe.
     */
    public Double getTagWeight(String tagName) {
        return tags.get(tagName);
    }


    /**
     * Obtiene todas las etiquetas asociadas con el producto.
     *
     * @return Un mapa de nombres de etiquetas a sus respectivos pesos.
     */
    public Map<String,Double> getTags() {
        return tags;
    }


    /**
     * Obtiene una lista de etiquetas asociadas con el producto, representadas como pares de nombres de etiqueta y sus respectivos pesos.
     *
     * @return Una lista de pares etiqueta-peso.
     */
    @JsonIgnore
    public List<Pair<String,Double>> getTagList() {
        List<Pair<String, Double>> tagList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : tags.entrySet())
            tagList.add(new Pair<>(entry.getKey(), entry.getValue()));

        return tagList;
    }


    /**
     * Obtiene el peso máximo de las etiquetas asociadas con el producto.
     *
     * @return El peso máximo de las etiquetas.
     */
    public Double getMaxWeight() { return maxWeight; }


    /**
     * Convierte el producto a una representación en cadena.
     *
     * @return Una representación en cadena del producto.
     */
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", tags=" + tags + "]";
    }


    /**
     * Cambia el peso de una etiqueta existente.
     *
     * @param tagName El nombre de la etiqueta a cambiar.
     * @param newWeight El nuevo peso para la etiqueta.
     */
    public void changeWeight(String tagName, Double newWeight) {
        removeTag(tagName);
        addTag(tagName, newWeight);
    }


    /**
     * Compara este producto con otro objeto para determinar si son iguales.
     *
     * @param obj El objeto a comparar.
     * @return true si los productos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        // Verificar si son el mismo objeto
        if (this == obj) return true;

        // Verificar si el objeto es nulo o no es del mismo tipo
        if (obj == null || getClass() != obj.getClass()) return false;

        // Hacer el casting
        Product product = (Product) obj;

        // Comparar los atributos relevantes
        return id == product.id &&
                Objects.equals(name, product.name) &&
                Objects.equals(tags, product.tags)  &&
                Objects.equals(maxWeight, product.maxWeight);
    }

}