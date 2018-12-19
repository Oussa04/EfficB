package org.meteor.efficaisse.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    void init();

    String store(MultipartFile file,String subfolder);


    Path load(String filename , String subfolder);

    Resource loadAsResource(String filename,String subfolder);

    void deleteAll();

    void delete(String filename , String subfolder);

}
