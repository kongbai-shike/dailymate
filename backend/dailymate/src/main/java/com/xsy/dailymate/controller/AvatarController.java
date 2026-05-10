package com.xsy.dailymate.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class AvatarController {

    // 或写死如"/static/avatar/"
    @Value("${avatar.upload-dir:static/avatar/}")
    private String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 绝对/相对路径，static/avatar需存在且对外开放
        File dest = new File(uploadDir, fileName);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // 假设你配置了 /static/ 目录可被Web直接访问，生成访问url
        String url = "/static/avatar/" + fileName;
        // 返回url给前端保存
        return ResponseEntity.ok().body("{\"url\":\""+url+"\"}");
    }
}
