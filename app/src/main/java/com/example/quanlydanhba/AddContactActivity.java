package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * Màn hình 3: Thêm liên hệ (AddContactActivity)
 * Tiếp nhận thông tin liên hệ mới, kiểm tra tính hợp lệ và chống trùng lặp số điện thoại.
 */
public class AddContactActivity extends AppCompatActivity {

    public static final String EXTRA_EXISTING_PHONES = "EXISTING_PHONES";
    public static final String EXTRA_NEW_CONTACT = "NEW_CONTACT";

    private ImageButton btnBackAdd;
    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private Spinner spGroup;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private CheckBox cbFavorite;
    private TextInputEditText etNotes;
    private MaterialButton btnSaveContact;

    // Danh sách số điện thoại đã tồn tại để kiểm tra trùng lặp
    private ArrayList<String> existingPhoneList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Lấy danh sách số điện thoại truyền từ MainActivity
        if (getIntent() != null && getIntent().hasExtra(EXTRA_EXISTING_PHONES)) {
            existingPhoneList = getIntent().getStringArrayListExtra(EXTRA_EXISTING_PHONES);
            if (existingPhoneList == null) {
                existingPhoneList = new ArrayList<>();
            }
        }

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        btnBackAdd = findViewById(R.id.btnBackAdd);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        spGroup = findViewById(R.id.spGroup);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        cbFavorite = findViewById(R.id.cbFavorite);
        etNotes = findViewById(R.id.etNotes);
        btnSaveContact = findViewById(R.id.btnSaveContact);
    }

    private void setupSpinner() {
        // Cài đặt adapter cho Spinner nhóm
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.contact_groups,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGroup.setAdapter(adapter);

        // Mặc định chọn mục "Bạn bè" như trong phác thảo đề bài (hoặc mục đầu tiên)
        spGroup.setSelection(1); // "Bạn bè"
    }

    private void setupListeners() {
        // Nút quay lại trên Toolbar
        btnBackAdd.setOnClickListener(v -> finish());

        // Nút Lưu liên hệ
        btnSaveContact.setOnClickListener(v -> handleSaveContact());
    }

    private void handleSaveContact() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
        String group = spGroup.getSelectedItem() != null ? spGroup.getSelectedItem().toString() : "Khác";

        // 1. Kiểm tra họ và tên không được để trống
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Họ và tên không được để trống");
            etFullName.requestFocus();
            return;
        }

        // 2. Kiểm tra số điện thoại không được để trống
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Số điện thoại không được để trống");
            etPhone.requestFocus();
            return;
        }

        // 3. Kiểm tra định dạng số điện thoại (10 chữ số và bắt đầu bằng số 0)
        // Regex: ^0[0-9]{9}$
        if (!phone.matches("^0[0-9]{9}$")) {
            etPhone.setError("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0");
            etPhone.requestFocus();
            return;
        }

        // 4. Kiểm tra số điện thoại đã tồn tại trong danh bạ chưa
        if (existingPhoneList.contains(phone)) {
            etPhone.setError("Số điện thoại đã tồn tại trong danh bạ");
            etPhone.requestFocus();
            Toast.makeText(this, "Số điện thoại đã tồn tại trong danh bạ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Xác định giới tính đã chọn
        String gender = "Nam";
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rbFemale) {
            gender = "Nữ";
        } else if (checkedId == R.id.rbOther) {
            gender = "Khác";
        }

        // 6. Xác định trạng thái yêu thích
        boolean isFavorite = cbFavorite.isChecked();

        // 7. Tạo đối tượng Contact mới
        Contact newContact = new Contact(fullName, phone, group, gender, isFavorite, notes);

        // 8. Đóng gói vào Intent và trả về MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NEW_CONTACT, newContact);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Thêm liên hệ thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
