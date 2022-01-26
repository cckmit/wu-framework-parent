package com.wu.framework.inner.lazy.hbase.expland.persistence.method;


import com.wu.framework.inner.layer.stereotype.Layer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.hadoop.hbase.client.Connection;

public interface HBaseOperationMethodAdapter<P> extends Layer<P> {


    Object execute(Connection connection, Object... args) throws Exception;


    /**
     * description  方法操作参数
     *
     * @author Jia wei Wu
     * @date 2021/4/8 下午1:53
     */
    @Accessors(chain = true)
    @Data
    class HBaseExecuteParams {
        private Connection connection;
        private Object[] objects;

        public static HBaseExecuteParams build() {
            return new HBaseExecuteParams();
        }
    }


}
