package edu.upc.prop.cluster.common;

import java.util.function.Function;


/**
 * Representa un tipo genérico Either que puede contener un valor de tipo {@code L} (izquierda) o
 * {@code R} (derecha), pero no ambos a la vez. Esto es útil para modelar resultados que pueden ser
 * de dos tipos, como un éxito o un error.
 *
 * @param <L> el tipo del valor izquierdo
 * @param <R> el tipo del valor derecho
 * @author Jorge Vico Lora
 */
public abstract class Either<L, R> {

    /**
     * Crea una instancia de {@code Either} que contiene un valor izquierdo.
     *
     * @param value el valor izquierdo
     * @param <L>   el tipo del valor izquierdo
     * @param <R>   el tipo del valor derecho
     * @return una instancia de {@code Either} con el valor izquierdo
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }


    /**
     * Crea una instancia de {@code Either} que contiene un valor derecho.
     *
     * @param value el valor derecho
     * @param <L>   el tipo del valor izquierdo
     * @param <R>   el tipo del valor derecho
     * @return una instancia de {@code Either} con el valor derecho
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }


    /**
     * Aplica una de las dos funciones según si este objeto contiene un valor izquierdo o derecho.
     *
     * @param leftFunction  la función a aplicar si contiene un valor izquierdo
     * @param rightFunction la función a aplicar si contiene un valor derecho
     * @param <T>           el tipo de retorno de las funciones
     * @return el resultado de aplicar la función correspondiente
     */
    public abstract <T> T fold(Function<L, T> leftFunction, Function<R, T> rightFunction);

    /*
     * Clase interna para representar el caso de un valor izquierdo.
     */
    private static class Left<L, R> extends Either<L, R> {
        private final L value;

        public Left(L value) { this.value = value; }

        @Override
        public <T> T fold(Function<L, T> leftFunction, Function<R, T> rightFunction) {
            return leftFunction.apply(value);
        }
    }
    /*
     * Clase interna para representar el caso de un valor derecho.
     */
    private static class Right<L, R> extends Either<L, R> {
        private final R value;

        public Right(R value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<L, T> leftFunction, Function<R, T> rightFunction) {
            return rightFunction.apply(value);
        }
    }
}