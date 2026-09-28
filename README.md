# QuynhHill Coffee — Quản lý chuỗi cửa hàng cà phê

Ứng dụng web quản lý chuỗi cửa hàng cà phê: khách hàng xem thực đơn, đặt hàng online chọn cửa hàng nhận đơn; quản trị viên quản lý cửa hàng, danh mục, sản phẩm, đơn hàng và người dùng.

Xây dựng bằng **Spring Boot 2.7 + Spring Security + Spring Data JPA (Hibernate) + Thymeleaf + MySQL**, giao diện dựa trên Bootstrap 5 (vendor local, không phụ thuộc CDN ngoài).

## Tính năng

### Trang khách hàng (`/`)
- Trang chủ giới thiệu, món nổi bật, danh sách cửa hàng.
- Xem thực đơn theo danh mục/danh mục con, tìm kiếm sản phẩm, phân trang.
- Chi tiết sản phẩm, thêm vào giỏ hàng, cập nhật/xoá giỏ hàng.
- Đăng ký / đăng nhập tài khoản (Spring Security, mật khẩu mã hoá BCrypt), hoặc **đăng nhập bằng Google / Facebook** (tự tạo tài khoản ở lần đăng nhập đầu tiên — xem hướng dẫn cấu hình bên dưới).
- Thanh toán: nhập thông tin giao hàng, **chọn cửa hàng nhận đơn**, chọn **phương thức thanh toán** (COD hoặc chuyển khoản ngân hàng), xác nhận đặt hàng.

### Trang quản trị (`/admin/**`, chỉ tài khoản có quyền `ROLE_ADMIN`)
- **Dashboard**: tổng doanh thu, tổng đơn hàng, tổng cửa hàng, tổng sản phẩm, biểu đồ doanh thu theo cửa hàng, danh sách đơn hàng gần đây.
- **Cửa hàng**: thêm/sửa/ngừng hoạt động/xoá chi nhánh.
- **Danh mục**: thêm/xoá danh mục và danh mục con.
- **Sản phẩm**: thêm/sửa/xoá sản phẩm theo danh mục con.
- **Đơn hàng**: xem danh sách (lọc theo cửa hàng/trạng thái), xem chi tiết, cập nhật trạng thái đơn.
- **Người dùng**: tìm kiếm, cấp/gỡ quyền admin, xoá tài khoản.

## Yêu cầu

- JDK 11 trở lên (dự án dùng Maven Wrapper, không cần cài Maven riêng).
- MySQL 8 — chạy sẵn trên máy **hoặc** dùng Docker (xem bên dưới).

## Chạy dự án

### Cách 1 — Có sẵn MySQL trên máy (profile `dev`)

`src/main/resources/application-dev.yml` mặc định trỏ tới `jdbc:mysql://localhost:3306/demohibernate` (user `root`, không mật khẩu). Tạo database trống tên `demohibernate` rồi chạy:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Cách 2 — Dùng MySQL trong Docker, chạy app ngoài host

```bash
docker compose up -d mysql
```

MySQL sẽ chạy ở container, expose ra host qua cổng **3307** (để không đụng MySQL cài sẵn trên máy nếu có). Chạy app trỏ vào cổng đó:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://localhost:3307/demohibernate?useSSL=false&allowPublicKeyRetrieval=true --spring.datasource.username=root --spring.datasource.password=root"
```

### Cách 3 — Docker Compose full stack (app + MySQL cùng chạy trong container)

```bash
./mvnw clean package -DskipTests
docker compose up --build
```

App dùng profile `docker` (`application-docker.yml`, nối tới MySQL qua network nội bộ của Docker), truy cập tại `http://localhost:8899`.

## Sau khi chạy lần đầu

Ứng dụng tự seed dữ liệu mẫu khi khởi động lần đầu (bảng `role` trống) — xem `config/DataSeeder.java`: 2 vai trò (`ROLE_USER`, `ROLE_ADMIN`), 1 tài khoản quản trị, 3 cửa hàng, danh mục + thực đơn cà phê mẫu.

Tài khoản quản trị mặc định:

```
Tên đăng nhập: admin
Mật khẩu:      Admin@123
```

Truy cập `http://localhost:8899` (trang khách hàng) hoặc `http://localhost:8899/admin` (trang quản trị, cần đăng nhập tài khoản admin ở trên).

## Đăng nhập Google / Facebook

Mặc định (chưa cấu hình gì) hai nút này **tự ẩn** ở trang đăng nhập và ứng dụng chạy bình thường. Để bật, cần tự tạo ứng dụng OAuth trên chính tài khoản Google/Facebook của bạn (không thể làm thay), rồi truyền Client ID/Secret qua biến môi trường.

### Google

1. Vào [Google Cloud Console](https://console.cloud.google.com/apis/credentials) → tạo project mới (nếu chưa có) → **Create Credentials → OAuth client ID**.
2. Application type: **Web application**.
3. Authorized redirect URI: `http://localhost:8899/login/oauth2/code/google` (đổi `localhost:8899` thành domain thật khi deploy).
4. Copy **Client ID** và **Client secret**.

### Facebook

1. Vào [Meta for Developers](https://developers.facebook.com/apps/) → **Create App** → chọn loại "Consumer" → thêm sản phẩm **Facebook Login**.
2. Trong Facebook Login → Settings, thêm Valid OAuth Redirect URI: `http://localhost:8899/login/oauth2/code/facebook`.
3. Copy **App ID** và **App secret** (mục Settings → Basic).
4. Lưu ý: ở chế độ phát triển (chưa submit App Review), chỉ tài khoản được thêm vào vai trò Tester/Developer của app mới đăng nhập được và mới nhận được quyền `email`.

### Chạy với thông tin đã có

```bash
export GOOGLE_CLIENT_ID=xxx
export GOOGLE_CLIENT_SECRET=xxx
export FACEBOOK_CLIENT_ID=xxx
export FACEBOOK_CLIENT_SECRET=xxx
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Chỉ cấu hình Google hoặc chỉ Facebook cũng được — nút nào chưa có Client ID/Secret sẽ tự ẩn. Tài khoản đăng nhập lần đầu qua Google/Facebook được tạo tự động với `ROLE_USER`, dùng email làm tên đăng nhập; trang **Người dùng** trong admin có cột "Nguồn" để phân biệt tài khoản LOCAL/GOOGLE/FACEBOOK.

## Cấu trúc chính

```
src/main/java/com/quynhtadinh/finalexample/
  config/           WebSecurityConfig, DataSeeder, LocaleConfig
  controller/       WebController (khách hàng), UserController (auth/trang chủ)
  controller/admin/ Các controller quản trị (Dashboard, Store, Category, Product, Order, User)
  entity/           Product, Category, SubCategory, Store, Order, OrderDetail, User, Role...
  repository/       Spring Data JPA repositories
  security/         UserDetailsServiceImpl, SecurityService
  service/          UserService (+ service/impl)

src/main/resources/
  templates/        Trang khách hàng (index, listProduct, detail, checkout...)
  templates/admin/  Trang quản trị (dùng chung fragment admin/admin-layout.html)
  templates/fragments/ Nav + footer dùng chung cho toàn bộ trang khách hàng
  static/css/coffee-theme.css  Theme màu cà phê dùng chung storefront + admin
  static/images/coffee/        Logo/ảnh minh hoạ SVG tự thiết kế
```

## Ghi chú phạm vi

- Phương thức thanh toán (COD / chuyển khoản) chỉ là lựa chọn khai báo, **chưa tích hợp cổng thanh toán thật** (VNPay, Momo...).
- Menu dùng chung cho toàn chuỗi, chưa quản lý tồn kho riêng theo từng cửa hàng.
- Chưa có tài khoản quản lý riêng cho từng chi nhánh (manager theo cửa hàng).
