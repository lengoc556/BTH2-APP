package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Màn hình 1: Đăng nhập (LoginActivity)
 * Tài khoản cố định: admin / 123456
 * Xử lý kiểm tra bỏ trống, so sánh thông tin và chuyển sang MainActivity.
 */
public class LoginActivity extends AppCompatActivity {

    // Tài khoản cố định trong code theo đề bài
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextView tvErrorMessage;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các view từ XML
        initViews();

        // Thiết lập sự kiện cho nút Đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void handleLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // 1. Kiểm tra bỏ trống tên đăng nhập
        if (TextUtils.isEmpty(username)) {
            showError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

        // 2. Kiểm tra bỏ trống mật khẩu
        if (TextUtils.isEmpty(password)) {
            showError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // 3. So sánh với tài khoản cố định (admin / 123456)
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            // Đăng nhập thành công: Ẩn thông báo lỗi
            tvErrorMessage.setVisibility(View.GONE);
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Gọi finish() để khi nhấn nút Back không quay lại màn hình đăng nhập
            finish();
        } else {
            // Sai tên đăng nhập hoặc mật khẩu: hiển thị thông báo lỗi
            showError("Sai tên đăng nhập hoặc mật khẩu");
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
