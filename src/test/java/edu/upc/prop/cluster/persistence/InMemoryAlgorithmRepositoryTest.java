package edu.upc.prop.cluster.persistence;



import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.domain.algorithms.Algorithm;
import edu.upc.prop.cluster.domain.algorithms.Backtracking;
import edu.upc.prop.cluster.domain.algorithms.ProAlgorithm;
import edu.upc.prop.cluster.domain.data.Product;
import edu.upc.prop.cluster.domain.data.Tag;
import edu.upc.prop.cluster.persistence.AlgorithmRepository.InMemoryAlgorithmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/*
  Autor José Durán Foix
 */

class InMemoryAlgorithmRepositoryTest {

    private InMemoryAlgorithmRepository repository;
    Backtracking backtracking;
    ProAlgorithm proAlgorithm;
    @Mock
    private Product p1,p2,p3,p4,p5,p6,p7,p8,p9,p10;
    @Mock
    private Tag t1,t2,t3,t4,t5,t6,t7,t8,t9,t10;
    Map<String,Double> tags;
    List<Product> products;
    List<String> productNames;

    @BeforeEach
    void setUp() {
        File file = new File("main.shelfgenerator");
        if (file.exists()) { file.delete(); }
        repository = new InMemoryAlgorithmRepository();
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
        productNames = new ArrayList<>(Arrays.asList("product1", "product2", "product3", "product4", "product5", "product6", "product7", "product8", "product9", "product10"));
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
        backtracking = new Backtracking();
        proAlgorithm = new ProAlgorithm();
    }

    @Test
    void testGetShelf() {
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        List<Product> productlist = backtracking.execute(products,tags, setSimilarities);
        List<String> resultNames = productlist.stream().map(Product::getName).collect(Collectors.toList());;
        repository.saveShelf(resultNames);
        assertEquals(resultNames, repository.getShelf(), "Solucion incorrecta");

    }

    @Test
    void testClearResults() {
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        List<Product> productlist = backtracking.execute(products,tags, setSimilarities);
        List<String> resultNames = productlist.stream().map(Product::getName).collect(Collectors.toList());;
        repository.saveShelf(resultNames);
        assertEquals(resultNames, repository.getShelf(), "Solucion incorrecta");

        repository.clear();
        assertEquals(Collections.emptyList(), repository.getShelf(), "Solucion incorrecta");
    }

    @Test
    void testJson () {
        Map<Pair<Integer,Integer>,Double> setSimilarities = Map.of();
        List<Product> productlist = backtracking.execute(products,tags, setSimilarities);
        List<String> resultNames = productlist.stream().map(Product::getName).collect(Collectors.toList());;
        repository.saveShelf(resultNames);
        repository.saveToJSON("main.shelfgenerator"); //en teoria guarda automaticament al JSON
        repository.clear();
        repository.loadFromJSON();
        assertEquals(resultNames, repository.getShelf(), "Solucion incorrecta");
    }
}



