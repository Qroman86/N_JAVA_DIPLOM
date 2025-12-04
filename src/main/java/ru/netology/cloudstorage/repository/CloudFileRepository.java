package ru.netology.cloudstorage.repository;

import ru.netology.cloudstorage.entity.CloudFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CloudFileRepository extends JpaRepository<CloudFile, Long> {
    List<CloudFile> findByOwnerLogin(String ownerLogin);
    Optional<CloudFile> findByOwnerLoginAndFilename(String ownerLogin, String filename);
    void deleteByOwnerLoginAndFilename(String ownerLogin, String filename);
}
