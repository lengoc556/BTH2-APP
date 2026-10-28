package com.example.quanlydanhba;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * Màn hình 4: Chi tiết liên hệ (ContactDetailActivity)
 * Hiển thị đầy đủ thông tin của liên hệ, hỗ trợ gọi điện qua ACTION_DIAL,
 * thay đổi trạng thái yêu thích và xóa liên hệ.
 */
public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "CONTACT";
    public static final String EXTRA_POSITION = "POSITION";
    public static final String EXTRA_UPDATED_CONTACT = "UPDATED_CONTACT";
    public static final String EXTRA_DELETE_CONTACT = "DELETE_CONTACT";

    private ImageButton btnBackDetail;
    private FrameLayout layoutDetailAvatar;
    private TextView tvDetailAvatarLetter;
    private TextView tvDetailFullName;
    private TextView tvDetailPhone;
    private TextView tvDetailGroup;
    private TextView tvDetailGender;
    private TextView tvDetailNotes;
    private CheckBox cbDetailFavorite;
    private MaterialButton btnCall;
    private MaterialButton btnBack;
    private MaterialButton btnDeleteContact;

    private Contact contact;
    private int position = -1;

    // Bảng màu avatar đồng bộ hiện đại
    private static final int[] AVATAR_COLORS = {
            Color.parseColor("#4F46E5"), // Royal Indigo
            Color.parseColor("#7C3AED"), // Electric Violet
            Color.parseColor("#F43F5E"), // Rose
            Color.parseColor("#10B981"), // Emerald
            Color.parseColor("#F59E0B"), // Amber
            Color.parseColor("#06B6D4"), // Cyan
            Color.parseColor("#2563EB"), // Blue
            Color.parseColor("#0D9488")  // Teal
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // Nhận dữ liệu truyền qua Intent
        if (getIntent() != null) {
            contact = (Contact) getIntent().getSerializableExtra(EXTRA_CONTACT);
            position = getIntent().getIntExtra(EXTRA_POSITION, -1);
        }

        if (contact == null) {
            Toast.makeText(this, "Không tìm thấy thông tin liên hệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayContactData();
        setupListeners();
    }

    private void initViews() {
        btnBackDetail = findViewById(R.id.btnBackDetail);
        layoutDetailAvatar = findViewById(R.id.layoutDetailAvatar);
        tvDetailAvatarLetter = findViewById(R.id.tvDetailAvatarLetter);
        tvDetailFullName = findViewById(R.id.tvDetailFullName);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailGroup = findViewById(R.id.tvDetailGroup);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailNotes = findViewById(R.id.tvDetailNotes);
        cbDetailFavorite = findViewById(R.id.cbDetailFavorite);
        btnCall = findViewById(R.id.btnCall);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
    }

    private void displayContactData() {
        // 1. Avatar chữ cái đầu
        tvDetailAvatarLetter.setText(contact.getAvatarLetter());

        // Đổi màu nền avatar
        int colorIndex = Math.abs(contact.getFullName().hashCode()) % AVATAR_COLORS.length;
        GradientDrawable avatarDrawable = new GradientDrawable();
        avatarDrawable.setShape(GradientDrawable.OVAL);
        avatarDrawable.setColor(AVATAR_COLORS[colorIndex]);
        layoutDetailAvatar.setBackground(avatarDrawable);

        // 2. Họ tên và Số điện thoại
        tvDetailFullName.setText(contact.getFullName());
        tvDetailPhone.setText(contact.getPhone());

        // 3. Nhóm và Giới tính
        tvDetailGroup.setText(contact.getGroup());
        tvDetailGender.setText(contact.getGender());

        // 4. Ghi chú
        if (TextUtils.isEmpty(contact.getNotes())) {
            tvDetailNotes.setText(getString(R.string.no_notes));
            tvDetailNotes.setTextColor(getColor(R.color.text_hint));
        } else {
            tvDetailNotes.setText(contact.getNotes());
            tvDetailNotes.setTextColor(getColor(R.color.text_primary));
        }

        // 5. Trạng thái yêu thích
        cbDetailFavorite.setChecked(contact.isFavorite());
    }

    private void setupListeners() {
        btnBackDetail.setOnClickListener(v -> finishAndReturnResult());
        btnBack.setOnClickListener(v -> finishAndReturnResult());
        btnCall.setOnClickListener(v -> makePhoneCall());
        btnDeleteContact.setOnClickListener(v -> confirmDeleteContact());
    }

    private void makePhoneCall() {
        String phoneNumber = contact.getPhone();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
    }

    private void confirmDeleteContact() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(getString(R.string.dialog_delete_message, contact.getFullName()))
                .setPositiveButton(getString(R.string.dialog_btn_delete), (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_DELETE_CONTACT, true);
                    resultIntent.putExtra(EXTRA_POSITION, position);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(ContactDetailActivity.this, getString(R.string.msg_delete_success), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(getString(R.string.dialog_btn_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void finishAndReturnResult() {
        boolean currentFavorite = cbDetailFavorite.isChecked();
        contact.setFavorite(currentFavorite);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_UPDATED_CONTACT, contact);
        resultIntent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAndReturnResult();
        super.onBackPressed();
    }
}
