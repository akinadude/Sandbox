package ru.rudedude.sandbox.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.rudedude.sandbox.App;
import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.adapter.SeAdapter;
import ru.rudedude.sandbox.network.stackexchange.SeApiManager;
import ru.rudedude.sandbox.viewholder.SeUserViewHolder;


public class SeUsersFragment extends Fragment implements SeUserViewHolder.OpenProfileListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipe;

    private SeAdapter mAdapter;
    private SeApiManager mSeApiManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_se_users, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.so_recyclerview);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.so_swipe);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new SeAdapter(new ArrayList<>());
        mAdapter.setOpenProfileListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mSeApiManager = new SeApiManager();

        mSwipe.setOnRefreshListener(this::refreshList);

        refreshList();
    }

    private void refreshList() {
        showRefresh(true);
        mSeApiManager.getMostPopularSoUsers(10).subscribe(users -> {
            showRefresh(false);
            mAdapter.updateUsers(users);
        }, error -> {
            App.L.error(error.toString());
            showRefresh(false);
        });
    }

    private void showRefresh(boolean show) {
        mSwipe.setRefreshing(show);
        int visibility = show ? View.GONE : View.VISIBLE;
        mRecyclerView.setVisibility(visibility);
    }

    @Override
    public void openProfile(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
