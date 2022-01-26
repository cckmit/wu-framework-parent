package com.wu.freamwork.controller;

import com.wu.framework.easy.stereotype.upsert.component.IUpsert;
import com.wu.framework.easy.stereotype.upsert.dynamic.QuickEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.expand.database.persistence.LazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.PerfectLazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import com.wu.framework.inner.lazy.hbase.expland.bo.HBaseUserBo;
import com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * description HBase测试操作
 *
 * @author Jia wei Wu
 * @date 2021/4/8 下午4:11
 */
@Api(tags = "HBase测试操作")
@EasyController("/HBase")
public class HBaseController implements CommandLineRunner {

    private final HBaseOperation hBaseOperation;

    private final LazyOperation lazyOperation;
    private final IUpsert iUpsert;
    private final PerfectLazyOperation perfectLazyOperation;

    public HBaseController(HBaseOperation hBaseOperation, LazyOperation lazyOperation, IUpsert iUpsert, PerfectLazyOperation perfectLazyOperation) {
        this.hBaseOperation = hBaseOperation;
        this.lazyOperation = lazyOperation;
        this.iUpsert = iUpsert;
        this.perfectLazyOperation = perfectLazyOperation;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        mysql2HBase();

    }

    public void mysql2HBase() throws Exception {
        perfectLazyOperation.scroll(null, EasyHashMap.class, "select * from a_map_weather", easyHashMapPage -> {
            hBaseOperation.insert(easyHashMapPage.getRecord());
            return easyHashMapPage;
        });
    }

    @QuickEasyUpsert(type = EasyUpsertType.HBASE)
    @ApiOperation(tags = "HBase测试操作", value = "保存数据到HBase")
    @GetMapping("/save/HBase")
    public List<HBaseUserBo> saveHBase(@RequestParam(defaultValue = "100") Integer size) {
        List<HBaseUserBo> hBaseUserBoList = new ArrayList<>();
        long a = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            hBaseUserBoList.add(new HBaseUserBo().setUserName("hbase_user").setAge("12").setSex("男").setId(i));
        }
        return hBaseUserBoList;
    }
}
