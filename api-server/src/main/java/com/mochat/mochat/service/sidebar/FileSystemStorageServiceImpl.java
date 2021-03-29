package com.mochat.mochat.service.sidebar;

import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author: yangpengwei
 * @time: 2021/1/27 2:11 下午
 * @description 文件存储
 */
@Slf4j
@Service
public class FileSystemStorageServiceImpl implements StorageService {

    private static final String UPLOAD_DIR = "upload-dir";

    private final Path rootLocation = Paths.get(UPLOAD_DIR);

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new ParamException("文件为空");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize()
                    .toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ParamException("文件为空");
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new CommonException(filename + " 文件不存在");

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CommonException(filename + " 文件读取失败");
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("文件夹创建失败", e);
        }
    }
}
