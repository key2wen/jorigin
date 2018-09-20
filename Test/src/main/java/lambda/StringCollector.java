package lambda;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class StringCollector implements Collector<String, StringCombiner, String> {

    private String prefix    = "{";
    private String suffix    = "}";
    private String delimiter = "|";

    public StringCollector(){
    }

    public StringCollector(String prefix, String suffix, String delimiter){
        this.prefix = prefix;
        this.suffix = suffix;
        this.delimiter = delimiter;
    }

    public static StringCollector selfJoin(String prefix, String suffix, String delimiter) {
        return new StringCollector(prefix, suffix, delimiter);
    }

    @Override
    public Supplier<StringCombiner> supplier() {
        return () -> new StringCombiner(prefix, suffix, delimiter);
    }

    @Override
    public BiConsumer<StringCombiner, String> accumulator() {

        // return (sb, s) -> sb.add(s);
        return StringCombiner::add;
    }

    @Override
    public BinaryOperator<StringCombiner> combiner() {
        // return (sb, sb2) -> sb.merge(sb2);
        return StringCombiner::merge;
    }

    @Override
    public Function<StringCombiner, String> finisher() {
        // return (sb) -> sb.toString();
        return StringCombiner::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

}
