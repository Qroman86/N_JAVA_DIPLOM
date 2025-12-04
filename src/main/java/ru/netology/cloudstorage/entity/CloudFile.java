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

    private String filename;        // имя, под которым хранится на диске
    private String originalName;    // оригинальное имя от клиента
    private long size;
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "owner_login")
    private String ownerLogin;
}
