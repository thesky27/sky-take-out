package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传{}", file.getOriginalFilename());

        try {
            String filename = file.getOriginalFilename();//获取原始文件名
            String suffix = filename.substring(filename.lastIndexOf("."));//获取图片扩展名
            //构造新文件的名称
            UUID uuid = UUID.randomUUID();
            String name = uuid.toString() + suffix;

            //返回文件路径
            String filepath = aliOssUtil.upload(file.getBytes(), name);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }

        return Result.success();
    }
}
