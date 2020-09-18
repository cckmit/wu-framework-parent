package com.wu.framework.easy.stereotype.upsert.dynamic.upsert;


import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertDS;
import com.wu.framework.easy.stereotype.upsert.upsert.IEasyUpsert;

/**
 * description EasyUpsert获取IEasyUpsert和注解CustomDS抽象类
 *
 * @author 吴佳伟
 * @date 2020/9/11 下午1:06
 */
public abstract class AbstractDynamicEasyUpsert {

     public abstract EasyUpsertDS determineEasyUpsertDS();

     public abstract IEasyUpsert determineIEasyUpsert();

}
