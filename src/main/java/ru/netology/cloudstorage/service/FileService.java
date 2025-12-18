package ru.netology.cloudstorage.service;

import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final CloudFileRepository fileRepository;

    private final UserRepository userRepository;

    @Value("${app.storage-path:./uploads}")
    private String storagePath;

    public FileService(CloudFileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void uploadFile(String login, String filename, MultipartFile file) throws IOException {
        Optional<User> user = userRepository.findByLogin(login);
        if(user.isEmpty())  throw new RuntimeException("Пользователь с логином " + login + " не найден");;

        Path path = Paths.get(storagePath, login, filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        CloudFile cloudFile = new CloudFile();
        cloudFile.setFilename(filename);
        cloudFile.setOriginalName(file.getOriginalFilename());
        cloudFile.setSize(file.getSize());
        cloudFile.setContentType(file.getContentType());


        User currentUser = user.get();
        cloudFile.setOwner(currentUser);
        fileRepository.save(cloudFile);
    }


    public Resource downloadFile(String login, String filename) throws IOException {
        Path path = Paths.get(storagePath, login, filename);
        return new ByteArrayResource(Files.readAllBytes(path));
    }

    public List<FileListResponse> getFileList(String login, int limit) {
        return fileRepository.findByOwnerLogin(login).stream()
                .limit(limit)
                .map(f -> new FileListResponse(f.getFilename(), f.getSize()))
                .toList();
    }

    @Transactional
    public void deleteFile(String login, String filename) {
        fileRepository.deleteByOwnerLoginAndFilename(login, filename);
        try {
            Files.deleteIfExists(Paths.get(storagePath, login, filename));
        } catch (IOException ignored) {}
    }

    @Transactional
    public void renameFile(String login, String oldName, String newName) {
        CloudFile file = fileRepository.findByOwnerLoginAndFilename(login, oldName)
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setFilename(newName);
        file.setOriginalName(newName);
        fileRepository.save(file);

        try {
            Path oldPath = Paths.get(storagePath, login, oldName);
            Path newPath = Paths.get(storagePath, login, newName);
            Files.move(oldPath, newPath);
        } catch (IOException ignored) {}
    }
}
