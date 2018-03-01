package ru.rudedude.sandbox.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.adapter.ReposAdapter;
import ru.rudedude.sandbox.model.GithubRepo;
import ru.rudedude.sandbox.network.GithubClient;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReposFragment extends Fragment {

    private static final String TAG = ReposFragment.class.getSimpleName();

    private Subscription subscription;

    private RecyclerView mReposRv;
    private ReposAdapter mReposAdapter;
    private EditText mUsernameEt;
    private Button mSearchBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repos, container, false);

        mReposRv = (RecyclerView) view.findViewById(R.id.repos_rv);
        mReposAdapter = new ReposAdapter();
        mReposRv.setAdapter(mReposAdapter);
        mReposRv.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        mUsernameEt = (EditText) view.findViewById(R.id.fragment_search_repos_username_et);
        mSearchBtn = (Button) view.findViewById(R.id.fragment_search_repos_search_btn);
        mSearchBtn.setOnClickListener(v -> {
            final String username = mUsernameEt.getText().toString();
            if (!username.isEmpty())
                getStarredRepos(username);
        });

        /*Observable<Integer> deferred = Observable.defer(this::getInt);
        subscription = deferred.subscribe();*/

        return view;
    }

    private Observable<Integer> getInt() {
        return Observable.just(1);
    }

    private void getStarredRepos(String username) {
        subscription = GithubClient.getInstance()
                .getStarredRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GithubRepo>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "In onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "In onError()");
                    }

                    @Override
                    public void onNext(List<GithubRepo> gitHubRepos) {
                        Log.d(TAG, "In onNext()");
                        mReposAdapter.setItems(gitHubRepos);
                    }
                });
    }
}
