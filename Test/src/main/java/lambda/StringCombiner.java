package lambda;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class StringCombiner {

    private StringBuilder sb        = new StringBuilder();
    private String        prefix    = "{";
    private String        suffix    = "}";
    private String        delimiter = "|";

    public StringCombiner(){
    }

    public StringCombiner(String prefix, String suffix, String delimiter){
        this.prefix = prefix;
        this.suffix = suffix;
        this.delimiter = delimiter;
    }

    private boolean isEmtry() {
        if (sb == null || sb.length() == 0) {
            return true;
        }
        return false;
    }

    public StringCombiner add(String element) {

        if (isEmtry()) {
            sb.append(prefix);
        } else {
            sb.append(delimiter);
        }
        sb.append(element);
        return this;
    }

    public StringCombiner merge(StringCombiner other) {
        // sb.append(suffix);
        sb.append(other.sb);
        return this;
    }

    @Override
    public String toString() {
        return "StringCombiner:" + sb.append(suffix);
    }

}
