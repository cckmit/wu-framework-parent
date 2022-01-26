package com.wu.framework.easy.temple.controller;

import com.wu.framework.easy.stereotype.upsert.component.IUpsert;
import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertDS;
import com.wu.framework.easy.stereotype.upsert.dynamic.QuickEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.easy.temple.domain.UpsertBinary;
import com.wu.framework.easy.temple.domain.UseExcel;
import com.wu.framework.easy.temple.domain.UserLog;
import com.wu.framework.easy.temple.domain.bo.ExtractBo;
import com.wu.framework.easy.temple.domain.bo.MoreExtractBo;
import com.wu.framework.inner.layer.data.LayerDataAnalyzeAdapter;
import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
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
@Api(tags = "HBase数据快速插入")
@EasyController("/upsert/HBase")
public class UpsertHBaseController implements LayerDataAnalyzeAdapter {


    private final IUpsert iUpsert;

    public UpsertHBaseController(IUpsert iUpsert) {
        this.iUpsert = iUpsert;
    }

    /**
     * description IUpsert操作数据入DB
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/15 上午9:50
     */
    @EasyUpsertDS(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "IUpsert操作数据入HBase")
    @GetMapping()
    public List<UserLog> upsert(@RequestParam(required = false, defaultValue = "100") Integer size) {
        List<UserLog> userLogList = createUserLog(size);
        iUpsert.upsert(userLogList, userLogList, new UserLog());
        return userLogList;
    }


    /**
     * description 使用注解实现数据插入
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/19 上午10:11
     */
    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "使用注解实现数据插入")
    @GetMapping("/size")
    public List<UserLog> upsertSize(@RequestParam(required = false, defaultValue = "100") Integer size) {
        return createUserLog(size);
    }

    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "复杂数据EasyHashMap")
    @GetMapping("/easyHashMap")
    public List<EasyHashMap> easyHashMap(@RequestParam(required = false, defaultValue = "1000") Integer size) {
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


    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "复杂数据DB")
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

        val lists = extractData(null, extractBo);
        return extractBo;
    }

    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "复杂数据DB")
    @GetMapping("/moreExtractBo")
    public MoreExtractBo moreExtractBo() {
        MoreExtractBo moreExtractBo = new MoreExtractBo();
        ExtractBo extractBo = complexData();
        moreExtractBo.setExtractBo(extractBo);
        moreExtractBo.setUseExcel(extractBo.getUseExcel());
        moreExtractBo.setUserLog(extractBo.getUserLog());
        moreExtractBo.setUserLogList(createUserLog(1000));
        return moreExtractBo;
    }




    /**
     * description binary 或者文件类型数据插入
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/19 上午10:11
     */
    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase数据快速插入", value = "binary 数据插入")
    @GetMapping("/binary")
    public List<UpsertBinary> binary(@RequestParam(required = false, defaultValue = "1000") Integer size) {
        List<UpsertBinary> upsertBinaryList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            upsertBinaryList.add(new UpsertBinary());
        }
        return upsertBinaryList;
    }


    /**
     * description 创建测试数据
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/19 上午10:09
     */
    public List<UserLog> createUserLog(Integer size) {
        List<UserLog> userLogList = new ArrayList<>();
        size = size == null ? 10000 : size;
        for (int i = 0; i < size; i++) {
            UserLog userLog = new UserLog();
            userLog.setCurrentTime(LocalDateTime.now());
            userLog.setContent("创建时间:" + userLog.getCurrentTime());
            userLog.setUserId(i);
            userLogList.add(userLog);
        }
        return userLogList;
    }


}
