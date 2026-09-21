# Selenium Framework Rules & Guidelines

Tất cả các thay đổi và triển khai mã nguồn liên quan đến Selenium Framework trong workspace phải tuân thủ nghiêm ngặt các quy chuẩn sau:

## 1. Thread Safety & WebDriver Lifecycle
- Bắt buộc dùng `ThreadLocal<WebDriver>`, tuyệt đối không dùng shared bare static WebDriver.
- Quản lý lifecycle rõ ràng (`BeforeMethod`/`BeforeClass`/`BeforeTest`).
- Luôn dọn dẹp driver trong teardown (`alwaysRun = true`): gọi `quit()` và `remove()` ThreadLocal value kể cả khi setup fail.
- Tuyệt đối không commit credentials/secrets, và **không commit thư mục `allure-results`** lên repository.

## 2. Page Object & Element Architecture
- **Không gọi WebUI / WebDriver trực tiếp trong Test body**: Mọi thao tác UI phải đi qua các method của Page Object.
- **Không gom vào Selenium Helper chung chung**: Sử dụng Element Wrapper riêng biệt (ví dụ: `BaseElement`, `WrappedElement`) để đóng gói các cơ chế wait, scroll, click, sendKeys.
- **Khai báo By Locator**: Khai báo bên ngoài method dưới dạng class fields; đặt tên mang tính ngữ nghĩa (semantic naming), **không chứa hậu tố CSS hoặc XPath** (ví dụ: `LOGIN_BUTTON`, không dùng `loginBtnCss` hay `loginXpath`).
- **Encapsulate trong DriverUtils**: Page Object không tự resolve raw `WebElement`, không tự scroll hay duyệt từng row rồi gọi `getText()`. Đưa phần cơ chế đó vào `DriverUtils`, Page Object chỉ nhận kết quả đã định kiểu như `List<String>`, `boolean` hoặc DTO.
- **Ví dụ**: `CartPage.reload()` gọi `DriverUtils.refresh()` và return `this` (không gọi trực tiếp `DriverManager.getDriver().navigate().refresh()`).
- **Page Navigation**: Return `this` nếu tiếp tục ở lại trang, return `NewPage` khi chuyển trang (Fluent pattern). Cẩn thận tránh vòng lặp đệ quy vô hạn trong Constructor.

## 3. Chiến Lược Assertion (Hybrid)
- **Trong Page Object (Action-level validation)**: Assert ngay trong method của Page cho các thao tác lặp lại hoặc kiểm tra hoàn tất bước thao tác (ví dụ: `addProductToCart` -> assert ngay sản phẩm đã được thêm thành công) để fail-fast tại bước cơ sở.
- **Ngoài Test Case (`@Test` - Major Checkpoints)**: Bắt buộc assert các checkpoint nghiệp vụ lớn của kịch bản ngoài Test Case (kiểm tra giỏ hàng, thông tin thanh toán, đơn hàng, tổng tiền...).
- **Assert Style**: Hard assert cho prerequisite để fail-fast; Soft assert cho các checkpoint độc lập và bắt buộc gọi `softAssert.assertAll()` ở ranh giới method/test. Không nuốt `AssertionError`.

## 4. Hằng Số & Cấu Hình
- **Hằng số tập trung**: Khai báo hằng số, categories, URLs, timeouts tại một nơi duy nhất (Enums hoặc Constants classes), không hardcode rải rác.
- **File cấu hình mẫu**: Mọi file JSON cấu hình chạy môi trường phải có 1 file template mẫu (sanitized, ví dụ `.example`) để người khác biết format cần chuẩn bị.

## 5. TestNG Listener & Báo Cáo Allure
- Sử dụng TestNG Listener để tự động chụp màn hình khi test FAIL và đính kèm vào Allure report trước khi driver teardown. Không gọi thủ công `captureFailureScreenshot()` trong từng test case.
- Sử dụng annotation `@Step` thay vì gọi `Allure.step()`.
- Log có cấu trúc theo các cấp độ (Step, Debug, Info, Warn, Error) qua SLF4J.

## 6. Tiêu Chuẩn Clean Code & Maven
- Tuyệt đối không dùng wildcard imports (`import ...*`), kể cả static imports.
- Xóa bỏ mọi code thừa và import không sử dụng (unused code/imports).
- Comment tối giản: chỉ giải thích "tại sao" cho bug, issue hoặc logic đặc biệt; không comment mô tả hiển nhiên code làm gì.
- Dùng Lombok khi phù hợp (`when-useful`) để giảm boilerplate, tránh lạm dụng blanket `@Data` trên các model nhạy cảm.
- Maven: Dùng `${testng.version}` trong `pom.xml`, không hardcode version. Đặt file testng suite tại `src/test/resources/testng.xml` và cấu hình Surefire plugin trỏ vào đường dẫn này.
- README: Bổ sung đầy đủ hướng dẫn setup, lệnh chạy suite, và ghi rõ version cụ thể của Java/JDK, Maven, Selenium, TestNG và WebDriver/trình duyệt.
- Git/PR Workflow: Khi cập nhật code của PR nào, push thay đổi trực tiếp lên nhánh của PR đó để dễ tracking và review.
