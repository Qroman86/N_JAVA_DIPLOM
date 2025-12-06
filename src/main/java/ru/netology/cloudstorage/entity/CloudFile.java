package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cloud_files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    private String filename;        // имя, под которым хранится на диске
    private String originalName;    // оригинальное имя от клиента
    private long size;
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "owner_login")
    private String ownerLogin;
}
