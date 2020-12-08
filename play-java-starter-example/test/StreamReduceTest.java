import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamReduceTest {
    @Test
    public void basic() {
        //Optional<T> reduce(BinaryOperator<T> accumulator);
        List<Integer> numList = Arrays.asList(1, 2, 3, 4, 5);
        int result = numList.stream().reduce((a, b) -> a + b).get();
        System.out.println(result);

        //T reduce(T identity, BinaryOperator<T> accumulator);
        numList = Arrays.asList(1, 2, 3, 4, 5);
        result = numList.stream().reduce(0, (a, b) -> a + b);
        System.out.println(result);

        //<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);
        numList = Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE);
        long longResult = numList.stream().reduce(0L, (a, b) -> a + b, (a, b) -> 0L);
        System.out.println(longResult);

        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        List<Integer> list1 = list.stream().reduce(new ArrayList<>(), (r, v) -> {
            if (v % 2 == 0) {
                r.add(v + 1);
            }
            return r;
        }, (r, v) -> null);
        System.out.println(list1);
    }
}
