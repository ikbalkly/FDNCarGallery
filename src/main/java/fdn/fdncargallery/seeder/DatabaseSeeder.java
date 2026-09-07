package fdn.fdncargallery.seeder;

import fdn.fdncargallery.entity.Address;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.Brand;
import fdn.fdncargallery.entity.Model;
import fdn.fdncargallery.entity.SystemAdmin;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IBrandRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.IModelRepository;
import fdn.fdncargallery.repository.ISystemAdminRepository;
import fdn.fdncargallery.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private static final String SYSTEM_ADMIN_EMAIL = "admin@fdncargallery.com";
    private static final String BRANCH_ADMIN_EMAIL = "sube.admin@fdncargallery.com";

    private final IEmployeeRepository employeeRepository;
    private final ISystemAdminRepository systemAdminRepository;
    private final IBranchRepository branchRepository;
    private final IBrandRepository brandRepository;
    private final IModelRepository modelRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;

    @Value("${fdn.admin.password:}")
    private String configuredAdminPassword;

    @Value("${fdn.branch.admin.password:}")
    private String configuredBranchAdminPassword;

    @Transactional
    @Override
    public void run(String... args) {

        if (employeeRepository.existsByEmail(SYSTEM_ADMIN_EMAIL)) {
            return;
        }
        log.info("Sistem ilk kez başlatılıyor... Kurulum hesapları oluşturuluyor.");

        seedSystemAdmin();
        seedBranchAdmin();
        seedBrandsAndModels();
    }

    private void seedBrandsAndModels() {

        seedBrand("Ford", "Focus", "Fiesta", "Kuga", "Puma");
        seedBrand("Volkswagen", "Golf", "Passat", "Polo", "Tiguan");
        seedBrand("Renault", "Clio", "Megane", "Captur", "Taliant");
        seedBrand("Toyota", "Corolla", "Yaris", "C-HR", "RAV4");
        seedBrand("Fiat", "Egea", "500", "Panda", "Doblo");
        seedBrand("BMW", "3 Serisi", "5 Serisi", "X1", "X3");
        seedBrand("Mercedes-Benz", "A-Serisi", "C-Serisi", "E-Serisi", "GLC");
        seedBrand("Hyundai", "i20", "i30", "Tucson", "Bayon");

        log.info("Marka/model referans verisi yüklendi.");
    }

    private void seedBrand(String brandName, String... modelNames) {

        Brand brand = new Brand();
        brand.setBrandName(brandName);
        Brand savedBrand = brandRepository.save(brand);

        for (String modelName : modelNames) {
            Model model = new Model();
            model.setModelName(modelName);
            model.setBrand(savedBrand);
            modelRepository.save(model);
        }
    }

    private void seedSystemAdmin() {

        String password = requiredPassword(configuredAdminPassword, "FDN_ADMIN_PASSWORD");

        Branch itBranch = new Branch();
        itBranch.setBranchName("IT Merkez");
        itBranch.setAddress(systemAddress("Sistem Yönetim Merkezi"));
        Branch savedBranch = branchRepository.save(itBranch);

        SystemAdmin superAdmin = new SystemAdmin();
        superAdmin.setName("System");
        superAdmin.setSurname("Administrator");
        superAdmin.setIdentityNumber("10000000001");
        superAdmin.setPhoneNumber("+900000000000");
        superAdmin.setBaseSalary(BigDecimal.ZERO);
        superAdmin.setBranch(savedBranch);
        superAdmin.setAddress(systemAddress("Kurucu Admin Adresi"));

        String username = usernameGenerator.generateUnique(
                superAdmin.getName(), superAdmin.getSurname(), Role.SUPER_ADMIN, savedBranch.getId());

        superAdmin.setUsername(username);
        superAdmin.setEmail(SYSTEM_ADMIN_EMAIL);
        superAdmin.setPassword(passwordEncoder.encode(password));
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setFirstLogin(false);

        systemAdminRepository.save(superAdmin);

        log.info("Sistem Yöneticisi kuruldu. Şube: {}, Username: {}", savedBranch.getBranchName(), username);
    }

    private void seedBranchAdmin() {

        Branch galleryBranch = new Branch();
        galleryBranch.setBranchName("Cyberpark Oto Galeri");
        galleryBranch.setAddress(cyberparkAddress());
        Branch savedBranch = branchRepository.save(galleryBranch);

        SystemAdmin branchAdmin = new SystemAdmin();
        branchAdmin.setName("Branch");
        branchAdmin.setSurname("Admin");
        branchAdmin.setIdentityNumber("10000000002");
        branchAdmin.setPhoneNumber("+905000000000");
        branchAdmin.setBaseSalary(BigDecimal.ZERO);
        branchAdmin.setBranch(savedBranch);
        branchAdmin.setAddress(cyberparkAddress());

        String username = usernameGenerator.generateUnique(
                branchAdmin.getName(), branchAdmin.getSurname(), Role.BRANCH_ADMIN, savedBranch.getId());

        String temporaryPassword = requiredPassword(configuredBranchAdminPassword, "FDN_BRANCH_ADMIN_PASSWORD");

        branchAdmin.setUsername(username);
        branchAdmin.setEmail(BRANCH_ADMIN_EMAIL);
        branchAdmin.setPassword(passwordEncoder.encode(temporaryPassword));
        branchAdmin.setRole(Role.BRANCH_ADMIN);
        branchAdmin.setFirstLogin(true);

        systemAdminRepository.save(branchAdmin);

        log.info("Şube Yöneticisi kuruldu. Şube: {}, Username: {}", savedBranch.getBranchName(), username);
        log.warn("İlk girişte /api/auth/change-password ile değiştirilmesi ZORUNLUDUR.");
    }

    private String requiredPassword(String configured, String envName) {
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException(
                    envName + " tanımlı değil. Kurulum hesapları bu değişken olmadan oluşturulamaz.");
        }
        return configured;
    }

    private Address systemAddress(String buildingName) {
        Address address = new Address();
        address.setCity("Sistem");
        address.setDistrict("Merkez");
        address.setNeighborhood("Merkez");
        address.setStreet("IT Bilişim");
        address.setBuildingName(buildingName);
        return address;
    }

    private Address cyberparkAddress() {
        Address address = new Address();
        address.setCity("Ankara");
        address.setDistrict("Çankaya");
        address.setNeighborhood("Üniversiteler");
        address.setStreet("1596. Cadde");
        address.setBuildingName("Cyberpark");
        address.setBuildingNo("6");
        address.setZipCode("06800");
        return address;
    }
}
