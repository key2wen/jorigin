package com.key.jorigin.lambda;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class Streams2 {

    static List<String> lists  = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n");
    static List<String> lists2 = Arrays.asList("a", "c", "e", "g", "h", "j", "k", "m", "n");

    public static void stream() {
        Set<String> sameValues = new HashSet<String>();
        for (String l : lists) {
            for (String l2 : lists2) {
                if (l.equals(l2)) {
                    sameValues.add(l);
                    break;
                }
            }
        }
        System.out.println(sameValues);

        // 1.
        Set<String> sameValues2 = new HashSet<String>();
        lists.stream().forEach(l -> {
            lists2.stream().forEach(l2 -> {
                if (l.equals(l2)) {
                    sameValues2.add(l);
                }
            });
        });
        System.out.println(sameValues2);

        // 2
        Set<String> sameValues3 = new HashSet<String>();
        lists.stream().forEach(l -> {
            lists2.stream().filter(l2 -> l.equals(l2)).peek(l2 -> sameValues3.add(l)).count();
        });
        System.out.println(sameValues3);

        // 3
        Set<String> sameValues4 = new HashSet<String>();
        sameValues4 = lists.stream()
            .flatMap(l -> lists2.stream().filter(l2 -> l.equals(l2)))
            .collect(Collectors.toSet());
        System.out.println(sameValues4);
    }

    public static void optional() {

    }

    public static void main(String[] args) {
        stream();
    }
}
