package com.wu.framework.easy.excel.stereotype;

import com.wu.framework.easy.excel.util.ISheetShowContextMethod;
import com.wu.framework.easy.excel.util.SheetNumContextMethod;
import com.wu.framework.easy.excel.util.SheetTextContextMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * description 导出注解
 * @author 吴佳伟
 * @date 2020/10/5 下午7:08
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EasyExcel {

    /**
     * 文件名
     * @return
     */
    String fileName();

    /**
     * 文件后缀
     * @return
     */
    String suffix() default "xls";

    /**
     * 字段列名注解
     * @return
     */
    Class<? extends Annotation> filedColumnAnnotation() default EasyExcelFiled.class;

    /**
     * 字段列名注解属性名
     * @return
     */
   String filedColumnAnnotationAttribute() default "name";

    /**
     * 多个 sheet
     * @return
     */
   boolean multipleSheet() default false;

    /**
     * multipleSheet true 有效
     * 工作簿每页限制长度
     * @return
     */
   int limit() default 65534;
    /**
     * multipleSheet true 有效
     * 工作簿展示内容
     */
    SheetShowContext sheetShowContext() default SheetShowContext.NUM ;

    @Getter
    @AllArgsConstructor
    enum SheetShowContext{
        NUM(SheetNumContextMethod.class),// 1000~2000
        TEXT(SheetTextContextMethod.class);// 一  二 三
        private Class<? extends ISheetShowContextMethod> iSheetShowContextMethod;
    }


}