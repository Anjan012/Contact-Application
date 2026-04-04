package com.example.smartcontacts.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcontacts.R;
import com.example.smartcontacts.model.Contact;

import java.util.ArrayList;
import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    // Five distinct avatar colors for visual variety
    private static final int[] AVATAR_COLORS = {
        0xFF1565C0, // Blue
        0xFF6A1B9A, // Purple
        0xFF00695C, // Teal
        0xFFAD1457, // Pink
        0xFFE65100  // Deep Orange
    };

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    private List<Contact> contactList;
    private final OnContactClickListener listener;

    public ContactAdapter(OnContactClickListener listener) {
        this.contactList = new ArrayList<>();
        this.listener    = listener;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact, listener);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateList(List<Contact> newList) {
        this.contactList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // it will return true -- if the adapter currently has no items.
    public boolean isEmpty() {
        return contactList.isEmpty();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        private final View     viewAvatarBg;
        private final TextView tvInitials;
        private final TextView tvFullName;
        private final TextView tvCompany;
        private final TextView tvPhone;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            viewAvatarBg = itemView.findViewById(R.id.viewAvatarBg);
            tvInitials   = itemView.findViewById(R.id.tvInitials);
            tvFullName   = itemView.findViewById(R.id.tvFullName);
            tvCompany    = itemView.findViewById(R.id.tvCompany);
            tvPhone      = itemView.findViewById(R.id.tvPhone);
        }

        void bind(Contact contact, OnContactClickListener listener) {
            // Full name
            tvFullName.setText(contact.getFullName());

            // Phone
            tvPhone.setText(contact.getPhone());

            // Company
            String company = contact.getCompany();
            if (company != null && !company.trim().isEmpty()) {
                tvCompany.setText(company.trim());
                tvCompany.setVisibility(View.VISIBLE);
            } else {
                tvCompany.setVisibility(View.GONE);
            }

            // Avatar
            tvInitials.setText(contact.getInitials());

            // Avatar background color
            int color = AVATAR_COLORS[contact.getColorIndex()];
            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setShape(GradientDrawable.OVAL);
            bgDrawable.setColor(color);
            viewAvatarBg.setBackground(bgDrawable);

            // Click to open detail
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onContactClick(contact);
            });
        }
    }
}
