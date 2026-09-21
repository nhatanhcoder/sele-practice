# Cấu hình cá nhân cho skill code Selenium

Sửa cột **Giá trị đang dùng** để thay đổi cách skill viết code. Đây là nguồn duy nhất chứa lựa chọn đang áp dụng; không cần JSON hoặc script.

Ví dụ driver theo class và assertion ngay trong method page là lựa chọn được hỗ trợ, chưa phải mặc định người dùng đã chọn. `project` giữ thiết kế nhất quán hiện có nếu thiết kế đó đúng.

## Lựa chọn đang áp dụng

| Thiết lập | Giá trị đang dùng | Giá trị hỗ trợ | Fallback/cách áp dụng |
| --- | --- | --- | --- |
| driver.lifecycle | `project` | project, method, class, xml-test | Khi tạo mới hoặc chưa có thiết kế nhất quán, dùng method. |
| execution.parallel | `project` | project, none, methods, classes, tests | Khi tạo mới, hỗ trợ parallel tương thích lifecycle và bật qua cấu hình chạy. Không tự tăng concurrency của test hiện tại. |
| assertions.location | `hybrid` | hybrid, project, test, page-method, page-validation | **Hybrid (Mặc định)**: Các kiểm tra hành động lặp lại (như add to cart thành công, popup/modal hiển thị, transition hoàn tất...) assert ngay trong function của Page để đảm bảo thao tác hoàn tất; các checkpoint chính/nghiệp vụ lớn của test case (order details, total price, cart contents...) bắt buộc đặt ngoài Test Case. |
| assertions.style | `mixed` | mixed, soft, hard | Mixed dùng soft cho kiểm tra độc lập và hard cho điều kiện tiên quyết. Soft vẫn phải fail-fast khi không thể tiếp tục. |
| assertions.soft-scope | `validation-method` | validation-method, test-invocation | Mỗi method validation flush nhóm riêng; nếu assertion trong test thì ranh giới là test invocation. |
| lombok.usage | `when-useful` | when-useful, prefer, avoid | Dùng khi giảm boilerplate hữu ích, phù hợp build. Avoid không yêu cầu tự gỡ Lombok hiện có. |

`project` không miễn kiểm tra lỗi. Nếu thiết kế không nhất quán, áp dụng fallback trong phạm vi task và giải thích. Yêu cầu trong task chỉ override lần đó; chỉ sửa bảng khi người dùng yêu cầu lưu lựa chọn lâu dài.

## Lifecycle WebDriver

- `method`: BeforeMethod tạo session, AfterMethod(alwaysRun = true) quit/remove. Mỗi invocation độc lập; kiểm tra mutable fields khi các method cùng test instance chạy song song.
- `class`: BeforeClass tạo session, AfterClass(alwaysRun = true) cleanup. Method dùng chung session phải tuần tự trên đúng thread owner. Reset trạng thái cần thiết để tránh phụ thuộc test. Kiểm tra nhiều instance/class dùng cùng worker không ghi đè session còn sống.
- `xml-test`: BeforeTest tạo session, AfterTest(alwaysRun = true) cleanup. Phạm vi là thẻ `<test>` trong testng.xml, không phải mỗi method @Test. Tránh tạo lặp driver qua nhiều instance BaseTest: cần một owner khởi tạo đúng một lần và teardown đúng một lần cho XML test.

| Lifecycle | Parallel thường phù hợp | Cần kiểm tra |
| --- | --- | --- |
| method | methods/classes/tests với session riêng mỗi invocation | DataProvider parallel và shared fields trên cùng instance. |
| class | classes, method trong mỗi class tuần tự | Không để parallel methods/DataProvider đồng thời dùng session class. |
| xml-test | tests, nội bộ mỗi XML test tuần tự | Không để parallel classes/methods trong cùng XML test đồng thời dùng session đó. |

Đây là hướng dẫn, không phải bảo đảm từ annotation. Kiểm tra scheduling, thread affinity, DataProvider và lifecycle thực tế. Không tự đổi lifecycle người dùng đã chọn để né xung đột; điều chỉnh scheduling trong phạm vi được yêu cầu hoặc nêu lựa chọn cần quyết định.

## Assertion trong Page Object

- `hybrid` (Quy ước chuẩn theo yêu cầu người dùng):
  - **Assert trong function của Page**: Áp dụng cho các xác nhận hành động lặp đi lặp lại hoặc kiểm tra điều kiện hoàn thành của action (ví dụ: click Add to cart -> assert ngay item đã thêm vào giỏ thành công/nút đổi trạng thái 'added'; đóng modal -> assert modal đã ẩn). Điều này giúp method tự bảo vệ (self-validating action), không để test chạy tiếp khi thao tác cơ sở đã fail.
  - **Assert ngoài Test Case (`@Test`)**: Áp dụng cho các **checkpoint to / checkpoint chính** của kịch bản test case (ví dụ: kiểm tra danh sách sản phẩm trong giỏ hàng, thông tin hóa đơn billing details, tổng tiền đơn hàng, thông báo order confirmation...). Điều này giúp cấu trúc test case tường minh, đọc vào thấy ngay các mục tiêu kiểm thử chính của kịch bản.
- `test`: Page Object chỉ thực hiện action/trả dữ liệu; toàn bộ assertion nằm ở test.
- `page-method`: đặt assertion trực tiếp trong method hành động để xác nhận kết quả cam kết của hành động đó. Dùng tên/contract rõ ràng khi có validation, ví dụ submitAndVerifySuccess(). Không thêm assertion ngoài scenario vào mọi getter/action.
- `page-validation`: page có method validation riêng, ví dụ verifyCartProducts(expected); test gọi action rồi validation. Method page chứa assertion, test không tự duyệt raw WebElement.

Nếu chọn page-method, không âm thầm chuyển sang page-validation theo sở thích của agent. Nhận expected data cần thiết từ caller, không hardcode dữ liệu scenario trong page.

Với validation-method, tạo SoftAssert riêng và assertAll trước khi method return; không tích lũy lỗi qua nhiều method. Với test-invocation, tạo/inject context riêng cho từng invocation, bảo đảm assertAll tại ranh giới test trước khi listener nhận kết quả và driver bị quit. Không chỉ flush ở AfterClass/AfterTest làm mất liên kết lỗi với test; không dùng SoftAssert static hay tái sử dụng qua tests. Hard assertion cho prerequisite mà hành động tiếp theo phụ thuộc vào.

## Ví dụ tùy chỉnh, không tự áp dụng

Để dùng driver theo class và assertion ngay trong method page: đổi driver.lifecycle thành class, assertions.location thành page-method. Nếu chạy song song các class, chọn execution.parallel là classes và bảo đảm methods/DataProviders trong cùng class không dùng session đồng thời. Giữ assertions.soft-scope là validation-method nếu muốn mỗi method page kết luận nhóm assertion trước khi trả về.
