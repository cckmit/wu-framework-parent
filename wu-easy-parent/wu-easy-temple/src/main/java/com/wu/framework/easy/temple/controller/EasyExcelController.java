package com.wu.framework.easy.temple.controller;


import com.alibaba.fastjson.annotation.JSONField;
import com.wu.framework.easy.excel.stereotype.EasyExcel;
import com.wu.framework.easy.excel.stereotype.RequestExcelBody;
import com.wu.framework.easy.excel.util.EasyExcelUtil;
import com.wu.framework.easy.excel.util.FastExcelImp;
import com.wu.framework.easy.stereotype.upsert.dynamic.QuickEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.easy.temple.EasyExcelTemp;
import com.wu.framework.easy.temple.domain.ComplexUseExcel;
import com.wu.framework.easy.temple.domain.SmartExcel;
import com.wu.framework.easy.temple.domain.UseExcel;
import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2020/9/18 下午11:32
 */
@Api(tags = "导出注解测试")
@EasyController("/easy/excel")
public class EasyExcelController {


    @EasyExcel(fileName = "导出数据")
    @ApiOperation(tags = "导出注解测试", value = "使用原生注解有效")
    @GetMapping("/run/{size}")
    public List<UseExcel> run(@PathVariable Integer size) {
        List<UseExcel> useExcelList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UseExcel useExcel = new UseExcel();
            useExcel.setCurrentTime(LocalDateTime.now());
            useExcel.setDesc("默认方式导出数据");
            useExcel.setExcelId(i);
            useExcel.setType("默认方式双注解导出");
            useExcelList.add(useExcel);
        }
        return useExcelList;
    }

    @EasyExcel(fileName = "导出数据多个工作簿", multipleSheet = true)
    @ApiOperation(tags = "导出注解测试", value = "使用原生注解有效多个工作簿")
    @GetMapping("/run1/{size}")
    public List<UseExcel> run1(@PathVariable Integer size) {
        return run(size);
    }

    @EasyExcel(fileName = "非原生注解导出数据", fieldColumnAnnotation = JSONField.class, fieldColumnAnnotationAttribute = "name", multipleSheet = true, limit = 1000, sheetShowContext = EasyExcel.SheetShowContext.TEXT)
    @ApiOperation(tags = "导出注解测试", value = "非原生注解导出数据")
    @GetMapping("/run2/{size}")
    public List<UseExcel> run2(@PathVariable Integer size) {
        List<UseExcel> useExcelList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UseExcel useExcel = new UseExcel();
            useExcel.setCurrentTime(LocalDateTime.now());
            useExcel.setDesc("自定义字段注解方式导出数据");
            useExcel.setExcelId(i);
            useExcel.setType("自定义字段注解导出");
            useExcelList.add(useExcel);
        }
        return useExcelList;
    }

    @EasyExcel(fileName = "导出所有字段", useAnnotation = false)
    @ApiOperation(tags = "导出注解测试", value = "导出所有字段")
    @GetMapping("/run3/{size}")
    public List<UseExcel> run3(@PathVariable Integer size) {
        List<UseExcel> useExcelList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UseExcel useExcel = new UseExcel();
            useExcel.setCurrentTime(LocalDateTime.now());
            useExcel.setDesc("不使用注解导出所有字段");
            useExcel.setExcelId(i);
            useExcel.setType("导出所有字段");
            useExcelList.add(useExcel);
        }
        return useExcelList;
    }

    @EasyExcelTemp(fileName = "自定义注解导出")
    @ApiOperation(tags = "导出注解测试", value = "自定义注解导出")
    @GetMapping("/run4/{size}")
    public List<UseExcel> run4(@PathVariable Integer size) {
        List<UseExcel> useExcelList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UseExcel useExcel = new UseExcel();
            useExcel.setCurrentTime(LocalDateTime.now());
            useExcel.setDesc("自定义注解导出");
            useExcel.setExcelId(i);
            useExcel.setType("自定义注解导出");
            useExcelList.add(useExcel);
        }
        return useExcelList;
    }

    @EasyExcel(fileName = "复杂数据导出", isComplicated = true, fieldColumnAnnotation = JSONField.class, sheetShowContext = EasyExcel.SheetShowContext.TEXT)
    @ApiOperation(tags = "导出注解测试", value = "复杂数据导出")
    @GetMapping("/run5/{size}")
    public List<ComplexUseExcel> run5(@PathVariable Integer size) {
        List<UseExcel> useExcelList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            UseExcel useExcel = new UseExcel();
            useExcel.setCurrentTime(LocalDateTime.now());
            useExcel.setDesc("复杂数据导出内部数据");
            useExcel.setExcelId(i);
            useExcel.setType("复杂数据导出内部数据");
            useExcelList.add(useExcel);
        }

        List<ComplexUseExcel> complexUseExcelList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            ComplexUseExcel complexUseExcel = new ComplexUseExcel();
            complexUseExcel.setCurrentTime(LocalDateTime.now());
            complexUseExcel.setDesc("复杂数据导出需要合并单元格");
            complexUseExcel.setId(i);
            complexUseExcel.setType("复杂数据导出需要合并单元格");
            complexUseExcel.setUseExcelList(useExcelList);
            complexUseExcel.setSmartExcel(new SmartExcel());
            complexUseExcelList.add(complexUseExcel);
        }
        return complexUseExcelList;
    }


    @EasyExcel(fileName = "导出数据")
    @ApiOperation(tags = "导出注解测试", value = "使用原生注解有效(自定义一导出文件名称)")
    @GetMapping("/run6/{size}")
    public List<UseExcel> run6(@PathVariable Integer size, @RequestParam String fileName) {
        EasyExcelUtil.modifyCurrentMethodEasyExcelFileName(this.getClass(), fileName);
        return run(size);
    }



    @ApiOperation(tags = "导入注解测试", value = "导入Excel并转换成对象")
    @PostMapping("/imp1")
    public String import1(@RequestPart MultipartFile multipartFile) {
        List<UseExcel> userLogList = FastExcelImp.parseExcel(multipartFile, UseExcel.class);
        return userLogList.toString();
    }

    @ApiOperation(tags = "导入注解测试", value = "导入Excel并转换成EasyHashMap对象")
    @PostMapping("/imp1/hash-map")
    public List<EasyHashMap> implMap(@RequestPart MultipartFile file) {
        List<EasyHashMap> easyHashMapList = FastExcelImp.parseExcel(file, EasyHashMap.class);
        if (ObjectUtils.isEmpty(easyHashMapList)) {
            return easyHashMapList;
        }
        return easyHashMapList;
    }

}
