package com.example.quanlydanhba;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(Contact contact, int position);
    }

    private final Context context;
    private List<Contact> originalList;
    private List<Contact> displayList;
    private final OnContactClickListener listener;

    // Bảng màu avatar phong cách hiện đại (Indigo, Violet, Rose, Emerald, Amber, Cyan)
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

    public ContactAdapter(Context context, List<Contact> contactList, OnContactClickListener listener) {
        this.context = context;
        this.originalList = contactList != null ? contactList : new ArrayList<>();
        this.displayList = new ArrayList<>(this.originalList);
        this.listener = listener;
    }

    public void updateData(List<Contact> newList) {
        this.originalList = newList != null ? newList : new ArrayList<>();
        this.displayList = new ArrayList<>(this.originalList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayList = new ArrayList<>(originalList);
        } else {
            String lowerQuery = query.trim().toLowerCase();
            List<Contact> filtered = new ArrayList<>();
            for (Contact c : originalList) {
                if (c.getFullName().toLowerCase().contains(lowerQuery) ||
                        c.getPhone().contains(lowerQuery)) {
                    filtered.add(c);
                }
            }
            displayList = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = displayList.get(position);

        // 1. Chữ cái đại diện avatar
        holder.tvAvatarLetter.setText(contact.getAvatarLetter());

        // Đổi màu nền avatar hài hòa dựa trên tên
        int colorIndex = Math.abs(contact.getFullName().hashCode()) % AVATAR_COLORS.length;
        GradientDrawable avatarDrawable = new GradientDrawable();
        avatarDrawable.setShape(GradientDrawable.OVAL);
        avatarDrawable.setColor(AVATAR_COLORS[colorIndex]);
        holder.layoutAvatar.setBackground(avatarDrawable);

        // 2. Họ và tên & Số điện thoại
        holder.tvFullName.setText(contact.getFullName());
        holder.tvPhone.setText(contact.getPhone());

        // 3. Nhóm liên hệ và định dạng badge màu sắc
        String group = contact.getGroup();
        holder.tvGroupBadge.setText(group);
        setupGroupBadge(holder.tvGroupBadge, group);

        // 4. Dấu sao yêu thích
        if (contact.isFavorite()) {
            holder.ivFavoriteStar.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavoriteStar.setVisibility(View.GONE);
        }

        // Sự kiện click vào item
        holder.cardContact.setOnClickListener(v -> {
            if (listener != null) {
                int originalIndex = originalList.indexOf(contact);
                listener.onContactClick(contact, originalIndex >= 0 ? originalIndex : holder.getAdapterPosition());
            }
        });
    }

    private void setupGroupBadge(TextView tvBadge, String group) {
        if (group == null) group = "";
        switch (group) {
            case "Gia đình":
                tvBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_family));
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.group_family_text));
                break;
            case "Bạn bè":
                tvBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_friends));
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.group_friends_text));
                break;
            case "Công việc":
                tvBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_work));
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.group_work_text));
                break;
            default: // "Khác"
                tvBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_other));
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.group_other_text));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardContact;
        FrameLayout layoutAvatar;
        TextView tvAvatarLetter;
        TextView tvFullName;
        TextView tvPhone;
        TextView tvGroupBadge;
        ImageView ivFavoriteStar;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContact = itemView.findViewById(R.id.cardContact);
            layoutAvatar = itemView.findViewById(R.id.layoutAvatar);
            tvAvatarLetter = itemView.findViewById(R.id.tvAvatarLetter);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvGroupBadge = itemView.findViewById(R.id.tvGroupBadge);
            ivFavoriteStar = itemView.findViewById(R.id.ivFavoriteStar);
        }
    }
}
