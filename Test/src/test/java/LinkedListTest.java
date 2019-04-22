import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashMap;

public class LinkedListTest {


    @Test
    public void main() {

        //按访问顺序排序：
        LinkedHashMap<String, Object> map = new LinkedHashMap(16,
                0.75f, true);

        map.put("1", 11);
        map.put("2", 21);
        map.put("3", 31);

        //访问后，会将结点放到tail队尾
        map.get("3");
        map.get("1");
        map.get("2");
        map.get("1");

        Collection collection = map.keySet();
        //按访问顺序排序输出：（从head队头开始遍历）
        //3  2  1
        collection.forEach(c ->
                System.out.print(c + "  ")
        );

        map.values();
    }

    @Test
    public void main2() {

        //按写入顺序排序（默认排序方式）
        LinkedHashMap<String, Object> map = new LinkedHashMap(16,
                0.75f, false);

        //写入后，会将结点放到tail队尾
        map.put("1", 11);
        map.put("2", 21);
        map.put("3", 31);

        //访问后，不影响排序
        map.get("3");
        map.get("1");
        map.get("2");
        map.get("1");

        Collection collection = map.keySet();
        //按写入顺序排序输出：（从head队头开始遍历）
        //1  2  3
        collection.forEach(c ->
                System.out.print(c + "  ")
        );

        map.values();
    }

}
