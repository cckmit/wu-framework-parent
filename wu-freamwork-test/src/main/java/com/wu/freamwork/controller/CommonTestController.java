package com.wu.freamwork.controller;


import com.wu.framework.inner.common.util.EasyCaptcha;
import com.wu.framework.inner.common.util.QRBO;
import com.wu.framework.inner.common.util.QRCodeGenerator;
import com.wu.framework.inner.layer.web.EasyController;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ApiOperation(value = "test", tags = "测试二维码 验证码")
@EasyController("/test")
public class CommonTestController {


    @GetMapping("/captcha.jpg")
    public void login(HttpServletResponse response) {
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        EasyCaptcha simpleCaptcha = new EasyCaptcha(200, 50, 4, 20);
        try {
            simpleCaptcha.write(response.getOutputStream());
//            RedisUtil.set(SecurityConstants.SPRING_SECURITY_RESTFUL_IMAGE_CODE+uuid, simpleCaptcha.getCode(), 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping(value = "/qrimage")
    public ResponseEntity<byte[]> getQRImage(QRBO qrbo) {

        //二维码内的信息
        String info = "This is my first QR Code";

        byte[] qrcode = null;
        qrcode = QRCodeGenerator.getQRCodeImage(qrbo.clearText(), 360, 360);
        // Set headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]>(qrcode, headers, HttpStatus.CREATED);
    }


}
