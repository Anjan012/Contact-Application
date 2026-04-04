package com.example.smartcontacts.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartcontacts.AppExecutor;
import com.example.smartcontacts.R;
import com.example.smartcontacts.database.DBHelper;
import com.example.smartcontacts.model.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class ContactDetailActivity extends AppCompatActivity {

    // ── Views ─────────────────────────────────────────────────────────────────
    private TextView         tvDetailInitials;
    private TextView         tvDetailName;
    private TextView         tvDetailCompanyHeader;
    private TextView         tvDetailPhone;
    private TextView         tvDetailEmail;
    private TextView         tvDetailCompany;
    private MaterialCardView cardPhone;
    private MaterialCardView cardEmail;
    private MaterialCardView cardCompany;
    private MaterialButton   btnEdit;
    private MaterialButton   btnDelete;

    // ── State ─────────────────────────────────────────────────────────────────
    private DBHelper db;
    private Contact  contact;
    private int      contactId = -1;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        db = DBHelper.getInstance(this);
        contactId = getIntent().getIntExtra(MainActivity.EXTRA_CONTACT_ID, -1);

        if (contactId == -1) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadContact();
    }


    private void initViews() {
        tvDetailInitials       = findViewById(R.id.tvDetailInitials);
        tvDetailName           = findViewById(R.id.tvDetailName);
        tvDetailCompanyHeader  = findViewById(R.id.tvDetailCompanyHeader);
        tvDetailPhone          = findViewById(R.id.tvDetailPhone);
        tvDetailEmail          = findViewById(R.id.tvDetailEmail);
        tvDetailCompany        = findViewById(R.id.tvDetailCompany);
        cardPhone              = findViewById(R.id.cardPhone);
        cardEmail              = findViewById(R.id.cardEmail);
        cardCompany            = findViewById(R.id.cardCompany);
        btnEdit                = findViewById(R.id.btnEdit);
        btnDelete              = findViewById(R.id.btnDelete);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }


    private void loadContact() {
        AppExecutor.getInstance().diskIO().execute(() -> {
            Contact c = db.getContactById(contactId);
            AppExecutor.getInstance().mainThread().execute(() -> {
                if (c != null) {
                    contact = c;
                    populateViews(c);
                } else {
                    Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void populateViews(Contact c) {
        // Header
        tvDetailInitials.setText(c.getInitials());
        tvDetailName.setText(c.getFullName());

        // Company in header
        String company = c.getCompany();
        if (company != null && !company.trim().isEmpty()) {
            tvDetailCompanyHeader.setText(company.trim());
            tvDetailCompanyHeader.setVisibility(View.VISIBLE);
        } else {
            tvDetailCompanyHeader.setVisibility(View.GONE);
        }

        // Phone
        tvDetailPhone.setText(c.getPhone());
        cardPhone.setOnClickListener(v -> dialPhone(c.getPhone()));

        // Email
        String email = c.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            tvDetailEmail.setText(email.trim());
            cardEmail.setVisibility(View.VISIBLE);
            cardEmail.setOnClickListener(v -> sendEmail(email.trim()));
        } else {
            cardEmail.setVisibility(View.GONE);
        }

        // Company
        if (company != null && !company.trim().isEmpty()) {
            tvDetailCompany.setText(company.trim());
            cardCompany.setVisibility(View.VISIBLE);
        } else {
            cardCompany.setVisibility(View.GONE);
        }

        // buttons
        btnEdit.setOnClickListener(v -> openEditScreen());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            openEditScreen();
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openEditScreen() {
        Intent intent = new Intent(this, AddEditContactActivity.class);
        intent.putExtra(MainActivity.EXTRA_CONTACT_ID, contactId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (contactId != -1) loadContact();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirm_title))
            .setMessage(getString(R.string.delete_confirm_message))
            .setPositiveButton(getString(R.string.delete), (dialog, which) -> deleteContact())
            .setNegativeButton(getString(R.string.cancel), null)
            .show();
    }

    private void deleteContact() {
        AppExecutor.getInstance().diskIO().execute(() -> {
            int rows = db.deleteContact(contactId);
            AppExecutor.getInstance().mainThread().execute(() -> {
                if (rows > 0) {
                    Toast.makeText(this, getString(R.string.contact_deleted),
                            Toast.LENGTH_SHORT).show();
                    finish(); // go back to list
                } else {
                    Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void dialPhone(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + Uri.encode(phone)));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(String email) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO,
                    Uri.parse("mailto:" + email));
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }
}
