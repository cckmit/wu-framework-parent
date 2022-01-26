package com.wu.bionic.language.vocalize.controller;

import com.wu.bionic.language.vocalize.uo.MusicUo;
import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.database.expand.database.persistence.LazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import io.swagger.annotations.ApiOperation;
import javazoom.jl.player.Player;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

/**
 * @author :  Jia Wei Wu
 * @version 1.0
 * @describe : 音乐
 * @date : 2021/3/2 7:45 下午
 */
@EasyController
public class MusicController {

    private final LazyOperation lazyOperation;

    public MusicController(LazyOperation lazyOperation) {
        this.lazyOperation = lazyOperation;
    }


    public void run(String... args) throws Exception {
        EasyHashMap easyHashMap = lazyOperation.executeSQLForBean("SELECT * FROM upsert_binary limit 1", EasyHashMap.class);
//        EasyHashMap easyHashMap = lazyOperation.executeSQLForBean("select voice from word where voice is NOT null  limit 1 ", EasyHashMap.class);
        byte[] file = easyHashMap.getBytes("file");
        Thread thread = new Thread() {
            private Player player;

            @SneakyThrows
            @Override
            public void run() {
                BufferedInputStream buffer = new BufferedInputStream(new ByteArrayInputStream(file));
                player = new Player(buffer);
                player.play();
            }
        };
        thread.start();
    }

    @ApiOperation(tags = "音乐", value = "添加音乐")
    @PostMapping("/music")
    public void save(@RequestPart MultipartFile multipartFile) {
        lazyOperation.insert(new MusicUo().setMusicName(multipartFile.getName()).setMultipartFile(multipartFile));
    }

}
