package edu.upc.prop.cluster.common;

import java.util.Objects;

/**
 * Representa un par de elementos de tipo genérico {@code T1} y {@code T2}.
 * Esta clase proporciona métodos para acceder, modificar y comparar los dos elementos del par.
 *
 * @param <T1> el tipo del primer elemento
 * @param <T2> el tipo del segundo elemento
 * @author Jorge Vico Lora
 */
public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    /**
     * Crea una nueva instancia de {@code Pair} con los valores especificados.
     *
     * @param first el primer valor del par
     * @param second el segundo valor del par
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Obtiene el primer valor del par.
     *
     * @return el primer valor del par
     */
    public T1 first() {
        return first;
    }


    /**
     * Obtiene el segundo valor del par.
     *
     * @return el segundo valor del par
     */
    public T2 second() {
        return second;
    }


    /**
     * Establece el primer valor del par.
     *
     * @param first el nuevo valor del primer elemento
     */
    public void setFirst(T1 first) {
        this.first = first;
    }


    /**
     * Establece el segundo valor del par.
     *
     * @param second el nuevo valor del segundo elemento
     */
    public void setSecond(T2 second) {
        this.second = second;
    }


    /**
     * Compara este {@code Pair} con otro objeto para determinar si son iguales.
     * Dos pares son iguales si ambos tienen los mismos valores para los elementos {@code first} y {@code second}.
     *
     * @param obj el objeto con el que se compara
     * @return {@code true} si los pares son iguales, {@code false} en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }


    /**
     * Calcula el código hash del {@code Pair}. El código hash se calcula en base a los valores {@code first} y {@code second}.
     *
     * @return el código hash del {@code Pair}
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }


    /**
     * Devuelve una representación en cadena del {@code Pair} en el formato {@code {first, second}}.
     *
     * @return la cadena que representa el {@code Pair}
     */
    @Override
    public String toString() {return "{" + first + ", " + second + '}';}
}