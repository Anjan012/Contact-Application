package com.example.smartcontacts.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;           // ← Added this import
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcontacts.AppExecutor;
import com.example.smartcontacts.R;
import com.example.smartcontacts.adapter.ContactAdapter;
import com.example.smartcontacts.database.DBHelper;
import com.example.smartcontacts.model.Contact;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements ContactAdapter.OnContactClickListener {

    public static final String EXTRA_CONTACT_ID = "contact_id";

    private RecyclerView              recyclerView;
    private LinearLayout              emptyStateLayout;
    private TextView                  tvEmptyMessage;
    private ExtendedFloatingActionButton fab;

    private ContactAdapter adapter;
    private DBHelper        db;
    private String          currentQuery = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DBHelper.getInstance(this);

        initViews();
        setupRecyclerView();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload contacts every time we return (e.g. after add/edit/delete)
        loadContacts(currentQuery);
    }


    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView     = findViewById(R.id.recyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvEmptyMessage   = findViewById(R.id.tvEmptyMessage);
        fab              = findViewById(R.id.fab);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);

        // Shrink FAB when scrolling down, expand when at top
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 8)       fab.shrink();
                else if (dy < -8) fab.extend();
            }
        });
    }

    private void setupFab() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditContactActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    loadContacts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    loadContacts(newText);
                    return true;
                }
            });

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) { return true; }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    currentQuery = "";
                    loadContacts("");
                    return true;
                }
            });
        }

        return true;
    }


    private void loadContacts(String query) {
        AppExecutor.getInstance().diskIO().execute(() -> {
            List<Contact> contacts;
            if (TextUtils.isEmpty(query)) {
                contacts = db.getAllContacts();
            } else {
                contacts = db.searchContacts(query);
            }

            final List<Contact> result = contacts;
            AppExecutor.getInstance().mainThread().execute(() -> {
                adapter.updateList(result);
                updateEmptyState(result.isEmpty(), query);
            });
        });
    }

    private void updateEmptyState(boolean isEmpty, String query) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(query)) {
                tvEmptyMessage.setText(getString(R.string.no_results));
            } else {
                tvEmptyMessage.setText(getString(R.string.no_contacts));
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contact.getId());
        startActivity(intent);
    }
}