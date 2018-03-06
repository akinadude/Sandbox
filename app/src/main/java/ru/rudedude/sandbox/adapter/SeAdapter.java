package ru.rudedude.sandbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.model.stackexchange.User;
import ru.rudedude.sandbox.viewholder.SeUserViewHolder;

public class SeAdapter extends RecyclerView.Adapter<SeUserViewHolder> {

    private SeUserViewHolder.OpenProfileListener mProfileListener;
    private List<User> mUsers = new ArrayList<>();

    public SeAdapter(List<User> users) {
        mUsers = users;
    }

    public void updateUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public SeUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_se_user, parent, false);
        return new SeUserViewHolder(v, mProfileListener);
    }

    @Override
    public void onBindViewHolder(SeUserViewHolder holder, int position) {
        if (position < mUsers.size())
            holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    public void setOpenProfileListener(SeUserViewHolder.OpenProfileListener listener) {
        mProfileListener = listener;
    }
}
