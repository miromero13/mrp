package eccomerce.backend_eccomerce.backup.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class BackupStorageService {

    @Value("${backup.storage.path:/backups}")
    private String storagePath;

    public String guardar(Path archivoZip, UUID empresaId) throws IOException {
        // Crear directorio de la empresa si no existe
        Path dirEmpresa = Paths.get(storagePath, empresaId.toString());
        Files.createDirectories(dirEmpresa);

        // Mover archivo al directorio final
        Path destino = dirEmpresa.resolve(archivoZip.getFileName());
        Files.move(archivoZip, destino, StandardCopyOption.REPLACE_EXISTING);

        return destino.toAbsolutePath().toString();
    }

    public Resource cargar(String rutaArchivo) throws IOException {
        Path archivo = Paths.get(rutaArchivo);
        Resource resource = new UrlResource(archivo.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("No se puede leer el archivo: " + rutaArchivo);
        }
        return resource;
    }

}
