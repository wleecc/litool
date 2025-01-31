package io.leaderli.litool.core.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author leaderli
 * @since 2022/7/17
 */
class LiIteratorTest {


    @Test
    void of() {

        Assertions.assertFalse(LiIterator.of((Iterable<Object>) null).hasNext());
        Assertions.assertFalse(LiIterator.of((Iterator<Object>) null).hasNext());


        Assertions.assertFalse(LiIterator.of((Object[]) null).hasNext());
        Assertions.assertFalse(LiIterator.of((Object) null).hasNext());
        Assertions.assertThrows(NoSuchElementException.class, LiIterator.of((Iterable<Object>) null)::next);


        Assertions.assertTrue(LiIterator.of(Arrays.asList(1, 2)).hasNext());
        Assertions.assertEquals(1, LiIterator.of(Arrays.asList(1, 2)).next());

        Assertions.assertThrows(Throwable.class, LiIterator.of(Arrays.asList(1, 2))::remove);

        Assertions.assertThrows(NoSuchElementException.class, LiIterator.of((Enumeration<Integer>) null)::next);
        Assertions.assertFalse(LiIterator.of(1).hasNext());

        // arr box
        Assertions.assertTrue(LiIterator.of(new int[]{1, 2}).hasNext());
        Assertions.assertTrue(LiIterator.of(new String[]{"1", "2"}).hasNext());
        Assertions.assertTrue(LiIterator.of("123".toCharArray()).hasNext());

        Assertions.assertTrue(LiIterator.of(new String[]{null}).hasNext());
        Assertions.assertFalse(LiIterator.of(new String[]{}).hasNext());

    }
}
