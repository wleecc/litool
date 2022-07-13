package io.leaderli.litool.core.lang;

import io.leaderli.litool.core.meta.Lino;
import io.leaderli.litool.core.util.LiStringConvert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author leaderli
 * @since 2022/7/11
 */
public class BeanPath {

    private static final short STATE_BEGIN = 0;
    private static final short STATE_KEY = 1;
    private static final short STATE_KEY_END = 2;
    private static final short STATE_ARRAY = 3;
    private static final short STATE_ARRAY_END = 4;

    private static final char ARR_BEGIN = '[';
    private static final char ARR_END = ']';
    private static final char VARIABLE_SPLIT = '.';


    public static class BeginIllegalStateException extends IllegalStateException {

        public BeginIllegalStateException() {
            super("expression cannot start with  '.','[',']' ");
        }
    }

    public static class KeyEndIllegalStateException extends IllegalStateException {

        public KeyEndIllegalStateException() {
            super("expression after . cannot union with  '.','[',']' ");
        }
    }

    public static class ArrayEndIllegalStateException extends IllegalStateException {

        public ArrayEndIllegalStateException() {
            super("expression after ] only can union with  '.','[' ");
        }
    }

    public static class NotCompleteIllegalStateException extends IllegalStateException {

        public NotCompleteIllegalStateException() {
            super("expression is not complete");
        }
    }

    private final List<Function<Lino<Object>, Lino<Object>>> path = new ArrayList<>();

    /**
     * @param expression key 的表达式
     * @return {{@link #of(String, Function[])}}
     */
    public static BeanPath of(String expression) {

        //noinspection unchecked
        return of(expression, new Function[]{});
    }

    /**
     * 合法的表达式可以如下
     * <ul>
     *     <li>key1</li>
     *     <li>key1.key2</li>
     *     <li>key1[0]</li>
     *     <li>key1[0].key2</li>
     *     <li>key1[0][0]</li>
     *     <li></li>
     * </ul>
     * <p>
     * 其中表达数组或容器的标记，可以附加过滤器
     *
     * @param expression key的表达式
     * @param filters    针对 [index] list的过滤器，每个filter 对应一个 [index]
     * @return 指定位置的值
     */
    @SuppressWarnings({"unchecked"})
    public static BeanPath of(String expression, Function<Lino<?>, Lino<?>>... filters) {

        Objects.requireNonNull(expression, " expression is null");

        BeanPath beanPath = new BeanPath();


        int filter_index = 0;
        int state = STATE_BEGIN;

        StringBuilder temp = new StringBuilder();
        for (char ch : expression.toCharArray()) {

            switch (state) {
                case STATE_BEGIN:

                    if (ch == VARIABLE_SPLIT || ch == ARR_BEGIN || ch == ARR_END) {
                        throw new BeginIllegalStateException();
                    }
                    temp.append(ch);
                    state = STATE_KEY;
                    break;

                case STATE_KEY:


                    if (ch == VARIABLE_SPLIT) {
                        beanPath.setKeyFunction(temp);
                        temp = new StringBuilder();
                        state = STATE_KEY_END;
                    } else if (ch == ARR_BEGIN) {
                        beanPath.setKeyFunction(temp);
                        temp = new StringBuilder();
                        state = STATE_ARRAY;
                    } else if (ch == ARR_END) {
                        throw new IllegalStateException("key] it's not a valid expression");
                    } else {
                        temp.append(ch);
                    }
                    break;
                case STATE_KEY_END:
                    if (ch == VARIABLE_SPLIT || ch == ARR_BEGIN || ch == ARR_END) {
                        throw new KeyEndIllegalStateException();
                    }
                    temp.append(ch);
                    state = STATE_KEY;
                    break;
                case STATE_ARRAY:

                    if (ch >= '0' && ch <= '9') {

                        temp.append(ch);
                    } else if (ch == ARR_END) {

                        int index = LiStringConvert.parser(temp.toString(), -1);

                        Function<Lino<?>, Lino<?>> filter = null;
                        if (filter_index < filters.length) {

                            filter = filters[filter_index];
                            filter_index++;


                        }

                        beanPath.setArrFunction(index, filter);

                        temp = new StringBuilder();
                        state = STATE_ARRAY_END;

                    } else {
                        throw new IllegalStateException("[index] only support number");
                    }
                    break;

                case STATE_ARRAY_END:
                    if (ch == VARIABLE_SPLIT) {
                        state = STATE_KEY;
                    } else if (ch == ARR_BEGIN) {
                        state = STATE_ARRAY;
                    } else {

                        throw new ArrayEndIllegalStateException();

                    }
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("state %d not support", state));

            }


        }

        if (state == STATE_KEY && temp.length() > 0) {

            beanPath.setKeyFunction(temp);
            state = STATE_BEGIN;
        }

        if (state != STATE_BEGIN && state != STATE_ARRAY_END) {
            throw new NotCompleteIllegalStateException();
        }
        return beanPath;
    }

    private void setKeyFunction(StringBuilder key) {
        path.add(map -> map.cast(Map.class).map(m -> m.get(key.toString())));
    }

    private void setArrFunction(int index, Function<Lino<?>, Lino<?>> filter) {
        Function<Lino<Object>, Lino<Object>> filterWrapper;
        if (filter == null) {

            filterWrapper = m -> m
                    .isArray()
                    .toLira(Object.class)
                    .get(index);
        } else {
            filterWrapper = m -> m
                    .isArray()
                    .toLira(Object.class)
                    .filter(item -> filter.apply(Lino.of(item)))
                    .get(index);
        }
        path.add(filterWrapper);
    }

    /**
     * @param map@return
     */
    public Lino<Object> parse(Map<String, ?> map) {
        Lino<Object> of = Lino.of(map);

        for (Function<Lino<Object>, Lino<Object>> linoLinoFunction : path) {

            of = linoLinoFunction.apply(of);
        }

        return of;
    }

    public static Lino<Object> parse(Map<String, ?> obj, String expression) {

        return of(expression).parse(obj);
    }

    @SafeVarargs
    public static Lino<Object> parse(Map<String, ?> obj, String expression, Function<Lino<?>, Lino<?>>... filters) {
        return of(expression, filters).parse(obj);
    }
}
