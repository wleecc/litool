package io.leaderli.litool.core.type;

import io.leaderli.litool.core.meta.LiConstant;
import io.leaderli.litool.core.meta.Lino;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * @author leaderli
 * @since 2022/7/12
 */
class LiReflectUtilTest {

    static {
        LiConstant.WHEN_THROW = null;

    }

    public static class Bean {

        public String name = "bean";
        private int age = 80;


        @Override
        public String toString() {
            return "Bean{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static class LittleBean extends Bean {
        private String name = "little";
        public int age = 8;

        @Override
        public String toString() {
            return "LittleBean{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Test
    void getField() {

        LittleBean littleBean = new LittleBean();
        Assertions.assertEquals("bean", LiReflectUtil.getField(LittleBean.class, "name").throwable_map(f -> f.get(littleBean)).get());
        Assertions.assertEquals(8, LiReflectUtil.getField(LittleBean.class, "age").throwable_map(f -> f.get(littleBean)).get());

        Lino<Field> name = LiReflectUtil.getField(LittleBean.class, "name", true);
        Assertions.assertNull(name.throwable_map(f -> f.get(littleBean), null).get());
        Assertions.assertEquals("little", name.throwable_map(f -> {
            f.setAccessible(true);
            return f.get(littleBean);
        }).get());
        Assertions.assertEquals(8, LiReflectUtil.getField(LittleBean.class, "age", true).throwable_map(f -> f.get(littleBean)).get());
    }


    @SuppressWarnings("JavaReflectionMemberAccess")
    @Test
    void getFieldValue() throws NoSuchFieldException {

        LittleBean littleBean = new LittleBean();
        Assertions.assertEquals("bean", LiReflectUtil.getFieldValue(littleBean, "name").get());
        Assertions.assertEquals(8, LiReflectUtil.getFieldValue(littleBean, "age").get());

        Assertions.assertEquals("little", LiReflectUtil.getFieldValue(littleBean, "name", true).get());
        Assertions.assertEquals(8, LiReflectUtil.getFieldValue(littleBean, "age", true).get());


        Assertions.assertEquals(Lino.none(), LiReflectUtil.getFieldValue(null, "name"));
        Assertions.assertEquals(Lino.none(), LiReflectUtil.getFieldValue(null, "name", true));


        Assertions.assertEquals("bean", LiReflectUtil.getFieldValue(littleBean, LittleBean.class.getField("name")).get());
        Assertions.assertNull(LiReflectUtil.getFieldValue(littleBean, Lino.Some.class.getDeclaredField("value")).get());
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @Test
    void setFieldValue() throws NoSuchFieldException {
        LittleBean littleBean = new LittleBean();

        Assertions.assertFalse(LiReflectUtil.setFieldValue(null, "name", null));
        Assertions.assertFalse(LiReflectUtil.setFieldValue(null, "name", null, true));

        Assertions.assertEquals("bean", LiReflectUtil.getFieldValue(littleBean, "name").get());
        Assertions.assertEquals("little", LiReflectUtil.getFieldValue(littleBean, "name", true).get());
        Assertions.assertTrue(LiReflectUtil.setFieldValue(littleBean, "name", "hello"));
        Assertions.assertEquals("hello", LiReflectUtil.getFieldValue(littleBean, "name").get());
        Assertions.assertEquals("little", LiReflectUtil.getFieldValue(littleBean, "name", true).get());

        littleBean = new LittleBean();

        Assertions.assertEquals("bean", LiReflectUtil.getFieldValue(littleBean, "name").get());
        Assertions.assertEquals("little", LiReflectUtil.getFieldValue(littleBean, "name", true).get());
        Assertions.assertTrue(LiReflectUtil.setFieldValue(littleBean, "name", "hello", true));
        Assertions.assertEquals("bean", LiReflectUtil.getFieldValue(littleBean, "name").get());
        Assertions.assertEquals("hello", LiReflectUtil.getFieldValue(littleBean, "name", true).get());


        Assertions.assertFalse(LiReflectUtil.setFieldValue(littleBean, "name", 123, true));

        Assertions.assertEquals("hello", LiReflectUtil.getFieldValue(littleBean, "name", true).get());


        Assertions.assertTrue(LiReflectUtil.setFieldValue(littleBean, LittleBean.class.getField("name"), "hello"));

        Assertions.assertFalse(LiReflectUtil.setFieldValue(littleBean, Lino.Some.class.getDeclaredField("value"), "hello"));


    }
}
