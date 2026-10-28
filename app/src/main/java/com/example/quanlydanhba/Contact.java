package com.example.quanlydanhba;

import java.io.Serializable;
import java.util.Objects;

/**
 * Lớp Contact biểu diễn một liên hệ trong danh bạ
 * Gồm các trường: Họ và tên, Số điện thoại, Nhóm, Giới tính, Yêu thích, Ghi chú.
 * Kế thừa Serializable để có thể truyền qua lại giữa các Activity qua Intent.
 */
public class Contact implements Serializable {
    private String fullName;      // Họ và tên
    private String phone;         // Số điện thoại (10 chữ số, bắt đầu bằng 0)
    private String group;         // Nhóm (Gia đình, Bạn bè, Công việc, Khác)
    private String gender;        // Giới tính (Nam, Nữ, Khác)
    private boolean isFavorite;   // Yêu thích
    private String notes;         // Ghi chú

    // Constructor đầy đủ
    public Contact(String fullName, String phone, String group, String gender, boolean isFavorite, String notes) {
        this.fullName = fullName != null ? fullName.trim() : "";
        this.phone = phone != null ? phone.trim() : "";
        this.group = group != null ? group.trim() : "Khác";
        this.gender = gender != null ? gender.trim() : "Nam";
        this.isFavorite = isFavorite;
        this.notes = notes != null ? notes.trim() : "";
    }

    // Getters và Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group != null ? group.trim() : "Khác";
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender != null ? gender.trim() : "Nam";
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : "";
    }

    /**
     * Lấy ký tự đại diện cho Avatar.
     * Theo quy ước tên tiếng Việt (và như hình mẫu trong đề bài):
     * "Nguyễn Văn An" -> "A"
     * "Trần Thị Bình" -> "B"
     * "Lê Minh Châu" -> "C"
     */
    public String getAvatarLetter() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "?";
        }
        String cleanName = fullName.trim();
        String[] parts = cleanName.split("\\s+");
        String targetWord = parts[parts.length - 1]; // Lấy từ cuối cùng (Tên chính)
        if (!targetWord.isEmpty()) {
            return targetWord.substring(0, 1).toUpperCase();
        }
        return cleanName.substring(0, 1).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(phone, contact.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", group='" + group + '\'' +
                ", gender='" + gender + '\'' +
                ", isFavorite=" + isFavorite +
                ", notes='" + notes + '\'' +
                '}';
    }
}
