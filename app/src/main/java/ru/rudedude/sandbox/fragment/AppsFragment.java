package ru.rudedude.sandbox.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.rudedude.sandbox.ApplicationsList;
import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.adapter.AppsAdapter;
import ru.rudedude.sandbox.model.AppInfo;
import ru.rudedude.sandbox.model.AppInfoRich;
import ru.rudedude.sandbox.utils.Utils;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

public class AppsFragment extends Fragment {

    private static final String TAG = "AppsFragment";

    RecyclerView mAppsRv;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private AppsAdapter mAppsAdapter;

    private File mFilesDir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        mAppsRv = (RecyclerView) view.findViewById(R.id.apps_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.apps_srl);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppsRv.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAppsAdapter = new AppsAdapter(new ArrayList<>(), R.layout.item_app);
        mAppsRv.setAdapter(mAppsAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));

        // Progress
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        mAppsRv.setVisibility(View.GONE);

        Context appContext = getActivity().getApplicationContext();
        Log.d(TAG, appContext.toString());

        getFileDir().subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    mFilesDir = file;
                    refreshTheList();
                });
    }

    private void refreshTheList() {
        /*getApps().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<AppInfo> appInfos) {
                        //Log.d(TAG, "onNext, current thread: " + Thread.currentThread());
                        mAppsRv.setVisibility(View.VISIBLE);
                        mAppsAdapter.addApplications(appInfos);
                        mSwipeRefreshLayout.setRefreshing(false);
                        storeList(appInfos);
                    }
                });*/

        Observable.concat(groupedApps())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mAppsRv.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        //Log.d(TAG, "onNext, current thread: " + Thread.currentThread());
                        mAppsAdapter.addApplication(mAppsAdapter.getItemCount() - 1, appInfo);
                    }
                });
    }

    private void storeList(List<AppInfo> appInfos) {
        ApplicationsList.getInstance().setList(appInfos);

        Schedulers.io().createWorker().schedule(() -> {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            Type appInfoType = new TypeToken<List<AppInfo>>() {
            }.getType();
            sharedPref.edit().putString("APPS", new Gson().toJson(appInfos, appInfoType)).apply();
        });
    }

    private Observable<File> getFileDir() {
        return Observable.create(subscriber -> {
            //subscriber.onNext(App.instance.getFilesDir());
            subscriber.onNext(getActivity().getFilesDir());
            subscriber.onCompleted();
        });
    }

    private List<AppInfo> getAppsBlocking() {
        List<AppInfo> appInfos = new ArrayList<>();

        List<AppInfoRich> apps = new ArrayList<>();
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> infos = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos)
            apps.add(new AppInfoRich(getActivity(), info));

        for (AppInfoRich appInfo : apps) {
            Bitmap icon = Utils.drawableToBitmap(appInfo.getIcon());
            String name = appInfo.getName();
            String iconPath = mFilesDir + "/" + name;
            //Utils.storeBitmap(App.instance, icon, name);
            Utils.storeBitmap(getActivity(), icon, name);

            appInfos.add(new AppInfo(name, iconPath, appInfo.getLastUpdateTime()));
        }

        return appInfos;
    }

    private Observable<List<AppInfo>> getApps() {
        //Log.d(TAG, "current thread: " + Thread.currentThread());
        return Observable.from(getAppsBlocking()).toSortedList();
    }

    private Observable<GroupedObservable<String, AppInfo>> groupedApps() {
        return Observable.from(getAppsBlocking()).groupBy(appInfo -> {
            SimpleDateFormat f = new SimpleDateFormat("MM/yyyy");
            return f.format(new Date(appInfo.getLastUpdateTime()));
        });
    }
}

