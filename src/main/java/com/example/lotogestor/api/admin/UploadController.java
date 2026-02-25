package com.example.lotogestor.api.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class UploadController {

  private static final Path UPLOAD_DIR = Paths.get("uploads");

  @PostMapping("/upload")
  @ResponseStatus(HttpStatus.CREATED)
  public UploadResponse upload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio");
    }

    String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "foto" : file.getOriginalFilename());
    String ext = "";
    int dot = original.lastIndexOf('.');
    if (dot > -1) {
      ext = original.substring(dot);
    }
    String filename = UUID.randomUUID() + ext;

    try {
      Files.createDirectories(UPLOAD_DIR);
      Path target = UPLOAD_DIR.resolve(filename);
      file.transferTo(target);
      return new UploadResponse("/uploads/" + filename);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao salvar arquivo");
    }
  }
}
