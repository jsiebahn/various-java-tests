package com.github.jsiebahn.various.tests.junit5;

import static org.junit.jupiter.params.provider.Arguments.of;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author jsiebahn
 * @since 19.07.2018
 */
class CollectionsPerformanceTest {

    private static final int TEST_SIZE = 500_000;

    private static final int RUNS_PER_METHOD = 100;
    private static final int WARM_UP_PER_METHOD = 5;

    private static final int RANDOM_SEED = 123_456_789;

    private static final int TIMEOUT_EACH_RUN_SECONDS = 30;
    private static final long TIMEOUT_PER_METHOD_MILLIS = 10 * 60 * 1_000;

    private static Set<Integer> givenInput;
    private static Set<Integer> givenToBeRemoved;

    private static Map<String, List<Long>> measurements = new LinkedHashMap<>();

    @BeforeAll
    static void initTestData() {

        givenInput = new HashSet<>();
        givenToBeRemoved = new HashSet<>();

        long start = System.currentTimeMillis();
        Random random = new Random(RANDOM_SEED);
        while (givenInput.size() < TEST_SIZE && givenToBeRemoved.size() < TEST_SIZE){
            if (givenInput.size() < TEST_SIZE) {
                givenInput.add(random.nextInt(TEST_SIZE * 2));
            }
            if (givenToBeRemoved.size() < TEST_SIZE) {
                givenToBeRemoved.add(random.nextInt(TEST_SIZE * 2));
            }
        }
        System.out.println("Test data creation finished after " + Duration.ofMillis(System.currentTimeMillis() - start));
    }

    @AfterAll
    static void tearDown() {
        measurements.forEach((functionName, durations) -> {
            Double average = durations.stream().collect(Collectors.averagingLong(value -> value));
            Long min = durations.stream().min(Long::compareTo).orElse(Long.MAX_VALUE);
            Long max = durations.stream().max(Long::compareTo).orElse(Long.MAX_VALUE);
            System.out.println(functionName + " cnt: " + durations.size());
            System.out.println(functionName + " avg: " + Duration.ofMillis(Math.round(average)));
            System.out.println(functionName + " min: " + Duration.ofMillis(Math.round(min)));
            System.out.println(functionName + " max: " + Duration.ofMillis(Math.round(max)));
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testMethodsProvider")
    void test(String functionName, TestRunner testRunner) {
        long methodStart = System.currentTimeMillis();
        measurements.put(functionName, new ArrayList<>());
        measurements.put(functionName + "WarmUp", new ArrayList<>());

        for (int i = 0; i < RUNS_PER_METHOD + WARM_UP_PER_METHOD; i++) {

            long methodRunTime = System.currentTimeMillis() - methodStart;
            if (methodRunTime > TIMEOUT_PER_METHOD_MILLIS) {
                System.out.println("METHOD TIMEOUT REACHED " + Duration.ofMillis(methodRunTime));
                break;
            }

            List<Integer> input = new ArrayList<>(givenInput);
            List<Integer> toBeRemoved = new ArrayList<>(givenToBeRemoved);
            List<Integer> actual = new ArrayList<>();

            final int run = i;

            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(TIMEOUT_EACH_RUN_SECONDS), () -> {
                Instant start = Instant.now();
                actual.addAll(testRunner.apply(input, toBeRemoved));
                Instant finish = Instant.now();
                Duration duration = Duration.between(start, finish);
//                System.out.println(functionName + ": " + actual.size() + " items kept.");
//                System.out.println(functionName + ": " + duration);
                if (run == WARM_UP_PER_METHOD) {
                    System.out.println(functionName + ": Warm up finished.");
                }
                if (run >= WARM_UP_PER_METHOD) {
                    measurements.get(functionName).add(duration.toMillis());
                }
                else {
                    measurements.get(functionName + "WarmUp").add(duration.toMillis());
                }
            });

            // maybe assertions
            Assertions.assertEquals(input.size(), givenInput.size());
            Assertions.assertEquals(toBeRemoved.size(), givenToBeRemoved.size());
        }
    }

    // really slow options are commented out
    private static Stream<Arguments> testMethodsProvider() {
        return Stream.of(
//                of("removeAll", (TestRunner) (input, toBeRemoved) -> {
//                        List<Integer> result = new ArrayList<>(input);
//                        result.removeAll(toBeRemoved);
//                        return result;
//                    }
//                ),
                of("removeAllSetConversion", (TestRunner) (input, toBeRemoved) -> {
                    List<Integer> result = new ArrayList<>(input);
                    Set<Integer> toBeRemovedSet = new HashSet<>(toBeRemoved);
                    result.removeAll(toBeRemovedSet);
                    return result;
                }),
//                of("removeIfContains", (TestRunner) (input, toBeRemoved) -> {
//                        List<Integer> result = new ArrayList<>(input);
//                        result.removeIf(toBeRemoved::contains);
//                        return result;
//                    }
//                ),
                of("removeIfContainsSetConversion", (TestRunner) (input, toBeRemoved) -> {
                    List<Integer> result = new ArrayList<>(input);
                    Set<Integer> toBeRemovedSet = new HashSet<>(toBeRemoved);
                    result.removeIf(toBeRemovedSet::contains);
                    return result;
                })
//                ,
//                of("sortAndIterateList", (TestRunner) (input, toBeRemoved) -> {
//                    List<Integer> resultSorted = input.stream().sorted().collect(Collectors.toList());
//                    List<Integer> toBeRemovedSorted = toBeRemoved.stream().sorted().collect(Collectors.toList());
//                    System.out.println("Sorting finished.");
//
//                    Iterator<Integer> resultIterator = resultSorted.iterator();
//                    Iterator<Integer> toBeRemovedIterator = toBeRemovedSorted.iterator();
//                    Integer currentInToBeRemoved = toBeRemovedIterator.hasNext() ? toBeRemovedIterator.next() : null;
//                    while (resultIterator.hasNext()) {
//                        Integer current = resultIterator.next();
//                        if (current.equals(currentInToBeRemoved)) {
//                            resultIterator.remove();
//                            continue;
//                        }
//                        while (toBeRemovedIterator.hasNext() && (currentInToBeRemoved < current)) {
//                            currentInToBeRemoved = toBeRemovedIterator.next();
//                        }
//                        if (current.equals(currentInToBeRemoved)) {
//                            resultIterator.remove();
//                        }
//                    }
//
//                    return resultSorted;
//                })
        );
    }

    private interface TestRunner extends BiFunction<List<Integer>, List<Integer>, List<Integer>> {

    }
}