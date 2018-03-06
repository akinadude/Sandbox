package ru.rudedude.sandbox.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
//import com.rey.material.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.rudedude.sandbox.App;
import ru.rudedude.sandbox.R;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DownloadFileFragment extends Fragment {

    private ArcProgress mProgress;
    private Button mDownloadBtn;
    private PublishSubject<Integer> mProgressPs = PublishSubject.create();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_file, container, false);
        mProgress = (ArcProgress) view.findViewById(R.id.arc_progress);
        mDownloadBtn = (Button) view.findViewById(R.id.button_download);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDownloadBtn.setOnClickListener(v -> {
            mDownloadBtn.setText(getString(R.string.downloading));
            mDownloadBtn.setClickable(false);

            mProgressPs.distinct()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override public void onCompleted() {
                            App.L.debug("Completed");
                        }

                        @Override public void onError(Throwable e) {
                            App.L.error(e.toString());
                        }

                        @Override public void onNext(Integer progress) {
                            mProgress.setProgress(progress);
                        }
                    });

            String destination = "/sdcard/softboy.avi";

            obserbableDownload("http://archive.blender.org/fileadmin/movies/softboy.avi",
                    destination).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success -> {
                        resetDownloadButton();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        File file = new File(destination);
                        intent.setDataAndType(Uri.fromFile(file), "video/avi");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }, error -> {
                        Toast.makeText(getActivity(), "Something went south", Toast.LENGTH_SHORT).show();
                        resetDownloadButton();
                    });
        });
    }

    private Observable<Boolean> obserbableDownload(String source, String destination) {
        return Observable.create(subscriber -> {
            try {
                boolean result = downloadFile(source, destination);
                if (result) {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("Download failed."));
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private boolean downloadFile(String source, String destination) {
        boolean result = false;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(source);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();
            output = new FileOutputStream(destination);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;

                if (fileLength > 0) {
                    int percentage = (int) (total * 100 / fileLength);
                    mProgressPs.onNext(percentage);
                }
                output.write(data, 0, count);
            }
            mProgressPs.onCompleted();
            result = true;
        } catch (Exception e) {
            mProgressPs.onError(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                mProgressPs.onError(e);
            }

            if (connection != null) {
                connection.disconnect();
                mProgressPs.onCompleted();
            }
        }
        return result;
    }

    private void resetDownloadButton() {
        mDownloadBtn.setText(getString(R.string.download));
        mDownloadBtn.setClickable(true);
        mProgress.setProgress(0);
    }
}
