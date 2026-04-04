package com.example.smartcontacts.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartcontacts.AppExecutor;
import com.example.smartcontacts.R;
import com.example.smartcontacts.database.DBHelper;
import com.example.smartcontacts.model.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Screen for adding a new contact or editing an existing one.
 *
 * When launched with EXTRA_CONTACT_ID set to a valid ID, the form is
 * pre-populated and the save button performs an UPDATE.
 * Without it, an INSERT is performed.
 */
public class AddEditContactActivity extends AppCompatActivity {

    private TextInputLayout      tilFirstName, tilLastName, tilCompany,
                                  tilPhone, tilEmail;
    private TextInputEditText    etFirstName, etLastName, etCompany,
                                  etPhone, etEmail;
    private MaterialButton       btnSave;
    private TextView             tvAvatarPreview;

    private DBHelper db;
    private Contact  existingContact = null; // null = adding new
    private boolean  isEditMode      = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        db = DBHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupAvatarPreview();
        setupSaveButton();

        int contactId = getIntent().getIntExtra(MainActivity.EXTRA_CONTACT_ID, -1);
        if (contactId != -1) {
            isEditMode = true;
            loadContact(contactId);
        }
    }

    private void initViews() {
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName  = findViewById(R.id.tilLastName);
        tilCompany   = findViewById(R.id.tilCompany);
        tilPhone     = findViewById(R.id.tilPhone);
        tilEmail     = findViewById(R.id.tilEmail);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etCompany   = findViewById(R.id.etCompany);
        etPhone     = findViewById(R.id.etPhone);
        etEmail     = findViewById(R.id.etEmail);

        btnSave         = findViewById(R.id.btnSave);
        tvAvatarPreview = findViewById(R.id.tvAvatarPreview);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAvatarPreview() {
        TextWatcher nameWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String first = getText(etFirstName);
                String last  = getText(etLastName);

                StringBuilder initials = new StringBuilder();
                if (!first.isEmpty()) initials.append(Character.toUpperCase(first.charAt(0)));
                if (!last.isEmpty())  initials.append(Character.toUpperCase(last.charAt(0)));

                tvAvatarPreview.setText(initials.length() > 0 ? initials.toString() : "?");

                if (!first.isEmpty()) tilFirstName.setError(null);
            }
        };

        etFirstName.addTextChangedListener(nameWatcher);
        etLastName.addTextChangedListener(nameWatcher);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) tilPhone.setError(null);
            }
        });
    }

    private void loadContact(int contactId) {
        AppExecutor.getInstance().diskIO().execute(() -> {
            Contact c = db.getContactById(contactId);
            AppExecutor.getInstance().mainThread().execute(() -> {
                if (c != null) {
                    existingContact = c;
                    populateForm(c);
                    // Update toolbar title
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getString(R.string.edit_contact));
                    }
                }
            });
        });
    }

    private void populateForm(Contact c) {
        etFirstName.setText(c.getFirstName());
        etLastName.setText(c.getLastName());
        etCompany.setText(c.getCompany());
        etPhone.setText(c.getPhone());
        etEmail.setText(c.getEmail());

        tvAvatarPreview.setText(c.getInitials());
    }


    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveContact();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        // First name is required
        String firstName = getText(etFirstName);
        if (firstName.isEmpty()) {
            tilFirstName.setError(getString(R.string.first_name_required));
            valid = false;
        } else {
            tilFirstName.setError(null);
        }

        String phone = getText(etPhone);
        if (phone.isEmpty()) {
            tilPhone.setError(getString(R.string.phone_required));
            valid = false;
        } else if (!isValidPhone(phone)) {
            tilPhone.setError(getString(R.string.phone_invalid));
            valid = false;
        } else {
            tilPhone.setError(null);
        }

        String email = getText(etEmail);
        if (!email.isEmpty() && !isValidEmail(email)) {
            tilEmail.setError(getString(R.string.email_invalid));
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        return valid;
    }

    private void saveContact() {
        btnSave.setEnabled(false);

        Contact contact = buildContactFromForm();

        AppExecutor.getInstance().diskIO().execute(() -> {
            boolean success;
            if (isEditMode && existingContact != null) {
                contact.setId(existingContact.getId());
                success = (db.updateContact(contact) > 0);
            } else {
                success = (db.insertContact(contact) != -1);
            }

            final boolean ok = success;
            AppExecutor.getInstance().mainThread().execute(() -> {
                btnSave.setEnabled(true);
                if (ok) {
                    String msg = isEditMode
                            ? getString(R.string.contact_updated)
                            : getString(R.string.contact_saved);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    finish(); // go back to list
                } else {
                    Toast.makeText(this, getString(R.string.error_save), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private Contact buildContactFromForm() {
        return new Contact(
            getText(etFirstName),
            getText(etLastName),
            getText(etCompany),
            getText(etPhone),
            getText(etEmail)
        );
    }

    private String getText(TextInputEditText et) {
        Editable e = et.getText();
        return (e != null) ? e.toString().trim() : "";
    }

    private boolean isValidPhone(String phone) {
        // Allow digits, spaces, +, -, (, )  — at least 6 digits
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 6 && phone.matches("[0-9+\\-().\\s]+");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
