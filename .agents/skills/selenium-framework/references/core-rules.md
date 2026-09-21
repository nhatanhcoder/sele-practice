# Selenium Framework Requirements

Tài liệu này tổng hợp và chuẩn hóa các yêu cầu xây dựng, refactor và review Selenium Framework.
Mọi mã nguồn khi xây dựng mới hoặc bảo trì đều phải tuân thủ nghiêm ngặt các quy tắc dưới đây.

## 1. Driver Management

- [ ] Áp dụng Factory Pattern (`DriverFactory`) để tạo WebDriver.
- [ ] `DriverFactory` hỗ trợ Chrome, Firefox và Edge (tách riêng method options cho từng browser).
- [ ] Hỗ trợ headless mode (ví dụ `--headless=new` cho Chrome/Edge, `-headless` cho Firefox).
- [ ] Hỗ trợ local execution và remote execution qua Selenium Grid (`RemoteWebDriver`).
- [ ] Dùng `DriverManager` để quản lý lifecycle của driver.
- [ ] Dùng `ThreadLocal<WebDriver>` để mỗi thread có một WebDriver riêng.
- [ ] Không dùng bare static WebDriver dùng chung giữa các thread.
- [ ] Singleton `DriverManager`, nếu có, không được làm các thread dùng chung browser session.
- [ ] Xử lý đúng lifecycle của `@BeforeMethod`, `@BeforeClass` và `@BeforeTest` tương thích với execution mode.
- [ ] Teardown dùng `alwaysRun = true`, luôn gọi `quit()` và giải phóng bộ nhớ bằng `ThreadLocal.remove()` (kể cả khi setup thất bại).
- [ ] Hỗ trợ TestNG parallel execution (`methods`, `classes`, `tests`) và Selenium Grid.
- [ ] Driver lifecycle phải tương thích với parallel mode được chọn (ví dụ: parallel `methods` đi kèm method-scoped lifecycle).

## 2. Configuration Management

- [ ] Đọc cấu hình theo từng environment (dev, staging, prod) và execution mode (local, remote).
- [ ] Hỗ trợ JSON, Gson typed mapping và validation sớm (fail-fast khi thiếu key quan trọng).
- [ ] Có cơ chế đọc `dev-env.properties` nếu dự án sử dụng properties.
- [ ] Không hardcode username, password, base URL, timeout hoặc remote URL trong code.
- [ ] Credential được lấy từ environment variable, system property hoặc file private không commit git.
- [ ] Hỗ trợ browser, headless, local/remote, remote URL, environment, base URL, timeout (implicit, explicit, pageLoad), window size, browser arguments và thread count.
- [ ] Mỗi file cấu hình private cần có một file mẫu đã loại bỏ thông tin nhạy cảm, ví dụ `dev.json.example`.
- [ ] Tài liệu hóa thứ tự ưu tiên khi override cấu hình (System Property / CLI `-D` > File Config Environment > Default).

## 3. Element Architecture

- [ ] Chuyển các method xử lý element từ `ElementUtils` sang `Element` hoặc Element Wrapper phù hợp (`BaseElement`, `WrappedElement`).
- [ ] Đổi tên `ElementUtils` theo đúng trách nhiệm còn lại sau khi chuyển method (thường đổi thành `DriverUtils`).
- [ ] Tạo Element Wrapper thay vì một Selenium Helper tổng hợp dồn mọi thứ vào một chỗ.
- [ ] Wrapper hỗ trợ click, input, explicit wait, scroll và kiểm tra trạng thái (isDisplayed, isEnabled).
- [ ] Không expose `WebElement` hoặc `List<WebElement>` ra Page Object.
- [ ] Page chỉ nhận dữ liệu đơn giản hoặc có kiểu rõ ràng như `String`, `List<String>`, `boolean` hoặc DTO.
- [ ] `DriverUtils` đóng gói việc resolve `By`, wait, scroll, navigation, refresh, screenshot và repeated extraction.

Luồng tương tác mong muốn:

```text
Test → Page Object → Element Wrapper → DriverUtils → DriverManager → WebDriver
```

## 4. Page Object

- [ ] Không gọi WebDriver trực tiếp trong Page Object.
- [ ] Page Object chỉ khai báo và làm việc với `By`; việc chuyển `By` thành `WebElement` nằm trong `DriverUtils` / `Element Wrapper`.
- [ ] Page không tự scroll hoặc tự duyệt từng `WebElement` thô để lấy text.
- [ ] Các locator được khai báo thành field bên ngoài method (ở đầu class).
- [ ] Locator là `private` nếu class con không cần truy cập.
- [ ] Không dùng enum để xử lý dynamic locator; dùng selector template (`String.format`) hoặc parameterized locator method.
- [ ] Tên locator mang ý nghĩa nghiệp vụ, không dùng hậu tố `Css` hoặc `Xpath` (ví dụ: dùng `LOGIN_BUTTON`, `USERNAME_INPUT`, không dùng `loginXpath`).
- [ ] Return `this` khi vẫn ở cùng page và return Page Object phù hợp khi chuyển trang (Fluent Pattern).
- [ ] Tránh constructor hoặc cross-page return tạo vòng lặp vô hạn (không khởi tạo Page mới trong constructor của Page).
- [ ] Không instantiate `BasePage` trực tiếp trong test; dùng Page Object class cụ thể.

Ví dụ refresh đúng trách nhiệm:

```java
public CartPage reload() {
    DriverUtils.refresh();
    return this;
}
```

## 5. Test Design

- [ ] Không gọi WebDriver, WebUI, DriverUtils hoặc Element Wrapper trực tiếp trong test body.
- [ ] Mọi thao tác UI trong test đều phải đi qua Page Object.
- [ ] `BaseTest` chỉ quản lý cấu hình, driver lifecycle và hạ tầng dùng chung.
- [ ] Không đặt scenario data hoặc test data trong `BaseTest`.
- [ ] Test data nằm trong fixture, DataProvider, JSON, DTO hoặc Builder pattern.
- [ ] Test có đầy đủ steps, actions, expected results và assertions rõ ràng.
- [ ] Không thêm logic thừa thãi không phục vụ trực tiếp scenario hoặc validation.
- [ ] Test độc lập (independent) và không phụ thuộc thứ tự chạy của test khác.
- [ ] Tên test class mô tả feature hoặc behavior, ví dụ `BuyItemTest`, không dùng `TestCase01` hay `TC_01`.
- [ ] Tất cả class, method, field và variable có tên rõ nghĩa, chuẩn quy ước đặt tên Java (camelCase, PascalCase).

## 6. Assertions

- [ ] Chuyển các hard assertion hiện tại sang soft assertion theo yêu cầu dự án.
- [ ] Có thể đặt assertion trong Page Object khi phù hợp để xác nhận action hoàn tất (self-validating action theo chiến lược Hybrid, ví dụ: assert item đã thêm vào giỏ thành công trước khi đi tiếp).
- [ ] Các checkpoint nghiệp vụ lớn của kịch bản (tổng tiền, danh sách hàng, xác nhận đơn) bắt buộc phải thể hiện rõ trong test method (`@Test`).
- [ ] Mỗi `SoftAssert` thuộc riêng một validation method hoặc test invocation.
- [ ] Không dùng static `SoftAssert` và không chia sẻ giữa các test.
- [ ] Luôn gọi `softAssert.assertAll()` tại đúng boundary (cuối test method hoặc cuối validation method).
- [ ] Không bao giờ nuốt (swallow) `AssertionError`.
- [ ] Khi prerequisite thất bại (ví dụ login fail) khiến thao tác tiếp theo không an toàn, phải dùng hard assert để dừng flow hợp lý ngay lập tức (fail-fast).

## 7. Reporting, Listener và Logging

- [ ] Tích hợp Allure Report.
- [ ] Dùng annotation `@Step` trên các method của Page Object / Action, không gọi `Allure.step()` trực tiếp trong test.
- [ ] Đăng ký TestNG Listener để tự động chụp screenshot khi test thất bại trước khi teardown driver, đính kèm vào đúng Allure invocation khi chạy song song. Không gọi hàm chụp ảnh thủ công trong từng test.
- [ ] Cung cấp logging có cấu trúc (Step, Debug, Info, Warn, Error), không in credential hay thông tin nhạy cảm ra log/báo cáo.
- [ ] Không commit thư mục `allure-results`, `target/` hoặc report tạm vào git repository.

## 8. Code, Build, and Delivery Conventions

- [ ] **Clean Code & Imports:** Tuyệt đối không dùng wildcard import (`import ...*`), kể cả static import. Xóa toàn bộ unused import và unused code.
- [ ] **Lombok:** Dùng Lombok có chọn lọc (`when-useful`) để giảm boilerplate (ưu tiên `@Getter`, `@Builder`, tránh blanket `@Data` có thể gây lộ secret qua `toString()`).
- [ ] **Maven & Dependencies:** Định nghĩa version bằng properties (ví dụ `${testng.version}`, `${selenium.version}`). File `testng.xml` đặt tại `src/test/resources/testng.xml` và cấu hình `maven-surefire-plugin` trỏ đúng file này.
- [ ] **Centralized Constants:** Tập trung URL, timeout, selector template, category vào lớp Constants hoặc Enum chuyên biệt.
- [ ] **README Documentation:** Hướng dẫn đầy đủ về JDK, Maven, cách chạy suite (`mvn test`), override config qua `-D`, chạy parallel và gen report Allure.
- [ ] **QA Checks:** Trước khi bàn giao, kiểm tra kỹ: không gọi trực tiếp driver/WebElement trong page, không hardcode locator inline, không có wildcard import, không leak session ThreadLocal, gọi đầy đủ `assertAll()`.
