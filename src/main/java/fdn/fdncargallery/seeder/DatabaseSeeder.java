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

        seedBrand("Ford", "Focus", "Fiesta", "Kuga", "Puma", "Transit Courier", "Ranger", "Mondeo", "C-Max", "EcoSport", "Tourneo Courier", "Tourneo Connect", "Mustang", "Mustang Mach-E");
        seedBrand("Volkswagen", "Golf", "Passat", "Polo", "Tiguan", "T-Roc", "Caddy", "Jetta", "Arteon", "T-Cross", "Taigo", "Touareg", "Transporter", "Amarok", "ID.4");
        seedBrand("Renault", "Clio", "Megane", "Captur", "Taliant", "Austral", "Fluence", "Symbol", "Kadjar", "Talisman", "Laguna", "Kangoo", "Koleos", "Zoe");
        seedBrand("Toyota", "Corolla", "Yaris", "C-HR", "RAV4", "Hilux", "Auris", "Avensis", "Camry", "Yaris Cross", "Corolla Cross", "Proace City", "Land Cruiser");
        seedBrand("Fiat", "Egea", "500", "Panda", "Doblo", "Egea Cross", "Fiorino", "Linea", "Albea", "Palio", "Punto", "Bravo", "500X", "500L", "Qubo", "600");
        seedBrand("BMW", "3 Serisi", "5 Serisi", "X1", "X3", "1 Serisi", "X5", "2 Serisi", "4 Serisi", "7 Serisi", "X2", "X4", "X6", "X7", "i4", "iX");
        seedBrand("Mercedes-Benz", "A-Serisi", "C-Serisi", "E-Serisi", "GLC", "CLA", "GLA", "B-Serisi", "S-Serisi", "CLS", "GLB", "GLE", "GLS", "G-Serisi", "V-Serisi", "Vito", "EQA", "EQE", "EQS");
        seedBrand("Hyundai", "i20", "i30", "Tucson", "Bayon", "i10", "Accent Blue", "Elantra", "Accent", "Accent Era", "Getz", "Kona", "Santa Fe", "Ioniq 5", "Ioniq 6");
        seedBrand("Tesla", "Cybertruck", "Model Y", "Model 3", "Model S", "Model X");
        seedBrand("Dacia", "Sandero", "Sandero Stepway", "Logan", "Duster", "Jogger", "Spring", "Lodgy", "Dokker", "Bigster");
        seedBrand("Honda", "Civic", "City", "Jazz", "HR-V", "CR-V", "Accord", "ZR-V", "e:Ny1");
        seedBrand("Seat", "Ibiza", "Leon", "Arona", "Ateca", "Toledo", "Altea", "Tarraco");
        seedBrand("Cupra", "Formentor", "Leon", "Born", "Ateca", "Tavascan", "Terramar");
        seedBrand("Mazda", "2", "3", "CX-3", "CX-30", "CX-5", "6", "MX-5", "CX-60");
        seedBrand("Volvo", "S60", "S90", "XC40", "XC60", "XC90", "S40", "V40", "V60", "C40", "EX30", "EX90");
        seedBrand("Chery", "Tiggo 7 Pro", "Tiggo 8 Pro", "Omoda 5");
        seedBrand("Nissan", "Micra", "Juke", "Qashqai", "X-Trail", "Navara", "Note", "Pulsar", "Almera", "Leaf");
        seedBrand("Suzuki", "Swift", "Vitara", "S-Cross", "Jimny", "SX4", "Ignis", "Grand Vitara");
        seedBrand("Citroen", "C3", "C4", "C5 Aircross", "C-Elysee", "Berlingo", "C1", "C3 Aircross", "C4 X", "C4 Picasso", "C5", "Nemo");
        seedBrand("Kia", "Picanto", "Rio", "Ceed", "Stonic", "Sportage", "Niro", "Cerato", "Venga", "XCeed", "Optima", "Sorento", "EV6");
        seedBrand("Jeep", "Renegade", "Compass", "Wrangler", "Grand Cherokee", "Cherokee", "Avenger");
        seedBrand("BYD", "Atto 3", "Dolphin", "Seal", "Seal U", "Han", "Atto 2", "Sealion 7");
        seedBrand("MINI", "Cooper", "Countryman", "Clubman", "Cabrio", "Paceman", "Aceman");
        seedBrand("Audi", "A3", "A4", "A6", "Q2", "Q3", "Q5", "A1", "A5", "A7", "A8", "Q7", "Q8", "Q4 e-tron");
        seedBrand("Togg", "T10X", "T10F");
        seedBrand("Opel", "Corsa", "Astra", "Insignia", "Mokka", "Grandland", "Vectra", "Meriva", "Zafira", "Crossland", "Combo");
        seedBrand("Land Rover", "Defender", "Discovery Sport", "Range Rover", "Range Rover Sport", "Range Rover Evoque", "Range Rover Velar", "Discovery", "Freelander");
        seedBrand("Skoda", "Fabia", "Scala", "Octavia", "Superb", "Kamiq", "Karoq", "Kodiaq", "Rapid", "Yeti", "Roomster", "Enyaq", "Elroq");
        seedBrand("Peugeot", "208", "308", "2008", "3008", "5008", "Rifter", "206", "207", "301", "407", "408", "508", "Partner");
        seedBrand("Porsche", "911", "Cayenne", "Macan", "Panamera", "Taycan", "718");
        seedBrand("Alfa Romeo", "Giulietta", "Giulia", "Stelvio", "Tonale");
        seedBrand("Chevrolet", "Aveo", "Cruze", "Lacetti", "Spark", "Captiva");
        seedBrand("DS", "DS 3", "DS 4", "DS 7");
        seedBrand("Lexus", "CT", "ES", "NX", "RX", "UX", "LBX");
        seedBrand("MG", "MG3", "MG4", "ZS", "HS");
        seedBrand("Mitsubishi", "L200", "ASX", "Lancer", "Colt", "Space Star", "Outlander");
        seedBrand("Subaru", "Forester", "XV", "Impreza", "Outback");
        seedBrand("Tofaş", "Şahin", "Doğan", "Kartal", "Serçe");

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

        String username = usernameGenerator.generateUnique(superAdmin.getName(), superAdmin.getSurname(), Role.SUPER_ADMIN, savedBranch.getId());

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
        galleryBranch.setCommissionRate(new BigDecimal("0.200"));
        galleryBranch.setMonthlySalesTarget(15);
        galleryBranch.setTargetBonusPerCar(new BigDecimal("1250.00"));
        Branch savedBranch = branchRepository.save(galleryBranch);

        SystemAdmin branchAdmin = new SystemAdmin();
        branchAdmin.setName("Branch");
        branchAdmin.setSurname("Admin");
        branchAdmin.setIdentityNumber("10000000002");
        branchAdmin.setPhoneNumber("+905000000000");
        branchAdmin.setBaseSalary(BigDecimal.ZERO);
        branchAdmin.setBranch(savedBranch);
        branchAdmin.setAddress(cyberparkAddress());

        String username = usernameGenerator.generateUnique(branchAdmin.getName(), branchAdmin.getSurname(), Role.BRANCH_ADMIN, savedBranch.getId());

        String temporaryPassword = requiredPassword(configuredBranchAdminPassword, "FDN_BRANCH_ADMIN_PASSWORD");

        branchAdmin.setUsername(username);
        branchAdmin.setEmail(BRANCH_ADMIN_EMAIL);
        branchAdmin.assignTemporaryPassword(passwordEncoder.encode(temporaryPassword));
        branchAdmin.setRole(Role.BRANCH_ADMIN);

        systemAdminRepository.save(branchAdmin);

        log.info("Şube Yöneticisi kuruldu. Şube: {}, Username: {}", savedBranch.getBranchName(), username);
        log.warn("İlk girişte /api/auth/change-password ile değiştirilmesi ZORUNLUDUR.");
    }

    private String requiredPassword(String configured, String envName) {
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException(envName + " tanımlı değil. Kurulum hesapları bu değişken olmadan oluşturulamaz.");
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
