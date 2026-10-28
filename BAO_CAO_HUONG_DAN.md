# BÁO CÁO BÀI TẬP LỚN LẬP TRÌNH ANDROID
## ỨNG DỤNG QUẢN LÝ DANH BẠ LIÊN HỆ (CONTACT MANAGER)

**Thông tin sinh viên:**
- **Họ và tên:** [Điền họ và tên của bạn]
- **Mã sinh viên (MSSV):** [Điền MSSV]
- **Lớp:** [Điền lớp học phần]
- **Link Repository Git:** `https://github.com/[username]/MSSV_HoTen_QuanLyDanhBa`

---

## 1. TỔNG QUAN VỀ ỨNG DỤNG
Ứng dụng **Quản lý danh bạ liên hệ** được xây dựng bằng ngôn ngữ **Java** và thiết kế giao diện bằng **XML Layout** theo tiêu chuẩn **Material Design 3**, hỗ trợ người dùng quản lý thông tin liên hệ một cách trực quan, hiện đại và tiện lợi.

### Cấu trúc dự án:
- **Ngôn ngữ:** Java 11
- **Kiến trúc & Quản lý dữ liệu:** Lưu trữ trong bộ nhớ khi ứng dụng chạy bằng `ArrayList<Contact>`.
- **Giao tiếp giữa các màn hình:** Sử dụng `Intent` và `ActivityResultLauncher<Intent>` để truyền đối tượng `Contact` (implement `Serializable`).
- **Giao diện hiện đại (UI/UX):**
  - Bảng màu chủ đạo: Xanh ngọc đậm (Deep Teal `#00695C`, `#004D40`) kết hợp viền bo tròn bo góc (Corner Radius 16–20dp) và bóng đổ nhẹ nhàng.
  - Avatar đại diện: Tự động trích xuất chữ cái đầu của Tên (theo chuẩn tên tiếng Việt: *Nguyễn Văn An* -> *A*, *Trần Thị Bình* -> *B*) và tạo màu sắc ngẫu nhiên hài hòa cho từng liên hệ.
  - Phân loại nhóm bằng thẻ màu (Badge/Chip): Gia đình (đỏ nhạt), Bạn bè (xanh dương), Công việc (tím), Khác (xám nhạt).
  - Tích hợp thanh tìm kiếm thông minh theo Tên hoặc Số điện thoại.

---

## 2. NỘI DUNG CHI TIẾT THEO 4 MÀN HÌNH

### 2.1. Màn hình 1: Đăng nhập (`LoginActivity`)
#### a. Các thành phần giao diện đã thêm:
- **Logo ứng dụng:** Khối tròn bo góc chứa biểu tượng ống nghe điện thoại (`ic_phone`).
- **Tiêu đề & Phụ đề:** `TextView` "Danh bạ" và "Đăng nhập để tiếp tục".
- **Ô nhập tên đăng nhập:** `TextInputLayout` + `TextInputEditText` (`etUsername`) với biểu tượng `ic_person`.
- **Ô nhập mật khẩu:** `TextInputLayout` + `TextInputEditText` (`etPassword`) với chế độ ẩn ký tự `inputType="textPassword"` và nút ẩn/hiện mật khẩu `password_toggle`.
- **Thông báo lỗi:** `TextView` (`tvErrorMessage`), chữ đỏ nổi bật, mặc định ẩn và chỉ hiện khi có lỗi.
- **Nút Đăng nhập:** `MaterialButton` (`btnLogin`) màu Deep Teal bo tròn 28dp.
- **Ghi chú tài khoản mẫu:** `TextView` "Tài khoản mẫu: admin / 123456".

#### b. Xử lý logic & Mã nguồn tiêu biểu:
- **Tài khoản cố định:** Khai báo trực tiếp trong code:
  ```java
  private static final String DEFAULT_USERNAME = "admin";
  private static final String DEFAULT_PASSWORD = "123456";
  ```
- **Kiểm tra hợp lệ:**
  - Nếu để trống tên đăng nhập: hiển thị lỗi *"Vui lòng nhập tên đăng nhập"*.
  - Nếu để trống mật khẩu: hiển thị lỗi *"Vui lòng nhập mật khẩu"*.
  - Nếu nhập sai tài khoản hoặc mật khẩu: hiển thị thông báo *"Sai tên đăng nhập hoặc mật khẩu"*.
- **Điều hướng khi đăng nhập thành công:**
  ```java
  Intent intent = new Intent(LoginActivity.this, MainActivity.class);
  startActivity(intent);
  finish(); // Đóng LoginActivity để phím Back không quay lại
  ```

---

### 2.2. Màn hình 2: Danh sách liên hệ (`MainActivity`)
#### a. Các thành phần giao diện đã thêm:
- **Thanh tiêu đề (Header):** `TextView` "Danh bạ của tôi" trên nền xanh Deep Teal.
- **Khối Thống kê (Statistics Card):** Thiết kế card gradient xanh ngọc hiển thị 2 thông số:
  - **Tổng liên hệ (`tvTotalContactsCount`)**: Cập nhật theo `contactList.size()`.
  - **Yêu thích (`tvFavoriteContactsCount`)**: Đếm các liên hệ có `isFavorite() == true`.
- **Thanh tìm kiếm:** `EditText` (`etSearchContact`) tìm kiếm theo thời gian thực (lọc theo họ tên hoặc số điện thoại).
- **Danh sách liên hệ (`rvContacts`):** Sử dụng `RecyclerView` kết hợp `ContactAdapter`.
  - Mỗi item (`item_contact.xml`): Avatar tròn chứa chữ cái đầu, Họ tên, Số điện thoại, Badge nhóm màu sắc, và biểu tượng ngôi sao vàng nếu là liên hệ yêu thích.
- **Giao diện trống (`layoutEmptyState`):** Hiển thị khi chưa có liên hệ nào trong danh bạ.
- **Nút Thêm liên hệ (`btnAddContact`):** Nút bo tròn nổi bật gắn dưới cùng màn hình với icon dấu cộng (`ic_add`).

#### b. Dữ liệu mẫu ban đầu:
1. `Nguyễn Văn An` | `0901234567` | `Bạn bè` | Giới tính: `Nam` | Yêu thích: `false`
2. `Trần Thị Bình` | `0912345678` | `Gia đình` | Giới tính: `Nữ` | Yêu thích: `true`
3. `Lê Minh Châu` | `0987654321` | `Công việc` | Giới tính: `Khác` | Yêu thích: `false`

#### c. Xử lý logic & Mã nguồn tiêu biểu:
- **Cập nhật thống kê:**
  ```java
  private void updateStatistics() {
      int total = contactList.size();
      int favorites = 0;
      for (Contact c : contactList) {
          if (c.isFavorite()) favorites++;
      }
      tvTotalContactsCount.setText(String.valueOf(total));
      tvFavoriteContactsCount.setText(String.valueOf(favorites));
  }
  ```
- **Nhấn vào liên hệ để xem chi tiết:**
  ```java
  Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
  intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
  intent.putExtra(ContactDetailActivity.EXTRA_POSITION, position);
  contactDetailLauncher.launch(intent);
  ```
- **Mở màn hình Thêm liên hệ:** Truyền danh sách số điện thoại hiện tại sang `AddContactActivity` để chống trùng lặp.

---

### 2.3. Màn hình 3: Thêm liên hệ (`AddContactActivity`)
#### a. Các thành phần giao diện đã thêm:
- **Toolbar:** Nút quay lại (`btnBackAdd`) + Tiêu đề "Thêm liên hệ".
- **Họ và tên:** `TextInputEditText` (`etFullName`), bắt buộc nhập.
- **Số điện thoại:** `TextInputEditText` (`etPhone`), `inputType="phone"`, giới hạn tối đa 10 ký tự.
- **Nhóm:** `Spinner` (`spGroup`) với 4 lựa chọn: *Gia đình*, *Bạn bè*, *Công việc*, *Khác*.
- **Giới tính:** `RadioGroup` (`rgGender`) gồm 3 `RadioButton`: *Nam* (mặc định), *Nữ*, *Khác*.
- **Yêu thích:** `CheckBox` (`cbFavorite`) "Thêm vào yêu thích".
- **Ghi chú:** `TextInputEditText` (`etNotes`), `inputType="textMultiLine"`, hiển thị nhiều dòng.
- **Nút Lưu liên hệ:** `MaterialButton` (`btnSaveContact`).

#### b. Xử lý logic & Mã nguồn tiêu biểu:
- **Kiểm tra họ tên:** Không được để trống (`TextUtils.isEmpty(fullName)`).
- **Kiểm tra định dạng số điện thoại:** Phải đúng 10 chữ số và bắt đầu bằng số 0 bằng biểu thức chính quy (Regex):
  ```java
  if (!phone.matches("^0[0-9]{9}$")) {
      etPhone.setError("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0");
      etPhone.requestFocus();
      return;
  }
  ```
- **Kiểm tra trùng lặp số điện thoại:**
  ```java
  if (existingPhoneList.contains(phone)) {
      etPhone.setError("Số điện thoại đã tồn tại trong danh bạ");
      etPhone.requestFocus();
      Toast.makeText(this, "Số điện thoại đã tồn tại trong danh bạ!", Toast.LENGTH_SHORT).show();
      return;
  }
  ```
- **Đóng gói trả về `MainActivity`:**
  ```java
  Contact newContact = new Contact(fullName, phone, group, gender, isFavorite, notes);
  Intent resultIntent = new Intent();
  resultIntent.putExtra(EXTRA_NEW_CONTACT, newContact);
  setResult(RESULT_OK, resultIntent);
  finish();
  ```

---

### 2.4. Màn hình 4: Chi tiết liên hệ (`ContactDetailActivity`)
#### a. Các thành phần giao diện đã thêm:
- **Toolbar:** Nút quay lại (`btnBackDetail`) + Tiêu đề "Chi tiết liên hệ".
- **Card thông tin liên hệ:**
  - Avatar cỡ lớn (72dp) với chữ cái đầu của tên.
  - Họ và tên (`tvDetailFullName`) chữ to, in đậm.
  - Số điện thoại (`tvDetailPhone`) màu xanh ngọc nổi bật.
  - Phân vùng hiển thị Nhóm và Giới tính với đường phân cách thanh mảnh.
  - Ghi chú (`tvDetailNotes`).
  - `CheckBox` Yêu thích (`cbDetailFavorite`): Cho phép người dùng trực tiếp tích/bỏ tích để thay đổi trạng thái yêu thích.
- **Các nút chức năng:**
  - **Nút "Gọi điện" (`btnCall`):** Màu Deep Teal kèm icon điện thoại.
  - **Nút "Quay lại" (`btnBack`):** Nút viền ngoài (Outlined button).
  - **Nút "Xóa liên hệ" (`btnDeleteContact`):** Màu đỏ cảnh báo (`#D32F2F`) kèm icon thùng rác.

#### b. Xử lý logic & Mã nguồn tiêu biểu:
- **Gọi điện bằng Intent ngầm định (`ACTION_DIAL`):**
  ```java
  Intent dialIntent = new Intent(Intent.ACTION_DIAL);
  dialIntent.setData(Uri.parse("tel:" + contact.getPhone()));
  startActivity(dialIntent);
  ```
- **Xác nhận xóa an toàn bằng `AlertDialog`:**
  ```java
  new AlertDialog.Builder(this)
      .setTitle("Xác nhận xóa")
      .setMessage("Bạn có chắc chắn muốn xóa liên hệ \"" + contact.getFullName() + "\" không?")
      .setPositiveButton("Xóa", (dialog, which) -> {
          Intent resultIntent = new Intent();
          resultIntent.putExtra(EXTRA_DELETE_CONTACT, true);
          resultIntent.putExtra(EXTRA_POSITION, position);
          setResult(RESULT_OK, resultIntent);
          finish();
      })
      .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
      .show();
  ```
- **Cập nhật trạng thái yêu thích khi quay lại:**
  ```java
  contact.setFavorite(cbDetailFavorite.isChecked());
  Intent resultIntent = new Intent();
  resultIntent.putExtra(EXTRA_UPDATED_CONTACT, contact);
  resultIntent.putExtra(EXTRA_POSITION, position);
  setResult(RESULT_OK, resultIntent);
  finish();
  ```

---

## 3. HƯỚNG DẪN MỞ DỰ ÁN TRÊN ANDROID STUDIO & ĐẨY LÊN GIT

### 3.1. Cách mở dự án trên Android Studio:
1. Mở **Android Studio**.
2. Chọn **File -> Open...** (hoặc **Open Project** ở màn hình Welcome).
3. Trỏ tới thư mục:
   - `/Users/ngoc/AndroidStudioProjects/QuanLyDanhBa`
4. Chờ Android Studio đồng bộ Gradle và tải tài nguyên.
5. Chọn thiết bị ảo (Emulator) hoặc máy thật và nhấn nút **Run (Shift + F10)**.

### 3.2. Cách đẩy mã nguồn lên GitHub / GitLab:
Dự án đã được khởi tạo sẵn Git và cấu hình `.gitignore` đầy đủ (loại trừ `build/`, `.gradle/`, `.idea/`, `local.properties`). Để đẩy lên tài khoản Git của bạn:
```bash
cd /Users/ngoc/AndroidStudioProjects/QuanLyDanhBa

# Thay link repository của bạn vào đây:
git remote add origin https://github.com/[username]/MSSV_HoTen_QuanLyDanhBa.git

# Đổi nhánh sang main (nếu cần) và đẩy lên:
git branch -M main
git push -u origin main
```
