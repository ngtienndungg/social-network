package com.example.social_network.feature.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.model.search.User;

import org.w3c.dom.Text;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    Context context;
    List<User> userList;

    public SearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        String userImage = "";

        Uri uri = Uri.parse(user.getProfileUrl());

        if (uri.getAuthority()==null) {
            userImage = ApiClient.BASE_URL + user.getProfileUrl();
        } else {
            userImage = user.getProfileUrl();
        }

        if (!userImage.isEmpty()) {
            Glide.with(context).load(userImage).into(holder.profileImage);
        }
        holder.profileName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage;
        TextView profileName;
        public ViewHolder(View item) {
            super(item);

            profileImage = item.findViewById(R.id.ivUser);
            profileName = item.findViewById(R.id.tvName);

            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", userList.get(getAdapterPosition()).getUid()));
        }
    }
}
