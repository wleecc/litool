package io.leaderli.litool.core.exception;

import java.lang.reflect.Type;

/**
 * @author leaderli
 * @since 2022/8/13 9:34 AM
 */
public class UnsupportedTypeException extends RuntimeException {
    public UnsupportedTypeException(Type type) {

        super(type.getClass() + ":" + type.getTypeName());
    }

}
