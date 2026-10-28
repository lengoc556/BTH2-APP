package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * Màn hình 2: Danh sách liên hệ (MainActivity)
 * Quản lý danh sách liên hệ bằng ArrayList<Contact>, cập nhật khối thống kê,
 * hiển thị dữ liệu mẫu và điều hướng sang AddContactActivity hoặc ContactDetailActivity.
 */
public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    private TextView tvTotalContactsCount;
    private TextView tvFavoriteContactsCount;
    private EditText etSearchContact;
    private RecyclerView rvContacts;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnAddContact;

    // Danh sách lưu trữ trong bộ nhớ tạm theo yêu cầu đề bài
    private ArrayList<Contact> contactList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    // ActivityResultLauncher nhận dữ liệu khi Thêm liên hệ mới
    private final ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Contact newContact = (Contact) data.getSerializableExtra(AddContactActivity.EXTRA_NEW_CONTACT);
                    if (newContact != null) {
                        contactList.add(newContact);
                        contactAdapter.updateData(contactList);
                        updateStatistics();
                        checkEmptyState();
                        // Cuộn đến mục vừa thêm
                        rvContacts.smoothScrollToPosition(contactList.size() - 1);
                    }
                }
            }
    );

    // ActivityResultLauncher nhận dữ liệu khi Chi tiết liên hệ có thay đổi hoặc bị xóa
    private final ActivityResultLauncher<Intent> contactDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int position = data.getIntExtra(ContactDetailActivity.EXTRA_POSITION, -1);
                    boolean isDeleted = data.getBooleanExtra(ContactDetailActivity.EXTRA_DELETE_CONTACT, false);

                    if (isDeleted && position >= 0 && position < contactList.size()) {
                        // Người dùng đã xóa liên hệ
                        contactList.remove(position);
                        contactAdapter.updateData(contactList);
                        updateStatistics();
                        checkEmptyState();
                    } else if (data.hasExtra(ContactDetailActivity.EXTRA_UPDATED_CONTACT) && position >= 0 && position < contactList.size()) {
                        // Người dùng thay đổi trạng thái yêu thích
                        Contact updatedContact = (Contact) data.getSerializableExtra(ContactDetailActivity.EXTRA_UPDATED_CONTACT);
                        if (updatedContact != null) {
                            contactList.set(position, updatedContact);
                            contactAdapter.updateData(contactList);
                            updateStatistics();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSampleData();
        setupRecyclerView();
        updateStatistics();
        setupListeners();
    }

    private void initViews() {
        tvTotalContactsCount = findViewById(R.id.tvTotalContactsCount);
        tvFavoriteContactsCount = findViewById(R.id.tvFavoriteContactsCount);
        etSearchContact = findViewById(R.id.etSearchContact);
        rvContacts = findViewById(R.id.rvContacts);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    /**
     * Khởi tạo 3 dữ liệu mẫu ban đầu theo đúng yêu cầu đề bài:
     * 1. Nguyễn Văn An  | 0901234567 | Bạn bè    | Yêu thích: Không
     * 2. Trần Thị Bình  | 0912345678 | Gia đình  | Yêu thích: Có
     * 3. Lê Minh Châu   | 0987654321 | Công việc | Yêu thích: Không
     */
    private void initSampleData() {
        contactList.add(new Contact(
                "Nguyễn Văn An",
                "0901234567",
                "Bạn bè",
                "Nam",
                false,
                "Bạn học thời đại học"
        ));

        contactList.add(new Contact(
                "Trần Thị Bình",
                "0912345678",
                "Gia đình",
                "Nữ",
                true,
                "Chị gái, hay gọi buổi tối"
        ));

        contactList.add(new Contact(
                "Lê Minh Châu",
                "0987654321",
                "Công việc",
                "Khác",
                false,
                "Đồng nghiệp cùng dự án"
        ));
    }

    private void setupRecyclerView() {
        contactAdapter = new ContactAdapter(this, contactList, this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(contactAdapter);
        checkEmptyState();
    }

    /**
     * Cập nhật khối thống kê: Tổng liên hệ và Số liên hệ yêu thích
     */
    private void updateStatistics() {
        int total = contactList.size();
        int favorites = 0;
        for (Contact contact : contactList) {
            if (contact.isFavorite()) {
                favorites++;
            }
        }

        tvTotalContactsCount.setText(String.valueOf(total));
        tvFavoriteContactsCount.setText(String.valueOf(favorites));
    }

    private void checkEmptyState() {
        if (contactList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvContacts.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvContacts.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        // Nút Thêm liên hệ
        btnAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            // Truyền danh sách số điện thoại hiện có để kiểm tra chống trùng lặp
            ArrayList<String> phones = new ArrayList<>();
            for (Contact c : contactList) {
                phones.add(c.getPhone());
            }
            intent.putStringArrayListExtra(AddContactActivity.EXTRA_EXISTING_PHONES, phones);
            addContactLauncher.launch(intent);
        });

        // Tìm kiếm theo tên hoặc số điện thoại thời gian thực
        etSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                contactAdapter.filter(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Khi nhấn vào một liên hệ trong danh sách: mở ContactDetailActivity
     */
    @Override
    public void onContactClick(Contact contact, int position) {
        Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
        intent.putExtra(ContactDetailActivity.EXTRA_POSITION, position);
        contactDetailLauncher.launch(intent);
    }
}
