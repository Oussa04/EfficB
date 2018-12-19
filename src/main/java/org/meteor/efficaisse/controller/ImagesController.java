package org.meteor.efficaisse.controller;

import org.apache.commons.io.FileUtils;
import org.meteor.efficaisse.service.StorageFileNotFoundException;
import org.meteor.efficaisse.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
public class ImagesController {

    private final StorageService storageService;

    @Autowired
    public ImagesController(StorageService storageService) {
        this.storageService = storageService;
    }



    @GetMapping("/images/{subFolder}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> serveFile(@PathVariable String subFolder,@PathVariable String filename) throws IOException {

        Resource file = storageService.loadAsResource(filename,subFolder);
        byte[] byteArray = FileUtils.readFileToByteArray(file.getFile());
        return ResponseEntity.ok().body(byteArray);
    }



    @ExceptionHandler(StorageFileNotFoundException.class)
    public static ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
