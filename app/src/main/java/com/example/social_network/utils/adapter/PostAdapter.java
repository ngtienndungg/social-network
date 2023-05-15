package com.example.social_network.utils.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.nfc.TagLostException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.model.post.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvName.setText(post.getName());
        holder.tvDate.setText(post.getStatusTime());

        if (post.getPrivacy().equals("0")) {
            holder.ivPrivacy.setImageResource(R.drawable.ic_friends);
        }
        else if (post.getPrivacy().equals("1")) {
            holder.ivPrivacy.setImageResource(R.drawable.ic_only_me);
        }
        else {
            holder.ivPrivacy.setImageResource(R.drawable.ic_public);
        }

        String profileImage = post.getProfileUrl();

        if (!post.getProfileUrl().isEmpty()) {
            if (Uri.parse(post.getProfileUrl()).getAuthority() == null) {
                profileImage = ApiClient.BASE_URL + post.getProfileUrl();
            }
            Glide.with(context).load(profileImage).placeholder(R.drawable.default_profile_placeholder).into(holder.ivProfile);
        }
        if (!post.getStatusImage().isEmpty()) {
            holder.ivStatusImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(ApiClient.BASE_URL + post.getStatusImage()).placeholder(R.drawable.default_profile_placeholder).into(holder.ivStatusImage);
        }
        else {
            holder.ivStatusImage.setVisibility(View.GONE);
        }

        if (post.getPost().isEmpty()) {
            holder.tvPost.setVisibility(View.GONE);
        }
        else {
            holder.tvPost.setVisibility(View.VISIBLE);
            holder.tvPost.setText(post.getPost());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfile, ivPrivacy, ivStatusImage;
        private TextView tvName, tvDate, tvPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivPrivacy = itemView.findViewById(R.id.ivPrivacy);
            ivStatusImage = itemView.findViewById(R.id.ivStatusImage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPost = itemView.findViewById(R.id.tvPost);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
