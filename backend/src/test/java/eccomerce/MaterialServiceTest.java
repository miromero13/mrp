package eccomerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import eccomerce.backend_eccomerce.user.dto.CreateMaterialDto;
import eccomerce.backend_eccomerce.user.dto.UpdateMaterialDto;
import eccomerce.backend_eccomerce.user.entity.MaterialEntity;
import eccomerce.backend_eccomerce.user.repository.MaterialRepository;
import eccomerce.backend_eccomerce.user.service.MaterialService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private MaterialService materialService;

    private MaterialEntity materialMock;
    private CreateMaterialDto createDto;
    private UpdateMaterialDto updateDto;
    private UUID materialId;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();

        materialMock = new MaterialEntity();
        materialMock.setId(materialId);
        materialMock.setCode("MAT-001");
        materialMock.setName("Cemento");
        materialMock.setDescription("Cemento portland");
        materialMock.setMeasureUnit("kg");
        materialMock.setCurrentStock(100.0);
        materialMock.setMinimumStock(20.0);
        materialMock.setState(true);

        createDto = new CreateMaterialDto();
        createDto.setCode("MAT-001");
        createDto.setName("Cemento");
        createDto.setDescription("Cemento portland");
        createDto.setMeasureUnit("kg");
        createDto.setCurrentStock(100.0);
        createDto.setMinimumStock(20.0);
        createDto.setState(true);

        updateDto = new UpdateMaterialDto();
        updateDto.setName("Cemento modificado");
        updateDto.setDescription("Nueva descripcion");
        updateDto.setMeasureUnit("ton");
        updateDto.setCurrentStock(200.0);
        updateDto.setMinimumStock(50.0);
    }

    // ================================================================
    // CREATE
    // ================================================================

    @Test
    void createMaterial_debeRetornarExito_cuandoLosDatosSonValidos() {
        when(materialRepository.save(any(MaterialEntity.class))).thenReturn(materialMock);

        var response = materialService.createMaterial(createDto);

        assertTrue(response.isSuccess());
        assertEquals("Material creado correctamente", response.getMessage());
        assertEquals("MAT-001", response.getData().getCode());
        verify(materialRepository, times(1)).save(any(MaterialEntity.class));
    }

    @Test
    void createMaterial_debeRetornarError400_cuandoHayViolacionDeIntegridad() {
        when(materialRepository.save(any(MaterialEntity.class)))
            .thenThrow(new DataIntegrityViolationException("Duplicate entry",
                new RuntimeException("codigo duplicado")));

        var response = materialService.createMaterial(createDto);

        assertFalse(response.isSuccess());
        assertEquals(400, response.getStatusCode());
        assertEquals("No se pudo crear el material", response.getMessage());
    }

    @Test
    void createMaterial_debeRetornarError500_cuandoOcurreExcepcionInesperada() {
        when(materialRepository.save(any(MaterialEntity.class)))
            .thenThrow(new RuntimeException("Error inesperado"));

        var response = materialService.createMaterial(createDto);

        assertFalse(response.isSuccess());
        assertEquals(500, response.getStatusCode());
    }

    // ================================================================
    // UPDATE
    // ================================================================

    @Test
    void updateMaterial_debeRetornarExito_cuandoElMaterialExiste() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));
        when(materialRepository.save(any(MaterialEntity.class))).thenReturn(materialMock);

        var response = materialService.updateMaterial(materialId, updateDto);

        assertTrue(response.isSuccess());
        assertEquals("Material actualizado correctamente", response.getMessage());
        assertEquals("Cemento modificado", response.getData().getName());
        assertEquals("ton", response.getData().getMeasureUnit());
        assertEquals(200.0, response.getData().getCurrentStock());
    }

    @Test
    void updateMaterial_debeRetornarError404_cuandoElMaterialNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(materialRepository.findById(idInexistente)).thenReturn(Optional.empty());

        var response = materialService.updateMaterial(idInexistente, updateDto);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void updateMaterial_debeSoloActualizarCamposNoNulos() {
        UpdateMaterialDto parcialDto = new UpdateMaterialDto();
        parcialDto.setName("Solo nombre cambiado");

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));
        when(materialRepository.save(any(MaterialEntity.class))).thenReturn(materialMock);

        var response = materialService.updateMaterial(materialId, parcialDto);

        assertTrue(response.isSuccess());
        assertEquals("Solo nombre cambiado", response.getData().getName());
        assertEquals("Cemento portland", response.getData().getDescription()); // sin cambio
        assertEquals("kg", response.getData().getMeasureUnit());               // sin cambio
    }

    // ================================================================
    // GET ALL
    // ================================================================

    @Test
    void getAllMaterials_debeRetornarListaConMateriales() {
        when(materialRepository.findAll()).thenReturn(List.of(materialMock));

        var response = materialService.getAllMaterials();

        assertTrue(response.isSuccess());
        assertEquals(1, response.getCountData());
        assertEquals(1, response.getData().size());
    }

    @Test
    void getAllMaterials_debeRetornarListaVacia_cuandoNoHayMateriales() {
        when(materialRepository.findAll()).thenReturn(List.of());

        var response = materialService.getAllMaterials();

        assertTrue(response.isSuccess());
        assertEquals(0, response.getCountData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void getAllMaterials_debeRetornarError500_cuandoFallaElRepositorio() {
        when(materialRepository.findAll()).thenThrow(new RuntimeException("Fallo en BD"));

        var response = materialService.getAllMaterials();

        assertFalse(response.isSuccess());
        assertEquals(500, response.getStatusCode());
    }

    // ================================================================
    // GET BY ID
    // ================================================================

    @Test
    void getMaterialById_debeRetornarMaterial_cuandoExiste() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));

        var response = materialService.getMaterialById(materialId);

        assertTrue(response.isSuccess());
        assertEquals("Material encontrado", response.getMessage());
        assertEquals(materialId, response.getData().getId());
    }

    @Test
    void getMaterialById_debeRetornarError404_cuandoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(materialRepository.findById(idInexistente)).thenReturn(Optional.empty());

        var response = materialService.getMaterialById(idInexistente);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertEquals("No se pudo obtener el material", response.getMessage());
    }

    // ================================================================
    // DELETE (soft delete)
    // ================================================================

    @Test
    void deleteMaterial_debeCambiarEstadoAFalse_cuandoElMaterialExiste() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));
        when(materialRepository.save(any(MaterialEntity.class))).thenReturn(materialMock);

        var response = materialService.deleteMaterial(materialId);

        assertTrue(response.isSuccess());
        assertEquals("Material eliminado correctamente", response.getMessage());
        assertFalse(materialMock.getState()); // verificar soft delete
        verify(materialRepository, times(1)).save(materialMock);
    }

    @Test
    void deleteMaterial_debeRetornarError404_cuandoElMaterialNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(materialRepository.findById(idInexistente)).thenReturn(Optional.empty());

        var response = materialService.deleteMaterial(idInexistente);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        verify(materialRepository, never()).save(any());
    }
}