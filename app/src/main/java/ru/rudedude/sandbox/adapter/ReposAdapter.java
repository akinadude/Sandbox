package ru.rudedude.sandbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.model.GithubRepo;
import ru.rudedude.sandbox.viewholder.BaseViewHolder;
import ru.rudedude.sandbox.viewholder.RepoViewHolder;

public class ReposAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<GithubRepo> mRepos = new ArrayList<>();

    public void setItems(List<GithubRepo> repos) {
        mRepos.clear();
        mRepos = repos;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        return new RepoViewHolder(li.inflate(R.layout.item_github_repo, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(mRepos.get(position));
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }
}
