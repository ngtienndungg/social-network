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

import com.amrdeveloper.reactbutton.FbReactions;
import com.amrdeveloper.reactbutton.ReactButton;
import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.model.post.Post;
import com.example.social_network.model.reaction.Reaction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    List<Post> posts;

    public interface IUpdateUserReaction {
        void updateUserReaction(String uid,
                                int postId,
                                String postOwnerId,
                                String previousReactionType,
                                String newReactionType,
                                int adapterPosition);
    }

    IUpdateUserReaction iUpdateUserReaction;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        if (context instanceof IUpdateUserReaction) {
            iUpdateUserReaction = (IUpdateUserReaction) context;
        }
        else {
            throw new RuntimeException(context.toString() + "must implement IUpdateUserReaction");
        }
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
        holder.rbReaction.setCurrentReaction(FbReactions.getReaction(post.getReactionType()));

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

    public void updatePostAfterReaction(int adapterPosition, Reaction reaction) {
        Post post = posts.get(adapterPosition);

        post.setLikeCount(reaction.getLikeCount());
        post.setLoveCount(reaction.getLoveCount());
        post.setCareCount(reaction.getCareCount());
        post.setHahaCount(reaction.getHahaCount());
        post.setWowCount(reaction.getWowCount());
        post.setSadCount(reaction.getSadCount());
        post.setAngryCount(reaction.getAngryCount());

        posts.set(adapterPosition, post);
        notifyItemChanged(adapterPosition, post);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView ivProfile, ivPrivacy, ivStatusImage;
        private TextView tvName, tvDate, tvPost;
        ReactButton rbReaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivPrivacy = itemView.findViewById(R.id.ivPrivacy);
            ivStatusImage = itemView.findViewById(R.id.ivStatusImage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPost = itemView.findViewById(R.id.tvPost);
            tvName = itemView.findViewById(R.id.tvName);
            rbReaction = itemView.findViewById(R.id.rbReaction);

            rbReaction.setReactClickListener(this);
            rbReaction.setReactDismissListener(this);
        }

        @Override
        public void onClick(View v) {
            onReactionChanged(v);
        }

        @Override
        public boolean onLongClick(View v) {
            onReactionChanged(v);
            return false;
        }

        private void onReactionChanged(View v) {
            String previousReactionType = posts.get(getAdapterPosition()).getReactionType();
            String newReactionType = ((ReactButton) v).getCurrentReaction().getReactType();

            if (!previousReactionType.contentEquals(newReactionType)) {
                iUpdateUserReaction.updateUserReaction(
                        FirebaseAuth.getInstance().getUid(),
                        posts.get(getAdapterPosition()).getPostId(),
                        posts.get(getAdapterPosition()).getPostUserId(),
                        previousReactionType,
                        newReactionType,
                        getAdapterPosition()
                );
            }
        }
    }

}
