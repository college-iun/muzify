package com.muzify.service;

import com.muzify.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/** Хранение аудиофайлов на диске (папка app.storage.dir). */
@Service
public class StorageService {

    private static final Set<String> ALLOWED = Set.of("mp3", "wav", "ogg", "m4a");

    private final Path root;

    public StorageService(@Value("${app.storage.dir}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Сохраняет загруженный пользователем файл, возвращает имя в хранилище. */
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Файл не передан");
        }
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        ext = ext == null ? "" : ext.toLowerCase();
        if (!ALLOWED.contains(ext)) {
            throw ApiException.badRequest("Допустимые форматы: mp3, wav, ogg, m4a");
        }
        String name = UUID.randomUUID() + "." + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Не удалось сохранить файл");
        }
        return name;
    }

    /** Копирует поток (например, демо-файл из ресурсов) в хранилище под заданным именем. */
    public String saveStream(InputStream in, String filename) throws IOException {
        Files.copy(in, root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public Resource load(String filename) {
        Path p = root.resolve(filename).normalize();
        if (!p.startsWith(root) || !Files.exists(p)) {
            throw ApiException.notFound("Аудиофайл не найден");
        }
        return new FileSystemResource(p);
    }

    public void delete(String filename) {
        if (filename == null) return;
        try {
            Path p = root.resolve(filename).normalize();
            if (p.startsWith(root)) Files.deleteIfExists(p);
        } catch (IOException ignored) {
            // файл мог быть уже удалён - это не критично
        }
    }
}
