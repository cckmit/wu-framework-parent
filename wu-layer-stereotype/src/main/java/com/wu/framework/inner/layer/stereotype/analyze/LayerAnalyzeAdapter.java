package com.wu.framework.inner.layer.stereotype.analyze;

/**
 * description layer 解析适配器
 *
 * @author Jia wei Wu
 * @date 2021/4/1 下午3:32
 */
public interface LayerAnalyzeAdapter<A> {

    <A> A analyze(Class clazz);
}
