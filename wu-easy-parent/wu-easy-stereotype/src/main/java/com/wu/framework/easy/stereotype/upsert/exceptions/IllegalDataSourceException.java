package com.wu.framework.easy.stereotype.upsert.exceptions;

/**
 * description 非法数据源异常
 *
 * @author 吴佳伟
 * @date 2021/4/23 下午2:24
 */
public class IllegalDataSourceException extends RuntimeException {
    public IllegalDataSourceException(String message) {
        super(message);
    }

    public IllegalDataSourceException(Throwable cause) {
        super(cause);
    }
}
