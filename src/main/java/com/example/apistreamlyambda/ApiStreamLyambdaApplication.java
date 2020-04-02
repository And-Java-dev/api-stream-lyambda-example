package com.example.apistreamlyambda;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class ApiStreamLyambdaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApiStreamLyambdaApplication.class, args);
    }


    List<Person> people = Arrays.asList(
            new Person("David", 35),
            new Person("Sarah", 24),
            new Person("Tom", 15),
            new Person("Clair", 35)
    );

    @Override
    public void run(String... args) throws Exception {


        //COLLECT

        List<Person> filtered = people.stream()
                .filter(p -> p.name.startsWith("C"))
                .collect(Collectors.toList());
        System.out.println(filtered);

        Map<Integer, List<Person>> personsByAge = people.stream()
                .collect(Collectors.groupingBy(p -> p.age));

        personsByAge.forEach((age, p) -> System.out.format("age %s: %s\n", age, p));


        Double averageAge = people.stream()
                .collect(Collectors.averagingInt(p -> p.age));
        System.out.println(averageAge);

        IntSummaryStatistics ageSummary = people.stream()
                .collect(Collectors.summarizingInt(p -> p.age));

        System.out.println(ageSummary);

        String phrase = people.stream()
                .filter(p -> p.age >= 18)
                .map(p -> p.name)
                .collect(Collectors.joining("and", "in Germany", "are considered adults"));
        System.out.println(phrase);

        Map<Integer, String> map = people.stream()
                .collect(Collectors.toMap(
                        p -> p.age,
                        p -> p.name,
                        (name1, name2) -> name1 + ";" + name2));
        System.out.println(map);

        Collector<Person, StringJoiner, String> personNameCollector =
                Collector.of(
                        () -> new StringJoiner(" |  "),
                        (j, p) -> j.add(p.name.toUpperCase()),
                        StringJoiner::merge,
                        StringJoiner::toString
                );

        String names = people.stream()
                .collect(personNameCollector);
        System.out.println(names);


        //FLATMAP


        List<Foo> foos = new ArrayList<>();

        foos.stream()
                .flatMap(f -> f.bars.stream())
                .forEach(b -> System.out.println(b.getName()));
        IntStream.range(1, 4)
                .mapToObj(i -> new Foo("Foo" + i))
                .peek(f -> IntStream.range(1, 4)
                .mapToObj(i -> new Bar("Bar" + i + " <- " + f.getName()))
                .forEach(f.bars::add))
                .flatMap(f -> f.bars.stream())
                .forEach(b -> System.out.println(b.getName()));


        Optional.of(new Outer())
                .flatMap(o -> Optional.ofNullable(o.nested))
                .flatMap(n -> Optional.ofNullable(n.inner))
                .flatMap(i -> Optional.ofNullable(i.foo))
                .ifPresent(System.out::println);


        //REDUCE

        people.stream()
                .reduce((p1,p2) -> p1.age < p2.age ? p1 : p2)
                .ifPresent(System.out::println);

        Person result = people.stream()
                .reduce(new Person("",0),(p1,p2) -> {
                    p1.age += p2.age;
                    p1.name += p2.name;
                    return p1;
                });

        System.out.format("name=%s; age=%s", result.name, result.age);

        Integer ageSum = people.stream()
                .reduce(0, (sum, p) -> sum+= p.age, (sum1, sum2) -> sum1 + sum2);
        System.out.println(ageSum);

        Integer ageSum2 = people
                .parallelStream()
                .reduce(0,
                        (sum, p) -> {
                    System.out.format("accumulator: sum=%s; person=%s\n", sum,p);
                    return sum += p.age;
                        },
                        (sum1, sum2) -> {
                            System.out.format("combiner: sum=%s; sum2=%s\n", sum1,sum2);
                            return sum1 + sum2;
                        });
    }


}
