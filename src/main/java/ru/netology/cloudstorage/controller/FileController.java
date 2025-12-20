package ru.netology.cloudstorage.controller;

import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
public class FileController {

    private final FileService fileService;
    private final AuthService authService;

    private final CloudFileRepository fileRepository;

    public FileController(FileService fileService, AuthService authService, CloudFileRepository fileRepository) {
        this.fileService = fileService;
        this.authService = authService;
        this.fileRepository = fileRepository;
    }

    @PostMapping("/file")
    public ResponseEntity<Void> upload(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestPart("file") MultipartFile file) throws IOException {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String login = authService.getLoginByToken(token);
        fileService.uploadFile(login, filename, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> delete(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String login = authService.getLoginByToken(token);
        fileService.deleteFile(login, filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> download(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) throws IOException {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String login = authService.getLoginByToken(token);
        Resource resource = fileService.downloadFile(login, filename);

        CloudFile meta = fileRepository.findByOwnerLoginAndFilename(login, filename)
                .orElseThrow();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getOriginalName() + "\"")
                .body(resource);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> rename(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String oldName,
            @RequestBody Map<String, String> body) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String login = authService.getLoginByToken(token);
        String newName = body.get("filename");
        fileService.renameFile(login, oldName, newName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public List<FileListResponse> list(
            @RequestHeader("auth-token") String token,
            @RequestParam(defaultValue = "10") int limit) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String login = authService.getLoginByToken(token);
        return fileService.getFileList(login, limit);
    }
}