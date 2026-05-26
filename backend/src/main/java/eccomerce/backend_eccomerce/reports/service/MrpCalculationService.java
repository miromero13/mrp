package eccomerce.backend_eccomerce.reports.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentDetailEntity;
import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentEntity;
import eccomerce.backend_eccomerce.inventory.repository.InventoryRepository;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;
import eccomerce.backend_eccomerce.materials.repository.MaterialRepository;
import eccomerce.backend_eccomerce.products.entity.ProductEntity;
import eccomerce.backend_eccomerce.products.entity.ProductMaterialEntity;
import eccomerce.backend_eccomerce.products.repository.ProductMaterialRepository;
import eccomerce.backend_eccomerce.products.repository.ProductRepository;
import eccomerce.backend_eccomerce.reports.config.MrpProperties;
import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MrpCalculationService {

    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final InventoryRepository inventoryRepository;
    private final MrpProperties mrpProperties;

    public List<OrdenSugeridaDto> generateOrdenesSugeridas() {
        List<OrdenSugeridaDto> ordenes = new ArrayList<>();
        LocalDate horizonDate = LocalDate.now().plusDays(mrpProperties.getHorizonDays());

        List<ProductEntity> productsToProduce = inferProductionFromStock();

        for (ProductEntity product : productsToProduce) {
            BigDecimal productionQty = product.getStockMin().subtract(getProductAvailableStock(product));
            if (productionQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            List<ProductMaterialEntity> materials = productMaterialRepository
                    .findMaterialsByProduct(product);

            for (ProductMaterialEntity pm : materials) {
                MaterialEntity material = pm.getMaterial();
                BigDecimal requiredQty = productionQty.multiply(pm.getQuantity());
                BigDecimal availableQty = getMaterialAvailableStock(material);

                if (availableQty.compareTo(requiredQty) < 0) {
                    BigDecimal orderQty = requiredQty.subtract(availableQty);
                    LocalDate needDate = horizonDate.minusDays(material.getLeadTimeDays() != null ? material.getLeadTimeDays() : 0);

                    ordenes.add(OrdenSugeridaDto.builder()
                            .codigo(material.getCode())
                            .nombre(material.getName())
                            .tipo("MATERIAL")
                            .requerido(requiredQty)
                            .disponible(availableQty)
                            .ordenar(orderQty)
                            .fechaNecesidad(needDate)
                            .accion("COMPRAR")
                            .costoUnitario(material.getUnitCost())
                            .costoTotal(orderQty.multiply(material.getUnitCost()))
                            .build());
                }
            }
        }

        ordenes.sort(Comparator.comparing(OrdenSugeridaDto::getFechaNecesidad));
        return ordenes;
    }

    private List<ProductEntity> inferProductionFromStock() {
        return productRepository.findProductsWithMinStock().stream()
                .filter(p -> getProductAvailableStock(p).compareTo(p.getStockMin()) < 0)
                .collect(Collectors.toList());
    }

    private BigDecimal getProductAvailableStock(ProductEntity product) {
        return productRepository.findById(product.getId())
                .map(p -> p.getStockMin().subtract(p.getStockMin()))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMaterialAvailableStock(MaterialEntity material) {
        BigDecimal onHand = material.getStockCurrent() != null ? material.getStockCurrent() : BigDecimal.ZERO;
        BigDecimal committed = calculateCommittedStock(material);
        BigDecimal onOrder = calculateOnOrderStock(material);
        return onHand.subtract(committed).add(onOrder);
    }

    private BigDecimal calculateCommittedStock(MaterialEntity material) {
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateOnOrderStock(MaterialEntity material) {
        LocalDateTime cutoffDate = LocalDateTime.now().plusDays(mrpProperties.getHorizonDays());
        List<InventoryMomentDetailEntity> pendingOrders = inventoryRepository
                .findInventoryHistoryByMaterial(material, cutoffDate);

        return pendingOrders.stream()
                .filter(d -> !d.getIsInput() && "PENDING".equals(d.getInventoryMoment().getStatus()))
                .map(InventoryMomentDetailEntity::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ActionMessageDto> generateActionMessages() {
        List<ActionMessageDto> messages = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(mrpProperties.getHorizonDays());
        LocalDateTime endDate = LocalDateTime.now();

        List<InventoryMomentEntity> movements = inventoryRepository.findByMovementDateBetween(startDate, endDate);

        for (InventoryMomentEntity movement : movements) {
            if (movement.getType() == InventoryMomentEntity.InventoryMomentType.ORDEN_COMPRA ||
                movement.getType() == InventoryMomentEntity.InventoryMomentType.ORDEN_PRODUCCION) {

                if ("COMPLETED".equals(movement.getStatus())) {
                    LocalDate expectedDate = movement.getCreatedAt().toLocalDate().plusDays(7);
                    LocalDate actualDate = movement.getMovementDate().toLocalDate();

                    if (actualDate.isAfter(expectedDate)) {
                        for (InventoryMomentDetailEntity detail : movement.getInventoryMomentDetails()) {
                            messages.add(ActionMessageDto.builder()
                                    .codigoItem(detail.getMaterial().getCode())
                                    .nombreItem(detail.getMaterial().getName())
                                    .tipoItem("MATERIAL")
                                    .tipoAccion("RETRASAR")
                                    .fechaAnterior(expectedDate)
                                    .fechaNueva(actualDate)
                                    .cantidadAnterior(detail.getQuantity())
                                    .cantidadNueva(detail.getQuantity())
                                    .razon("La orden llegó después de la fecha esperada")
                                    .prioridad("MEDIUM")
                                    .fechaActual(LocalDate.now())
                                    .build());
                        }
                    }
                }
            }
        }

        messages.sort(Comparator.comparing(ActionMessageDto::getPrioridad).reversed());
        return messages;
    }

    public List<ExceptionDto> generateExceptions() {
        List<ExceptionDto> exceptions = new ArrayList<>();

        List<MaterialEntity> lowStockMaterials = materialRepository.findMaterialsBelowMinStock();
        for (MaterialEntity material : lowStockMaterials) {
            exceptions.add(ExceptionDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .tipo("MATERIAL")
                    .tipoExcepcion("STOCK_BAJOS")
                    .cantidadActual(material.getStockCurrent())
                    .cantidadMinima(material.getStockMin())
                    .cantidadAfectada(material.getStockMin().subtract(material.getStockCurrent()))
                    .severidad("CRITICAL")
                    .estado("PENDING")
                    .descripcion("El stock actual está por debajo del mínimo")
                    .build());
        }

        List<MaterialEntity> excessStockMaterials = materialRepository.findMaterialsWithExcessStock();
        for (MaterialEntity material : excessStockMaterials) {
            exceptions.add(ExceptionDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .tipo("MATERIAL")
                    .tipoExcepcion("EXCESO")
                    .cantidadActual(material.getStockCurrent())
                    .cantidadMinima(material.getStockMin())
                    .cantidadAfectada(material.getStockCurrent().subtract(material.getStockMin()))
                    .severidad("WARNING")
                    .estado("PENDING")
                    .descripcion("El stock actual excede 3 veces el mínimo")
                    .build());
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(mrpProperties.getObsoleteDays());
        List<MaterialEntity> materialsWithStock = materialRepository.findMaterialsWithStock();
        for (MaterialEntity material : materialsWithStock) {
            LocalDateTime lastMovement = getLastMovementDate(material);
            if (lastMovement == null || lastMovement.isBefore(cutoffDate)) {
                exceptions.add(ExceptionDto.builder()
                        .codigo(material.getCode())
                        .nombre(material.getName())
                        .tipo("MATERIAL")
                        .tipoExcepcion("OBSOLETO")
                        .cantidadActual(material.getStockCurrent())
                        .cantidadMinima(material.getStockMin())
                        .cantidadAfectada(material.getStockCurrent())
                        .severidad("INFO")
                        .estado("PENDING")
                        .descripcion("Material sin movimientos en los ultimos " + mrpProperties.getObsoleteDays() + " dias")
                        .build());
            }
        }

        exceptions.sort((e1, e2) -> {
            int severityOrder = getSeverityOrder(e1.getSeveridad()) - getSeverityOrder(e2.getSeveridad());
            return severityOrder != 0 ? severityOrder : e1.getCodigo().compareTo(e2.getCodigo());
        });

        return exceptions;
    }

    private int getSeverityOrder(String severity) {
        return switch (severity) {
            case "CRITICAL" -> 0;
            case "WARNING" -> 1;
            case "INFO" -> 2;
            default -> 3;
        };
    }

    public List<InventorySummaryDto> generateInventorySummary() {
        List<InventorySummaryDto> summaries = new ArrayList<>();
        List<MaterialEntity> materials = materialRepository.findByIsActiveTrue();

        for (MaterialEntity material : materials) {
            BigDecimal onHand = material.getStockCurrent() != null ? material.getStockCurrent() : BigDecimal.ZERO;
            BigDecimal committed = BigDecimal.ZERO;
            BigDecimal onOrder = calculateOnOrderStock(material);
            BigDecimal available = onHand.subtract(committed).add(onOrder);

            LocalDateTime lastMovement = getLastMovementDate(material);
            Integer daysWithout = lastMovement != null ?
                    (int) java.time.Duration.between(lastMovement, LocalDateTime.now()).toDays() : null;

            String estado = determineEstado(onHand, material.getStockMin());

            summaries.add(InventorySummaryDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .unidadMedida(material.getUnitOfMeasure())
                    .stockActual(onHand)
                    .stockMinimo(material.getStockMin())
                    .comprometido(committed)
                    .enOrden(onOrder)
                    .disponibleNeto(available)
                    .costoUnitario(material.getUnitCost())
                    .valorTotal(onHand.multiply(material.getUnitCost()))
                    .ultimoMovimiento(lastMovement != null ? lastMovement.toLocalDate() : null)
                    .diasSinMovimiento(daysWithout)
                    .estado(estado)
                    .build());
        }

        summaries.sort(Comparator.comparing(InventorySummaryDto::getCodigo));
        return summaries;
    }

    private LocalDateTime getLastMovementDate(MaterialEntity material) {
        List<InventoryMomentDetailEntity> history = inventoryRepository
                .findInventoryHistoryByMaterial(material, LocalDateTime.now());
        return history.isEmpty() ? null : history.get(0).getInventoryMoment().getMovementDate();
    }

    private String determineEstado(BigDecimal stock, BigDecimal stockMin) {
        if (stock.compareTo(BigDecimal.ZERO) == 0) {
            return "CRITICO";
        } else if (stock.compareTo(stockMin) < 0) {
            return "BAJO";
        } else if (stock.compareTo(stockMin.multiply(BigDecimal.valueOf(3))) > 0) {
            return "EXCESO";
        }
        return "NORMAL";
    }

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        List<OrdenSugeridaDto> ordenes = generateOrdenesSugeridas();
        List<ActionMessageDto> acciones = generateActionMessages();
        List<ExceptionDto> excepciones = generateExceptions();
        List<InventorySummaryDto> inventario = generateInventorySummary();

        summary.put("totalOrdenesSugeridas", ordenes.size());
        summary.put("costoTotalOrdenes", ordenes.stream()
                .map(OrdenSugeridaDto::getCostoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("totalAcciones", acciones.size());
        summary.put("accionesUrgentes", acciones.stream()
                .filter(a -> "HIGH".equals(a.getPrioridad()))
                .count());
        summary.put("totalExcepciones", excepciones.size());
        summary.put("excepcionesCriticas", excepciones.stream()
                .filter(e -> "CRITICAL".equals(e.getSeveridad()))
                .count());
        summary.put("materialesCriticos", inventario.stream()
                .filter(i -> "CRITICO".equals(i.getEstado()))
                .count());
        summary.put("materialesBajos", inventario.stream()
                .filter(i -> "BAJO".equals(i.getEstado()))
                .count());
        summary.put("valorTotalInventario", inventario.stream()
                .map(InventorySummaryDto::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return summary;
    }
}