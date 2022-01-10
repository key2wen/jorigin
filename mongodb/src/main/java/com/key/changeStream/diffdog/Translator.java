package com.key.changeStream.diffdog;

import java.util.List;

/**
 * @author real
 */
public interface Translator<T, R> {


    /**
     * convert2List
     *
     * @param messageKey
     * @param data
     * @return
     */
    List<R> convert2List(MessageKey messageKey, T data);

    /**
     * convert2Obj
     *
     * @param messageKey
     * @param data
     * @return
     */
    R convert2Obj(MessageKey messageKey, T data);

}
