package com.wu.framework.inner.layer.stereotype.analyze;

import com.wu.framework.inner.layer.stereotype.Layer;
import com.wu.framework.inner.layer.stereotype.LayerDefault;

import java.lang.annotation.Annotation;

/**
 * description layer 解析适配器
 *
 * @author Jia wei Wu
 * @date 2021/4/1 下午3:32
 */
public class LayerClassAnalyzeAdapter implements LayerDefault, Layer, LayerAnalyzeAdapter {


    /**
     * description 解析class
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/7 下午1:47
     */
    public Annotation analyze(Class clazz){
        return null;
    }


}
