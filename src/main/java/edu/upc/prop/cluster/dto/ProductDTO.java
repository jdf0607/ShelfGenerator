package edu.upc.prop.cluster.dto;

import java.util.Map;
import java.util.TreeMap;

/**
 * Representa un producto con un nombre y un conjunto de etiquetas asociadas con sus respectivos valores.
 * La clase proporciona métodos para acceder a los datos del producto y una representación en cadena de texto.
 *
 * @author Alex Meca Moñino
 */
public class ProductDTO {

    private String name;
    private Map<String,Double> tags;


    /**
     * Constructor de la clase ProductDTO.
     *
     * @param name Nombre del producto.
     * @param tags Mapa que contiene las etiquetas del producto como claves y sus respectivos valores como valores.
     *             Si el mapa es nulo, se asignará null al atributo tags.
     */
    public ProductDTO(String name, Map<String,Double> tags) {
        this.name = name;
        this.tags = (tags != null) ? new TreeMap<>(tags) : null;    // Evita modificar el objeto sin usar funciones de la clase
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
     * Obtiene el mapa de etiquetas del producto.
     *
     * @return El mapa de etiquetas, donde la clave es el nombre de la etiqueta y el valor es su peso.
     */
    public Map<String, Double> getTags() {
        return tags;
    }


    /**
     * Devuelve una representación en cadena del producto en formato legible.
     *
     * @return Una cadena que describe el producto con su nombre y sus etiquetas.
     */
    public String toString() {
        return "Product [name=" + name + ", tags=" + tags + "]";
    }
}
