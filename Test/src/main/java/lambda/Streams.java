package lambda;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class Streams {

    static List<String>  lists       = Arrays.asList("a",
                                         "b",
                                         "c",
                                         "d",
                                         "e",
                                         "f",
                                         "g",
                                         "h",
                                         "i",
                                         "j",
                                         "k",
                                         "l",
                                         "m",
                                         "n");
    static List<String>  listRepeats = Arrays.asList("a", "b", "c", "d", "e", "f", "a", "b", "c", "d", "e", "f");
    static List<Integer> intLists    = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    static List<String>  strLists    = Arrays.asList("1", "2", "3", "4", "5", "6", "1", "2");
    static List<Date>    dateLists   = Arrays.asList(new Date(), new Date(), new Date());

    public static void stream() {
        long count = lists.stream().filter(s -> {
            // System.out.println(s);
            return s.compareTo("i") > 0;
        }).count();
        System.out.println(count);

        List<String> lists1 = lists.stream().filter(s -> s.equals("a") || s.equals("n")).collect(Collectors.toList());
        System.out.println(lists1);

        Set<String> lists2 = lists.stream().filter(s -> s.equals("b") || s.equals("n")).collect(Collectors.toSet());
        System.out.println(lists2);

        List<String> mapList = Stream.of("a", "b", "c", "d", "e", "f")
            .map(t -> t.toUpperCase())
            .collect(Collectors.toList());
        System.out.println(mapList);

        // flatMap：和map类似，不同的是其每个元素转换得到的是Stream对象，会把子Stream中的元素压缩到父集合中；
        List<String> flatMapList = Stream.of(lists1, lists2)
            .flatMap(lists -> lists.stream())
            .collect(Collectors.toList());
        System.out.println(flatMapList);

        long reduceSum = Stream.of(1L, 2L, 366666666666666444L, 466666L).reduce(0L, (acc, element) -> acc + element);
        System.out.println(reduceSum);

        long reduceSum2 = Stream.of(1, 2, 3, 4, 5).reduce((acc, element) -> acc * element).get();
        System.out.println("reduceSum2:" + reduceSum2);

        List<String> simpleList = listRepeats.stream().distinct().collect(Collectors.toList());
        System.out.println(simpleList);

        listRepeats.stream().peek(s -> System.out.print(s)).count();
        System.out.println();
        System.out.println(listRepeats.stream().limit(4).collect(Collectors.toList()));

        System.out.println(listRepeats.stream().skip(4).collect(Collectors.toList()));

        // List<Object> a = new ArrayList<Object>();
        // a.add(new Integer(3));

        // IntStream
        int sums = intLists.stream().mapToInt(i -> i).sum();
        System.out.println(sums);
        int strSum = strLists.stream().mapToInt((i -> Integer.parseInt(i))).distinct().sum();
        System.out.println(strSum);

        // Stream collect method
        List<Object> collectList = strLists.stream().collect(() -> new ArrayList<Object>(),
            (a2, b) -> a2.add(b),
            (a1, b1) -> a1.addAll(b1));
        System.out.println(collectList);
        List<Object> collectList2 = dateLists.stream().collect(() -> new ArrayList<Object>(), (a2, b) -> {
            a2.add(b);
            a2.get(0);
        }, (a1, b1) -> a1.addAll(b1));
        System.out.println(collectList2);

        System.out.println(listRepeats.stream().allMatch(s -> s.compareTo("n") <= 0));
        System.out.println(listRepeats.stream().anyMatch(s -> s.compareTo("b") <= 0));
        System.out.println(listRepeats.stream().findFirst().get());
        System.out.println(listRepeats.stream().noneMatch(s -> s.equals("A")));
    }

    public static void optional() {
        Optional<String> op = lists.stream().max(Comparator.comparing(s -> s));
        String min = op.get();

        System.out.println(min);

        Optional<List<String>> ooo = Optional.of(Arrays.asList("a", "b"));
        System.out.println(ooo.get());

        // throw nullPointerException
        // Optional<String> oooo = Optional.of(null);
        // System.out.println(oooo.get());

        Optional<String> ooo1o = Optional.empty();
        if (ooo1o.isPresent()) {
            System.out.println(ooo1o.get());
        }

        System.out.println(ooo1o.orElse("other"));

        System.out.println(ooo1o.orElseGet(() -> {
            String xxx = new String("troubleStr");
            return xxx;
        }));
    }

    public static void methodRefrence() {
        // map(l -> l.toUpperCase());
        List<String> xxx = listRepeats.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(xxx);

        // map(l->new String(l))
        List<String> newO = listRepeats.stream().map(String::new).collect(Collectors.toList());
        System.out.println(newO);

        listRepeats.stream().map(l -> new String[] { l }).forEach(ls -> System.out.print(ls[0]));
        System.out.println();
        listRepeats.stream().sorted().map(l -> l).forEach(System.out::print);
        System.out.println();

        // 收集器
        TreeSet<String> newjjj = listRepeats.stream().sorted().collect(Collectors.toCollection(() -> new TreeSet<>()));
        System.out.println(newjjj);

        String max = listRepeats.stream().sorted().collect(Collectors.maxBy(Comparator.comparing(l -> l))).get();
        System.out.println(max);

        Map<Boolean, List<String>> maps = lists.stream().collect(Collectors.partitioningBy(l -> l.compareTo("g") > 0));
        System.out.println(maps);

        Map<String, List<String>> groups = listRepeats.stream().collect(Collectors.groupingBy(l -> l));
        System.out.println(groups);

        String jj = lists.stream().collect(Collectors.joining());
        System.out.println(jj);

        String jjj = lists.stream().collect(Collectors.joining(","));
        System.out.println(jjj);

        String jjjj = lists.stream().collect(Collectors.joining(",", "[", "]"));
        System.out.println(jjjj);

        // groupingBy 组合收集器
        Map<String, Long> combineCollector = listRepeats.stream().collect(Collectors.groupingBy(l -> l,
            Collectors.counting()));
        System.out.println(combineCollector);

        Map<String, List<Integer>> oos = ObjectTest.getListObjects()
            .stream()
            .collect(Collectors.groupingBy(o -> o.getName(), Collectors.mapping(oo -> oo.getAge(), Collectors.toList())));
        System.out.println(oos);

    }

    public void selfJoinCollect() {
        {
            StringBuilder sb = lists.stream().reduce(new StringBuilder("init"), (ss, s) -> {
                if (ss.length() > 0) {
                    ss.append("|");
                }
                ss.append(s);
                return ss;
            }, (left, right) -> right);
            sb.insert(0, "{");
            sb.append("}");
            System.out.println(sb.toString());
            // BiFunction
        }

        {
            String xx = lists.stream()
            // .reduce(new StringCombiner("[", "]", ","), (a, element) ->
            // a.add(element), (a1, a2) -> a1.merge(a2))
                .reduce(new StringCombiner("[", "]", ","), StringCombiner::add, StringCombiner::merge)
                .toString();
            System.out.println(xx);

            String self = lists.stream().collect(StringCollector.selfJoin("[", "]", ","));
            System.out.println(self);
        }

        {// 非常低效率用法
            String manman = lists.stream()
                .collect(Collectors.reducing(new StringCombiner(),
                    s -> new StringCombiner("[", "]", ",").add(s),
                    (sb3, sb4) -> sb4))
                .toString();
            System.out.println(manman);
        }
    }

    Map<String, String> mapnew = new HashMap<String, String>();

    public String cacheMap(String key) {
        String value = "asdfasdfadf";
        mapnew.put(key, value);
        System.out.println("put...");
        return value;
    }

    public String hasExists(String key, String value) {
        System.out.println("key is " + key + ", value: " + value);
        return "jjj new Value";
    }

    public void newMap() {
        String value = mapnew.computeIfAbsent("dump&kill.sh", x -> this.cacheMap(x));
        System.out.println(value);
        String value2 = mapnew.computeIfAbsent("dump&kill.sh", this::cacheMap);
        System.out.println(value2);

        mapnew.computeIfPresent("jjjj", this::hasExists);
        mapnew.put("jjjj", "jjjvalue");
        mapnew.computeIfPresent("jjjj", this::hasExists);

        mapnew.put("1", "1value");
        mapnew.put("2", "2value");
        mapnew.put("3", "3value");
        List<String> listString = new ArrayList<String>();
        mapnew.forEach((k, v) -> {
            listString.add(k);
            listString.add(v);
        });
        System.out.println(listString);
    }

    public static void main(String[] args) {
        // stream();
        // optional();
        // methodRefrence();
        // new Streams().selfJoinCollect();

        new Streams().newMap();

    }
}
