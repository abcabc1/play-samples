package utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class CalculatorUtils {

    public static Boolean getBooleanResult(Boolean... booleans) {
        return Stream.of(booleans).reduce(true, (result, v) -> (result == v));
    }

    public static <A> Set<A> unionRelative(Set<A> set1, Set<A> set2){
        HashSet<A> all = new HashSet<A>(set1), diff = new HashSet<A>(set1);
        all.addAll(set2);
        diff.retainAll(set2);
        all.removeAll(diff);
        return all;
    }
}
