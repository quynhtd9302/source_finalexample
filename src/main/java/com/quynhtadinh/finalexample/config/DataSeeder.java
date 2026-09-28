package com.quynhtadinh.finalexample.config;

import java.sql.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.quynhtadinh.finalexample.entity.Category;
import com.quynhtadinh.finalexample.entity.Product;
import com.quynhtadinh.finalexample.entity.Role;
import com.quynhtadinh.finalexample.entity.StatusCategory;
import com.quynhtadinh.finalexample.entity.StatusOrder;
import com.quynhtadinh.finalexample.entity.StatusProduct;
import com.quynhtadinh.finalexample.entity.StatusSubCategory;
import com.quynhtadinh.finalexample.entity.Store;
import com.quynhtadinh.finalexample.entity.SubCategory;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.CategoryRepository;
import com.quynhtadinh.finalexample.repository.ProductRepository;
import com.quynhtadinh.finalexample.repository.RoleRepository;
import com.quynhtadinh.finalexample.repository.StatusCategoryRepository;
import com.quynhtadinh.finalexample.repository.StatusOrderRepository;
import com.quynhtadinh.finalexample.repository.StatusProductRepository;
import com.quynhtadinh.finalexample.repository.StatusSubCategoryRepository;
import com.quynhtadinh.finalexample.repository.StoreRepository;
import com.quynhtadinh.finalexample.repository.SubCategoryRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;

/**
 * Seeds roles, an admin account, sample stores and a coffee-shop menu on first boot.
 * Guarded by roleRepository.count() so it only ever runs once against an empty database.
 */
@Component
public class DataSeeder implements CommandLineRunner {

	@Autowired private RoleRepository roleRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private StoreRepository storeRepository;
	@Autowired private CategoryRepository categoryRepository;
	@Autowired private SubCategoryRepository subCategoryRepository;
	@Autowired private ProductRepository productRepository;
	@Autowired private StatusCategoryRepository statusCategoryRepository;
	@Autowired private StatusSubCategoryRepository statusSubCategoryRepository;
	@Autowired private StatusProductRepository statusProductRepository;
	@Autowired private StatusOrderRepository statusOrderRepository;

	@Override
	public void run(String... args) {
		if (roleRepository.count() > 0) {
			return;
		}

		Role roleUser = roleRepository.save(newRole("ROLE_USER"));
		Role roleAdmin = roleRepository.save(newRole("ROLE_ADMIN"));

		User admin = new User();
		admin.setUsername("admin");
		admin.setEmail("admin@chaincoffee.vn");
		admin.setPassword(passwordEncoder.encode("Admin@123"));
		HashSet<Role> adminRoles = new HashSet<>();
		adminRoles.add(roleUser);
		adminRoles.add(roleAdmin);
		admin.setRoles(adminRoles);
		userRepository.save(admin);

		StatusOrder pending = statusOrderRepository.save(newStatusOrder("Chờ xác nhận"));
		statusOrderRepository.save(newStatusOrder("Đang pha chế"));
		statusOrderRepository.save(newStatusOrder("Đang giao"));
		statusOrderRepository.save(newStatusOrder("Hoàn thành"));
		statusOrderRepository.save(newStatusOrder("Đã huỷ"));

		StatusCategory activeCategory = statusCategoryRepository.save(newStatusCategory("Đang bán"));
		StatusSubCategory activeSubCategory = statusSubCategoryRepository.save(newStatusSubCategory("Đang bán"));
		StatusProduct inStock = statusProductRepository.save(newStatusProduct("Còn hàng"));
		statusProductRepository.save(newStatusProduct("Hết hàng"));

		seedStores();
		seedMenu(activeCategory, activeSubCategory, inStock);
	}

	private void seedStores() {
		storeRepository.save(newStore("HN01", "Chain Coffee - Hoàn Kiếm",
				"8D Phố Hàm Long, Hoàn Kiếm, Hà Nội", "024 3934 1234",
				"hoankiem@chaincoffee.vn", "07:00", "22:00", "/images/coffee/store-1.svg"));
		storeRepository.save(newStore("HN02", "Chain Coffee - Cầu Giấy",
				"150 Xuân Thuỷ, Cầu Giấy, Hà Nội", "024 3768 5678",
				"caugiay@chaincoffee.vn", "07:00", "22:30", "/images/coffee/store-2.svg"));
		storeRepository.save(newStore("DN01", "Chain Coffee - Đà Nẵng",
				"22 Bạch Đằng, Hải Châu, Đà Nẵng", "0236 3822 9999",
				"danang@chaincoffee.vn", "06:30", "22:00", "/images/coffee/store-3.svg"));
	}

	private void seedMenu(StatusCategory statusCategory, StatusSubCategory statusSubCategory, StatusProduct inStock) {
		SubCategory phin = seedCategoryTree("Cà phê", "Cà phê phin", statusCategory, statusSubCategory);
		SubCategory phaMay = subCategoryOf("Cà phê pha máy", phin.getCategory(), statusSubCategory);
		SubCategory traTraiCay = seedCategoryTree("Trà", "Trà trái cây", statusCategory, statusSubCategory);
		SubCategory traSua = subCategoryOf("Trà sữa", traTraiCay.getCategory(), statusSubCategory);
		SubCategory freeze = seedCategoryTree("Đá xay", "Đá xay", statusCategory, statusSubCategory);
		SubCategory banhNgot = seedCategoryTree("Bánh ngọt", "Bánh ngọt", statusCategory, statusSubCategory);

		seedProduct("CF001", "Cà phê đen đá", 25000, phin, inStock, "/images/coffee/products/ca-phe-den-da.jpg",
				"Cà phê phin truyền thống, đậm đà, phục vụ cùng đá.");
		seedProduct("CF002", "Cà phê sữa đá", 29000, phin, inStock, "/images/coffee/products/ca-phe-sua-da.jpg",
				"Cà phê phin hoà cùng sữa đặc béo ngậy.");
		seedProduct("CF003", "Bạc xỉu", 32000, phin, inStock, "/images/coffee/products/bac-xiu.jpg",
				"Nhiều sữa, ít cà phê, vị ngọt dịu dễ uống.");
		seedProduct("CF004", "Espresso", 35000, phaMay, inStock, "/images/coffee/products/espresso.jpg",
				"Chiết xuất từ máy pha chuyên nghiệp, đậm vị nguyên bản.");
		seedProduct("CF005", "Americano", 39000, phaMay, inStock, "/images/coffee/products/americano.jpg",
				"Espresso pha loãng cùng nước nóng hoặc đá.");
		seedProduct("CF006", "Cappuccino", 45000, phaMay, inStock, "/images/coffee/products/cappuccino.jpg",
				"Espresso, sữa nóng và lớp bọt sữa dày mịn.");
		seedProduct("CF007", "Latte", 45000, phaMay, inStock, "/images/coffee/products/latte.jpg",
				"Espresso hoà quyện cùng sữa tươi đánh bông.");
		seedProduct("TR001", "Trà đào cam sả", 45000, traTraiCay, inStock, "/images/coffee/products/tra-dao-cam-sa.jpg",
				"Trà trái cây thanh mát với đào, cam và sả.");
		seedProduct("TR002", "Trà vải", 42000, traTraiCay, inStock, "/images/coffee/products/tra-vai.jpg",
				"Trà trái cây vị vải ngọt thanh, thơm mát.");
		seedProduct("TR003", "Trà sữa trân châu", 40000, traSua, inStock, "/images/coffee/products/tra-sua-tran-chau.jpg",
				"Trà sữa béo thơm cùng trân châu đường đen.");
		seedProduct("FZ001", "Freeze việt quất", 49000, freeze, inStock, "/images/coffee/products/freeze-viet-quat.jpg",
				"Đá xay việt quất chua ngọt, mát lạnh sảng khoái.");
		seedProduct("BK001", "Bánh croissant bơ", 35000, banhNgot, inStock, "/images/coffee/products/banh-croissant-bo.jpg",
				"Bánh sừng bò bơ Pháp, lớp vỏ giòn xốp nhiều lớp.");
	}

	private SubCategory seedCategoryTree(String categoryName, String subCategoryName,
			StatusCategory statusCategory, StatusSubCategory statusSubCategory) {
		Category category = new Category();
		category.setCategoryName(categoryName);
		category.setStatus(statusCategory);
		category = categoryRepository.save(category);
		return subCategoryOf(subCategoryName, category, statusSubCategory);
	}

	private SubCategory subCategoryOf(String name, Category category, StatusSubCategory statusSubCategory) {
		SubCategory subCategory = new SubCategory();
		subCategory.setSubCategoryName(name);
		subCategory.setCategory(category);
		subCategory.setStatus(statusSubCategory);
		return subCategoryRepository.save(subCategory);
	}

	private void seedProduct(String code, String name, double price, SubCategory subCategory,
			StatusProduct status, String imageUrl, String description) {
		Product product = new Product();
		product.setCode(code);
		product.setName(name);
		product.setPrice(price);
		product.setQuantity(100);
		product.setDescription(description);
		product.setImageUrl(imageUrl);
		product.setSubCategory(subCategory);
		product.setStatus(status);
		product.setCreatedDate(new Date(System.currentTimeMillis()));
		productRepository.save(product);
	}

	private Role newRole(String name) {
		Role role = new Role();
		role.setName(name);
		return role;
	}

	private Store newStore(String code, String name, String address, String phone, String email,
			String openTime, String closeTime, String imageUrl) {
		Store store = new Store();
		store.setCode(code);
		store.setName(name);
		store.setAddress(address);
		store.setPhone(phone);
		store.setEmail(email);
		store.setOpenTime(openTime);
		store.setCloseTime(closeTime);
		store.setImageUrl(imageUrl);
		store.setActive(true);
		store.setCreatedDate(new Date(System.currentTimeMillis()));
		return store;
	}

	private StatusOrder newStatusOrder(String status) {
		StatusOrder statusOrder = new StatusOrder();
		statusOrder.setStatus(status);
		return statusOrder;
	}

	private StatusCategory newStatusCategory(String status) {
		StatusCategory statusCategory = new StatusCategory();
		statusCategory.setStatus(status);
		return statusCategory;
	}

	private StatusSubCategory newStatusSubCategory(String status) {
		StatusSubCategory statusSubCategory = new StatusSubCategory();
		statusSubCategory.setStatus(status);
		return statusSubCategory;
	}

	private StatusProduct newStatusProduct(String status) {
		StatusProduct statusProduct = new StatusProduct();
		statusProduct.setStatus(status);
		return statusProduct;
	}
}
