package com.wu.framework.easy.temple.controller;

import com.wu.framework.easy.stereotype.upsert.component.IUpsert;
import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertDS;
import com.wu.framework.easy.stereotype.upsert.dynamic.QuickEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.easy.temple.domain.UseExcel;
import com.wu.framework.easy.temple.domain.UserLog;
import com.wu.framework.easy.temple.domain.bo.ExtractBo;
import com.wu.framework.easy.temple.domain.bo.MoreExtractBo;
import com.wu.framework.easy.temple.service.RunService;
import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2020/11/7 下午5:57
 */
@EasyController("/upsert/mysql")
public class UpsertMySQLController {


    private final IUpsert iUpsert;
    private final RunService runService;

    public UpsertMySQLController(IUpsert iUpsert, RunService runService) {
        this.iUpsert = iUpsert;
        this.runService = runService;
    }

    /**
     * description IUpsert操作数据入DB
     *
     * @param
     * @return
     * @exception/throws
     * @author 吴佳伟
     * @date 2021/4/15 上午9:50
     */
    @EasyUpsertDS(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "IUpsert操作数据入DB")
    @GetMapping()
    public List<UserLog> upsert(@RequestParam(required = false, defaultValue = "100") Integer size) {
        List<UserLog> userLogList = new ArrayList<>();
        size = size == null ? 1000 : size;
        for (int i = 0; i <= size; i++) {
            UserLog userLog = new UserLog();
            userLog.setCurrentTime(LocalDateTime.now());
            userLog.setContent("创建时间:" + userLog.getCurrentTime());
            userLog.setUserId(i + 1);
            userLogList.add(userLog);
        }
        iUpsert.upsert(userLogList, userLogList, new UserLog());
        return userLogList;
    }

    @EasyUpsertDS(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "service 实现类操作数据插入")
    @GetMapping("/size")
    public void upsertSize(@RequestParam(required = false, defaultValue = "100") Integer size) {
        runService.run(size);
    }


    @QuickEasyUpsert(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "复杂数据DB")
    @GetMapping("/complexData")
    public ExtractBo complexData() {
        UserLog userLog = new UserLog();
        userLog.setCurrentTime(LocalDateTime.now());
        userLog.setContent("创建时间:" + userLog.getCurrentTime());
        userLog.setUserId(1);

        UseExcel useExcel = new UseExcel();
        useExcel.setCurrentTime(LocalDateTime.now());
        useExcel.setDesc("默认方式导出数据");
        useExcel.setExcelId(2);
        useExcel.setType("默认方式双注解导出");

        ExtractBo extractBo = new ExtractBo();
        extractBo.setUserLog(userLog);
        extractBo.setUseExcel(useExcel);

//        extractData(null, extractBo);
        return extractBo;
    }

    @QuickEasyUpsert(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "复杂数据DB")
    @GetMapping("/moreExtractBo")
    public MoreExtractBo moreExtractBo() {
        MoreExtractBo moreExtractBo = new MoreExtractBo();
        ExtractBo extractBo = complexData();
        moreExtractBo.setExtractBo(extractBo);
        moreExtractBo.setUseExcel(extractBo.getUseExcel());
        moreExtractBo.setUserLog(extractBo.getUserLog());
        moreExtractBo.setUserLogList(runService.run(1000));
        return moreExtractBo;
    }

    @QuickEasyUpsert(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "复杂数据EasyHashMap")
    @GetMapping("/easyHashMap")
    public List<EasyHashMap> easyHashMap(
            @RequestParam(required = false, defaultValue = "1000") Integer size) {
        List<EasyHashMap> easyHashMapList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            EasyHashMap easyHashMap = new EasyHashMap("uniqueLabel");
            easyHashMap.put("第一个字段", "第一个字段");
            easyHashMap.put("第二个字段", "第二个字段");
            easyHashMap.put("第三个字段", "第三个字段");
            easyHashMap.put("第四个字段", "第四个字段");
            easyHashMapList.add(easyHashMap);
        }
        return easyHashMapList;
    }


    @QuickEasyUpsert(type = EasyUpsertType.MySQL)
    @ApiOperation(tags = "MySQL快速插入数据", value = "binary 数据插入")
    @GetMapping("/binary")
    public List binary(@RequestParam(required = false, defaultValue = "1000") Integer size) {
        return runService.binary(size);
    }

}
