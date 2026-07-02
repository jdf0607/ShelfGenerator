package edu.upc.prop.cluster.domain.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upc.prop.cluster.common.Pair;

import java.util.Objects;

/**
 * La clase ProductPair extiende la clase Pair y representa un par compuesto por un peso y un ID de producto.
 * Esta clase es utilizada para asociar un peso con un producto específico.
 *
 * <p>La clase proporciona métodos para serializar estos valores con nombres personalizados mediante Jackson.</p>
 *
 * @author Jorge Vico Lora
 */
public class ProductPair extends Pair<Double, Integer> { // weight, productID

    /**
     * Constructor para crear un ProductPair con los valores proporcionados.
     *
     * @param first El peso asociado con el producto.
     * @param second El ID del producto.
     */
    @JsonCreator
    public ProductPair(Double first, Integer second) {
        super(first, second);
    }


    /**
     * Obtiene el peso asociado al producto.
     * Este método es utilizado por Jackson para serializar el campo "peso" en JSON.
     *
     * @return El peso del producto.
     */
    @JsonProperty("peso")
    public Double getPeso() {
        return this.first();
    }


    /**
     * Obtiene el ID del producto.
     * Este método es utilizado por Jackson para serializar el campo "ID" en JSON.
     *
     * @return El ID del producto.
     */
    @JsonProperty("ID")
    public Integer getID() {
        return this.second();
    }


    /**
     * Compara este ProductPair con otro objeto para determinar si son iguales.
     * La comparación se hace en función del ID del producto (second).
     *
     * @param obj El objeto a comparar.
     * @return true si ambos objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductPair that = (ProductPair) obj;
        return this.second().equals(that.second());
    }


    /**
     * Genera un código hash basado en el ID del producto.
     *
     * @return El código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.second());
    }
}
