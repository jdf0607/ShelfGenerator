package edu.upc.prop.cluster.dto;


import edu.upc.prop.cluster.domain.data.ProductPair;

import java.util.Set;

/**
 * Representa un objeto de transferencia de datos (DTO) para una etiqueta (tag).
 * La clase contiene el nombre de la etiqueta y opcionalmente una lista de productos asociados a la etiqueta.
 *
 * @author Alex Meca Moñino
 */
public class TagDTO {
    private String name;
    //private List<String> products;


    /**
     * Constructor para crear un TagDTO con solo el nombre de la etiqueta.
     *
     * @param name Nombre de la etiqueta.
     */
    public TagDTO(String name) {
        this.name = name;
    }


    /**
     * Constructor para crear un TagDTO con el nombre de la etiqueta y un conjunto de productos asociados.
     *
     * @param name Nombre de la etiqueta.
     * @param products Conjunto de pares de productos asociados a la etiqueta.
     */
    public TagDTO(String name, Set<ProductPair> products) {
        this.name = name;
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
