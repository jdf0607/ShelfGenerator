package edu.upc.prop.cluster.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * La clase Tag representa una etiqueta asociada a productos. Cada etiqueta tiene un nombre
 * y un conjunto de productos, donde cada producto está representado por un par de valores
 * que contienen el peso y el ID del producto.
 *
 * <p>Esta clase permite agregar, eliminar y cambiar el peso de los productos asociados a una etiqueta.</p>
 *
 * @author Jorge Vico Lora
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {

    private String name;
    @JsonSerialize
    @JsonDeserialize
    private Set<Integer> products; // Almacena el <peso, productID>

    /**
     * Constructor por defecto que inicializa el conjunto de productos vacío.
     */
    public Tag(){
        this.products = new HashSet<>();
    }


    /**
     * Constructor que inicializa la etiqueta con un nombre específico.
     *
     * @param name El nombre de la etiqueta.
     */
    public Tag(String name) {
        this.name = name;
        this.products = new HashSet<>();
    }


    /**
     * Agrega un producto a la etiqueta con un peso y un ID de producto.
     *
     * @param weight El peso del producto.
     * @param productID El ID del producto.
     * @return true si el producto fue agregado correctamente, false si ya existía.
     */
    public boolean addProduct(Double weight, Integer productID) {
        return products.add(productID);
    }


    /**
     * Elimina un producto de la etiqueta mediante su ID.
     *
     * @param productID El ID del producto a eliminar.
     * @return true si el producto fue eliminado correctamente, false si no se encontraba.
     */
    public boolean deleteProduct(Integer productID) {
        return products.remove(productID);
    }


    /**
     * Cambia el peso de un producto asociado a la etiqueta.
     *
     * @param weight El nuevo peso del producto.
     * @param productID El ID del producto cuyo peso se quiere cambiar.
     * @return true si el peso fue cambiado correctamente, false si el producto no existía.
     */
    public boolean changeWeight(Double weight, Integer productID) {
        if (this.deleteProduct(productID))
            return this.addProduct(weight, productID);

        return false;
    }


    /**
     * Obtiene una lista de los IDs de los productos asociados a la etiqueta.
     *
     * @return Una lista de IDs de productos.
     */
    @JsonIgnore
    public List<Integer> getIDs() {
        return new ArrayList<>(products);
    }


    /**
     * Compara dos etiquetas para ver si son iguales. Dos etiquetas se consideran iguales
     * si tienen el mismo nombre.
     *
     * @param obj El objeto a comparar.
     * @return true si ambas etiquetas tienen el mismo nombre, false en caso contrario.
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return name.equals(tag.name);
    }


    /**
     * Obtiene el número de productos asociados a la etiqueta.
     *
     * @return El número de productos asociados.
     */
    @JsonIgnore
    public int getProductCount () {
        return products.size();
    }


    /**
     * Obtiene el nombre de la etiqueta.
     *
     * @return El nombre de la etiqueta.
     */
    public String getName() {
        return name;
    }
}