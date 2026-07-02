package edu.upc.prop.cluster.domain.algorihm;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.algorithms.Backtracking;
import edu.upc.prop.cluster.domain.algorithms.ProAlgorithm;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.domain.data.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for comparing the performance and results of ProAlgorithm and Backtracking algorithms.
 *
 * @Author Alex Meca Moñino & Alejandro Ruiz Patón
 */

public class CompareAlgorithmTest {
    ProAlgorithm proAlgorithm;
    Backtracking backtracking;
    @Mock
    private Product p1,p2,p3,p4,p5,p6,p7,p8,p9,p10;
    @Mock
    private Tag t1,t2,t3,t4,t5,t6,t7,t8,t9,t10;
    Map<String,Double> tags;
    List<Product> products;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        products = new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
        tags = Map.of("test1",3.,
                "test2",4.,
                "test3", 5.,
                "test4", 4.,
                "test5", 4. ,
                "test6", 4. ,
                "test7", 4. ,
                "test8",3.,
                "test9", 2.,
                "test10", 3.);

        List<Integer> productIDs = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        List<String> productNames = new ArrayList<>(Arrays.asList("product1", "product2", "product3", "product4", "product5", "product6", "product7", "product8", "product9", "product10"));
        List<Map<String,Double>> productTags = new ArrayList<>(Arrays.asList(
                Map.of(
                        "test1", 45.7,
                        "test3", 0.3,
                        "test5", 130.5
                ),  Map.of(
                        "test1", 200.0,
                        "test2", 34.6,
                        "test5", 0.9
                ), Map.of(
                        "test2", 1.2,
                        "test4", 65.8,
                        "test6", 199.9
                ), Map.of(
                        "test3", 17.4,
                        "test7", 89.6,
                        "test8", 145.3,
                        "test10", 10.1
                ), Map.of(
                        "test2", 5.5,
                        "test4", 199.0,
                        "test6", 90.2,
                        "test9", 13.3
                ), Map.of(
                        "test3", 27.8,
                        "test5", 110.2,
                        "test7", 3.6
                ), Map.of(
                        "test6", 18.3,
                        "test8", 50.1,
                        "test10", 200.0
                ),Map.of(
                        "test1", 75.4,
                        "test4", 22.2,
                        "test5", 0.5,
                        "test7", 60.0,
                        "test9", 100.1
                ), Map.of(
                        "test2", 88.8,
                        "test3", 7.1,
                        "test6", 15.9,
                        "test8", 134.5
                ), Map.of(
                        "test3", 14.6,
                        "test4", 1.0,
                        "test7", 76.3,
                        "test10", 45.2
                )
        ));
        List<Double> productMaxWeights = new ArrayList<>(Arrays.asList(130.5,199.9,145.3,200.0,199.0,110.2,200.0,100.1,134.5,76.3));
        for (int i = 0; i < 10; i++) {
            when(products.get(i).getId()).thenReturn(productIDs.get(i));
            when(products.get(i).getName()).thenReturn(productNames.get(i));
            when(products.get(i).getTags()).thenReturn(productTags.get(i));
            when(products.get(i).getMaxWeight()).thenReturn(productMaxWeights.get(i));
        }
        proAlgorithm = new ProAlgorithm();
        backtracking = new Backtracking();
    }

    @Test
    public void testExecute() {
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        double resultadoProAlgorithm = proAlgorithm.getResultadoForTest(products,tags, setSimilarities);
        double resultadoBacktracking = backtracking.getResultadoForTest(products,tags, setSimilarities);
        assertEquals(resultadoProAlgorithm, resultadoBacktracking, resultadoBacktracking/2,"Solucion incorrecta");
    }


    @Test
    public void testExecuteTwoProducts() {
        List<Product> twoProduct = new ArrayList<>(Arrays.asList(p1, p3));
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        double resultadoProAlgorithm = proAlgorithm.getResultadoForTest(twoProduct,tags, setSimilarities);
        double resultadoBacktracking = backtracking.getResultadoForTest(twoProduct,tags, setSimilarities);
        assertEquals(resultadoProAlgorithm, resultadoBacktracking, resultadoBacktracking/2,"Solucion incorrecta");
    }


    @Test
    public void testExecuteWithFixedSimilarities() {
        Map<Pair<Integer,Integer>,Double> setSimilarities = new HashMap<>();
        setSimilarities.put(new Pair<>(1,9), 1000.);
        setSimilarities.put(new Pair<>(2,9), 5000.);
        setSimilarities.put(new Pair<>(2,8), 7000.);
        setSimilarities.put(new Pair<>(1,2), 7000.);
        double resultadoProAlgorithm = proAlgorithm.getResultadoForTest(products,tags, setSimilarities);
        double resultadoBacktracking = backtracking.getResultadoForTest(products,tags, setSimilarities);
        assertEquals(resultadoProAlgorithm, resultadoBacktracking, resultadoBacktracking/2,"Solucion incorrecta");
    }

    @Test
    public void testEmptyProductList() {
        List<Product> emptyList = new ArrayList<>();
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        double resultadoProAlgorithm = proAlgorithm.getResultadoForTest(emptyList,tags, setSimilarities);
        double resultadoBacktracking = backtracking.getResultadoForTest(emptyList,tags, setSimilarities);
        assertEquals(resultadoProAlgorithm, resultadoBacktracking, resultadoBacktracking/2,"Solucion incorrecta");
    }


    @Test
    public void testSingleProduct() {
        List<Product> singleProduct = new ArrayList<>(Arrays.asList(p1));
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        double resultadoProAlgorithm = proAlgorithm.getResultadoForTest(products,tags, setSimilarities);
        double resultadoBacktracking = backtracking.getResultadoForTest(products,tags, setSimilarities);
        assertEquals(resultadoProAlgorithm, resultadoBacktracking, resultadoBacktracking/2,"Solucion incorrecta");
    }

}
