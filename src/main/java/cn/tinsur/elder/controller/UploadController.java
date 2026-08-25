package cn.tinsur.elder.controller;

import cn.tinsur.elder.util.AliOSSUtil;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class UploadController {

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @PostMapping("/upload")
    public Result<String> update (MultipartFile file){
        String uuid = UUID.randomUUID().toString().replace("-","");
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        fileName = uuid + extension;
        String url = "";
        try {
            url = aliOSSUtil.uploadFile(fileName, file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.ok("上传成功", url);
    }

    @DeleteMapping("/deleteAvatar")
    public Result<String> delete (String url){
        aliOSSUtil.deleteFile(url);
        return Result.ok("删除成功");
    }

}