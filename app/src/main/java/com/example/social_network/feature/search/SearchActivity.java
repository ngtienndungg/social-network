package com.example.social_network.feature.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.social_network.R;
import com.example.social_network.model.search.SearchResponse;
import com.example.social_network.model.search.User;
import com.example.social_network.utils.ViewModelFactory;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView rvSearch;
    private TextView tvDefault;
    private SearchViewModel viewModel;
    private SearchAdapter adapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(SearchViewModel.class);

        rvSearch = findViewById(R.id.rvSearch);
        userList = new ArrayList<>();
        adapter = new SearchAdapter(this, userList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSearch.setLayoutManager(linearLayoutManager);
        rvSearch.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(getResources().getString(R.string.search_people));

        toolbar = findViewById(R.id.toolbar);
        tvDefault = findViewById(R.id.tvDefault);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.super.onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDb(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length()>2) {
                    searchDb(newText);
                }
                else {
                    tvDefault.setVisibility(View.VISIBLE);
                    userList.clear();
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void searchDb(String query) {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", query);
        viewModel.search(params).observe(SearchActivity.this, new Observer<SearchResponse>() {
            @Override
            public void onChanged(SearchResponse searchResponse) {
                if (searchResponse.getStatus()==200) {
                    tvDefault.setVisibility(View.GONE);
                    userList.clear();
                    userList.addAll(searchResponse.getUser());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchActivity.this, searchResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    tvDefault.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}