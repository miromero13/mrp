package eccomerce.backend_eccomerce.indirectcost.config;

import eccomerce.backend_eccomerce.indirectcost.entity.CostCenter;
import eccomerce.backend_eccomerce.indirectcost.entity.IndirectCostCategory;
import eccomerce.backend_eccomerce.indirectcost.repository.CostCenterRepository;
import eccomerce.backend_eccomerce.indirectcost.repository.IndirectCostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IndirectCostDataInitializer implements CommandLineRunner {

    private final CostCenterRepository costCenterRepository;
    private final IndirectCostCategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (costCenterRepository.count() == 0) {
            List<String> centers = List.of("Planta Principal", "Almacén", "Administración", "Producción A");
            centers.forEach(name -> {
                CostCenter center = new CostCenter();
                center.setName(name);
                center.setDescription("Centro de costo para " + name);
                costCenterRepository.save(center);
            });
        }

        if (categoryRepository.count() == 0) {
            List<String> categories = List.of("Energía Eléctrica", "Agua Potable", "Gas Natural", "Internet y Telefonía");
            categories.forEach(name -> {
                IndirectCostCategory category = new IndirectCostCategory();
                category.setName(name);
                category.setDescription("Gastos correspondientes a " + name);
                categoryRepository.save(category);
            });
        }
    }
}
