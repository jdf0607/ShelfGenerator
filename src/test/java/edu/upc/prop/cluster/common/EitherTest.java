package edu.upc.prop.cluster.common;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alejandro Ruiz Patón
 */
public class EitherTest {

    @Test
    public void testLeftValue() {
        Either<String, Integer> left = Either.left("l");

        String result = left.fold(
                Function.identity(),
                rightValue -> "Wrong"
        );

        assertEquals("l", result);
    }


    @Test
    public void testRightValue() {
        Either<String, Integer> right = Either.right(5);

        String result = right.fold(
                leftVaule -> "Wrong",
                rightValue -> "Right: " + rightValue
        );

        assertEquals("Right: 5", result);
    }


    @Test
    public void testLeftValue2() {
        Either<String, Integer> left = Either.left("l");

        Integer result = left.fold(
                leftValue -> leftValue.length(),
                rightValue -> -1
        );

        assertEquals(1, result);
    }


    @Test
    public void testRightValue2() {
        Either<String, Integer> right = Either.right(5);

        Integer result = right.fold(
                leftValue -> -1,
                rightValue -> rightValue
        );

        assertEquals(5, result);
    }


    @Test
    public void testLeftNull() {
        Either<String, Integer> left = Either.left(null);

        String result = left.fold(
                leftvalue -> leftvalue,
                rightValue -> "wrong"
        );

        assertNull(result);
    }


    @Test
    public void testRightNull() {
        Either<String, Integer> right = Either.right(null);

        String result = right.fold(
                leftValue -> "wrong",
                rightValue -> rightValue == null ? "right" : "wrong"
        );

        assertEquals("right", result);
    }

}
