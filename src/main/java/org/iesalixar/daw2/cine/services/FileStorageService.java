package org.iesalixar.daw2.cine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger logger =
            LoggerFactory.getLogger(FileStorageService.class);
    // Variable de entorno para la ruta de almacenamiento
    @Value("${UPLOAD_PATH}")
    private String uploadPath;
    /**
     * Guarda un archivo en el sistema de archivos y devuelve el nombre del
     archivo guardado.
     *
     * @param file El archivo a guardar.
     * @return El nombre del archivo guardado o null si ocurre un error.
     */
    public String saveFile(MultipartFile file) {
        try {
            // Generar un nombre único para el archivo
            String fileExtension =
                    getFileExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "." +
                    fileExtension;

            // Ruta completa del archivo
            Path filePath = Paths.get(uploadPath + File.separator + uniqueFileName);

            // Crear los directorios si no existen
            Files.createDirectories(filePath.getParent());

            // Guardar el archivo en la ruta
            Files.write(filePath, file.getBytes());
            logger.info("Archivo {} guardado con éxito.", uniqueFileName);
            return uniqueFileName; // Devolver el nombre del archivo para guardarlo en la base de datos
        } catch (IOException e) {
            logger.error("Error al guardar el archivo: {}", e.getMessage());
            return null;
        }
    }
    /**
     * Elimina un archivo del sistema de archivos.
     *
     * @param fileName El nombre del archivo a eliminar.
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);
            Files.deleteIfExists(filePath);
            logger.info("Archivo {} eliminado con éxito.", fileName);
        } catch (IOException e) {
            logger.error("Error al eliminar el archivo {}: {}", fileName,
                    e.getMessage());
        }
    }
    /**
     * Obtiene la extensión del archivo.
     *
     * @param fileName El nombre del archivo.
     * @return La extensión del archivo o una cadena vacía si no tiene extensión.
     */
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // Sin extensión
        }
    }
}