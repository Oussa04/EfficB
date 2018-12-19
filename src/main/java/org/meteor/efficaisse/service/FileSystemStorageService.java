package org.meteor.efficaisse.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    private final String rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation =properties.getLocation();
    }

    @Override
    public String store(MultipartFile file,String subFolder) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        String filename = UUID.randomUUID().toString().replace("-","");
        Path root = Paths.get(rootLocation+"/"+subFolder);

        while(Files.exists(root.resolve(filename+"."+extension))){
            filename = UUID.randomUUID().toString().replace("-","");
        }
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            Files.createDirectories(root);
            Files.copy(file.getInputStream(), root.resolve(filename+"."+extension),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
        return filename+"."+extension;
    }



    @Override
    public Path load(String filename,String subfolder) {

        Path root = Paths.get(rootLocation+"/"+subfolder);

        return root.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename,String subfolder) {
        try {
            Path file = load(filename,subfolder);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        Path root = Paths.get(rootLocation);
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public void delete(String filename, String subfolder) {
        Path root = Paths.get(rootLocation+"/"+subfolder);
        try {
            Files.delete(root.resolve(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {

            Path root = Paths.get(rootLocation);
            Files.createDirectories(root);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
