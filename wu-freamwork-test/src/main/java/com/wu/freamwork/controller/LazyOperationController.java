package com.wu.freamwork.controller;

import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.domain.Page;
import com.wu.framework.inner.lazy.database.expand.database.persistence.LazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.PerfectLazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import com.wu.framework.inner.lazy.database.test.pojo.DataBaseUser;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/4/17 10:37 下午
 */
@EasyController
public class LazyOperationController implements CommandLineRunner {

    private final LazyOperation lazyOperation;
    public final PerfectLazyOperation perfectLazyOperation;

    public LazyOperationController(LazyOperation lazyOperation, PerfectLazyOperation perfectLazyOperation) {
        this.lazyOperation = lazyOperation;
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

        EasyHashMap<String, String> easyHashMap = new EasyHashMap<>("temp_easy_hash");
        easyHashMap.put("type", "easy");
        easyHashMap.put("name", "map");
        easyHashMap.put("date", LocalDate.now().toString());
        final String s = easyHashMap.toEasyTableAnnotation(false).creatTableSQL();
//        lazyOperation.upsert(easyHashMap, Arrays.asList(easyHashMap));

//        test();

    }

    public void test() throws Exception {
        upsert();
        insert();
        update();
        delete();
        select();
        page();
        scroll();
    }

    /**
     * 更新或者插入
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午1:55
     **/
//    @Scheduled(fixedRate = 1000)
    public void upsert() {
        long s = System.currentTimeMillis();
        List<DataBaseUser> dataBaseUserList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DataBaseUser dataBaseUser = new DataBaseUser();
            dataBaseUser.setAddress("address");
            dataBaseUser.setBirthday(LocalDateTime.now().toString());
//            dataBaseUser.setId(12);
            dataBaseUser.setSex("woman");
            dataBaseUser.setUsername("methodName" + i);
            dataBaseUserList.add(dataBaseUser);
        }
        lazyOperation.upsert(dataBaseUserList);
        long e = System.currentTimeMillis();
        System.out.println("共计用时：" + (e - s) + "ms");
    }

    /**
     * 插入数据
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午1:56
     **/
    public void insert() {
        long s = System.currentTimeMillis();
        List<DataBaseUser> dataBaseUserList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            DataBaseUser dataBaseUser = new DataBaseUser();
            dataBaseUser.setAddress("address");
            dataBaseUser.setBirthday(LocalDateTime.now().toString());
//            dataBaseUser.setId(12);
            dataBaseUser.setSex("woman");
            dataBaseUser.setUsername("methodName" + i);
            dataBaseUserList.add(dataBaseUser);
        }
        lazyOperation.insert(dataBaseUserList);
        long e = System.currentTimeMillis();
        System.out.println("共计用时：" + (e - s) + "ms");
    }

    /**
     * 更新数据
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午1:56
     **/
    public void update() {
        long s = System.currentTimeMillis();
        List<DataBaseUser> dataBaseUserList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DataBaseUser dataBaseUser = new DataBaseUser();
            dataBaseUser.setAddress("address");
            dataBaseUser.setBirthday(LocalDateTime.now().toString());
//            dataBaseUser.setId(12);
            dataBaseUser.setSex("woman");
            dataBaseUser.setUsername("methodName" + i);
            dataBaseUserList.add(dataBaseUser);
            lazyOperation.updateById(dataBaseUser);
        }
        lazyOperation.updateAllByIdList(dataBaseUserList);
        long e = System.currentTimeMillis();
        System.out.println("共计用时：" + (e - s) + "ms");
    }

    /**
     * 删除
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午5:05
     **/
    public void delete() {
        long s = System.currentTimeMillis();
        DataBaseUser dataBaseUser = new DataBaseUser();
        dataBaseUser.setAddress("address");
        dataBaseUser.setBirthday(LocalDateTime.now().toString());
        dataBaseUser.setId(12);
        dataBaseUser.setSex("woman");
        dataBaseUser.setUsername("methodName");
        lazyOperation.deleteById(dataBaseUser);
        long e = System.currentTimeMillis();
        System.out.println("共计用时：" + (e - s) + "ms");
    }

    /**
     * 查询
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午5:05
     **/
    public void select() {
        long s = System.currentTimeMillis();
        DataBaseUser dataBaseUser = new DataBaseUser();
        dataBaseUser.setAddress("address");
//        dataBaseUser.setBirthday(LocalDateTime.now().toString());
//            dataBaseUser.setId(12);
        dataBaseUser.setSex("woman");
        dataBaseUser.setUsername("methodName");
        DataBaseUser dataBaseUser1 = lazyOperation.selectOne(dataBaseUser);
        System.out.println(dataBaseUser1);

        dataBaseUser.setUsername(null);
        List<DataBaseUser> dataBaseUserList = lazyOperation.selectAll(dataBaseUser);
        System.out.println(dataBaseUserList);
        long e = System.currentTimeMillis();
        System.out.println("共计用时：" + (e - s) + "ms");
    }

    /**
     * @param
     * @return
     * @describe 分页查询
     * @author Jia wei Wu
     * @date 2021/4/18 12:50 下午
     **/
    public void page() {
        Page<DataBaseUser> page = lazyOperation.page(new Page(), DataBaseUser.class, null);
        System.out.println(page);
    }

    /**
     * @param
     * @return
     * @describe 滚动查询
     * @author Jia wei Wu
     * @date 2021/4/18 7:31 下午
     **/
    public void scroll() throws Exception {
        perfectLazyOperation.scroll(null, DataBaseUser.class, null, m -> {
            System.out.println(m);
            return m;
        });
    }
}
