package com.example.social_network.feature.homepage.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.model.friend.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    List<Request> requests;
    private IPerformAction iPerformAction;

    public RequestAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
        iPerformAction = (IPerformAction) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request item = requests.get(position);
        holder.profileName.setText(item.getName());

        String image = "";

        if (Uri.parse(item.getProfileUrl()).getAuthority() == null) {
            image = ApiClient.BASE_URL + item.getProfileUrl();
        }
        else {
            image = item.getProfileUrl();
        }

        Glide.with(context).load(image).placeholder(R.drawable.default_placeholder).into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", item.getUid()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage;;
        TextView profileName;
        Button btAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.ivProfile);
            profileName = itemView.findViewById(R.id.tvProfileName);
            btAccept = itemView.findViewById(R.id.btAccept);
            btAccept.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iPerformAction.performAction(requests.indexOf(requests.get(getAdapterPosition())), requests.get(getAdapterPosition()).getUid(), 3);
        }
    }

    public interface IPerformAction {
        void performAction(int position, String profileId, int operationType);
    }
}
