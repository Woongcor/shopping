package com.shopping.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));  //~~.jpg 처럼 마지막 . 에서 jpg 제외한 이름 찾기
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); //기록하겠다
        fos.write(fileData);  // 아까 저기있던 파일 원격으로 기록한다.
        fos.close();

        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if(deleteFile.exists()){
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");

        }else{
            log.info("해당 파일이 존재하지 않습니다.");
        }
    }
}
