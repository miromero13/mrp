package eccomerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eccomerce.backend_eccomerce.user.dto.CreateMovementDto;
import eccomerce.backend_eccomerce.user.entity.MaterialEntity;
import eccomerce.backend_eccomerce.user.entity.MovementEntity;
import eccomerce.backend_eccomerce.user.entity.MovementEntity.MovementType;
import eccomerce.backend_eccomerce.user.repository.MaterialRepository;
import eccomerce.backend_eccomerce.user.repository.MovementRepository;
import eccomerce.backend_eccomerce.user.service.MovementService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private MovementService movementService;

    private MaterialEntity materialMock;
    private CreateMovementDto movementDto;
    private UUID materialId;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();

        materialMock = new MaterialEntity();
        materialMock.setId(materialId);
        materialMock.setCode("MAT-001");
        materialMock.setName("Cemento");
        materialMock.setCurrentStock(100.0);
        materialMock.setMinimumStock(20.0);
        materialMock.setState(true);

        movementDto = new CreateMovementDto();
        movementDto.setAmount(30.0);
        movementDto.setMovementType(MovementType.ENTRADA);
    }

    // ================================================================
    // LOG REVENUE (entrada)
    // ================================================================

    @Test
    void logRevenue_debeAumentarStock_cuandoElMaterialExiste() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));

        var response = movementService.logRevenue(materialId, movementDto);

        assertTrue(response.isSuccess());
        assertEquals("Se ha aumentado la cantidad del material correctamente", response.getMessage());
        assertEquals(130.0, materialMock.getCurrentStock()); // 100 + 30
        verify(movementRepository, times(1)).save(any(MovementEntity.class));
        verify(materialRepository, times(1)).save(materialMock);
    }

    @Test
    void logRevenue_debeRetornarError404_cuandoElMaterialNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(materialRepository.findById(idInexistente)).thenReturn(Optional.empty());

        var response = movementService.logRevenue(idInexistente, movementDto);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertEquals("No se registro la entrada correctamente", response.getMessage());
        verify(movementRepository, never()).save(any());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void logRevenue_debeRetornarError500_cuandoFallaElGuardado() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));
        when(movementRepository.save(any(MovementEntity.class)))
            .thenThrow(new RuntimeException("Fallo en BD"));

        var response = movementService.logRevenue(materialId, movementDto);

        assertFalse(response.isSuccess());
        verify(materialRepository, never()).save(any());
    }

    // ================================================================
    // LOG EXPENSE (salida)
    // ================================================================

    @Test
    void logExpense_debeReducirStock_cuandoHayStockSuficiente() {
        movementDto.setMovementType(MovementType.SALIDA);
        movementDto.setAmount(40.0);
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));

        var response = movementService.logExpense(materialId, movementDto);

        assertTrue(response.isSuccess());
        assertEquals("Se ha reducido la cantidad del material correctamente", response.getMessage());
        assertEquals(60.0, materialMock.getCurrentStock()); // 100 - 40
        verify(movementRepository, times(1)).save(any(MovementEntity.class));
        verify(materialRepository, times(1)).save(materialMock);
    }

    @Test
    void logExpense_debeRetornarError404_cuandoStockEsInsuficiente() {
        movementDto.setMovementType(MovementType.SALIDA);
        movementDto.setAmount(999.0); // mayor al stock actual de 100
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));

        var response = movementService.logExpense(materialId, movementDto);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getError().contains("Stock insuficiente"));
        verify(movementRepository, never()).save(any());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void logExpense_debeRetornarError404_cuandoElMaterialNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(materialRepository.findById(idInexistente)).thenReturn(Optional.empty());

        var response = movementService.logExpense(idInexistente, movementDto);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertEquals("No se registro la salida correctamente", response.getMessage());
        verify(movementRepository, never()).save(any());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void logExpense_debeRetornarError500_cuandoFallaElGuardado() {
        movementDto.setAmount(10.0);
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(materialMock));
        when(movementRepository.save(any(MovementEntity.class)))
            .thenThrow(new RuntimeException("Fallo en BD"));

        var response = movementService.logExpense(materialId, movementDto);

        assertFalse(response.isSuccess());
        verify(materialRepository, never()).save(any());
    }
}
