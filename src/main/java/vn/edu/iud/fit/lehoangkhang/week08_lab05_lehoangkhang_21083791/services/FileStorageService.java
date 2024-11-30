package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads/logos/";
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }

    public String saveFile(MultipartFile file) {
        try {
            // Tạo tên file unique
            String fileName = System.currentTimeMillis() + "_" + 
                            StringUtils.cleanPath(file.getOriginalFilename());
            
            // Tạo đường dẫn đầy đủ
            Path targetLocation = Path.of(uploadDir + fileName);
            
            // Copy file vào thư mục đích
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }
} 