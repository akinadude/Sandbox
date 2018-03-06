package ru.rudedude.sandbox.viewholder;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.rudedude.sandbox.App;
import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.model.ModelEntry;
import ru.rudedude.sandbox.model.openweathermap.WeatherResponse;
import ru.rudedude.sandbox.model.stackexchange.User;
import ru.rudedude.sandbox.network.openweathermap.OpenWeatherMapApiManager;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

public class SeUserViewHolder extends BaseViewHolder {

    private static final String TAG = "SeUserViewHolder";

    public interface OpenProfileListener {
        void openProfile(String url);
    }

    private TextView name;
    private TextView city;
    private TextView reputation;
    private ImageView userImage;
    private ImageView cityImage;

    private static SeUserViewHolder.OpenProfileListener mProfileListener;

    public SeUserViewHolder(View itemView, OpenProfileListener listener) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.se_user_name);
        city = (TextView) itemView.findViewById(R.id.se_user_city);
        reputation = (TextView) itemView.findViewById(R.id.se_user_reputation);
        userImage = (ImageView) itemView.findViewById(R.id.se_user_profile_image);
        cityImage = (ImageView) itemView.findViewById(R.id.se_user_city_image);

        mProfileListener = listener;
    }

    @Override
    public void bind(ModelEntry me) {
        User user = (User) me;
        name.setText(user.getDisplayName());
        city.setText(user.getLocation());
        reputation.setText(String.valueOf(user.getReputation()));

        ImageLoader.getInstance().displayImage(user.getProfileImage(), userImage);

        displayWeatherInfos(user);

        RxView.clicks(itemView).subscribe(onClickEvent -> {
            checkNotNull(mProfileListener, "Must implement OpenProfileListener");

            String url = user.getWebsiteUrl();
            if (url != null && !url.equals("") && !url.contains("search")) {
                mProfileListener.openProfile(url);
            } else {
                mProfileListener.openProfile(user.getLink());
            }
        });
    }

    private Observable<Bitmap> loadBitmap(String url) {
        return Observable.create(subscriber -> {
            ImageLoader.getInstance().displayImage(url, cityImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    subscriber.onError(failReason.getCause());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    subscriber.onNext(loadedImage);
                    subscriber.onCompleted();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    subscriber.onError(new Throwable("Image loading cancelled"));
                }
            });
        });
    }

    private void displayWeatherInfos(User user) {
        String location = user.getLocation();
        int separatorPosition = getSeparatorPosition(location);

        if (isCityValid(location)) {
            String city = getCity(location, separatorPosition);
            App.L.debug(TAG, "city: " + city);
            OpenWeatherMapApiManager.getInstance()
                    .getForecastByCity(city)
                    .filter(response -> response != null)
                    .filter(response -> response.getWeather().size() > 0)
                    .flatMap(response -> {
                        String url = getWeatherIconUrl(response);
                        App.L.debug("weather img url: " + url);
                        return loadBitmap(url);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            App.L.error(e.toString());
                        }

                        @Override
                        public void onNext(Bitmap icon) {
                            cityImage.setImageBitmap(icon);
                        }
                    });
        }
    }

    private String getWeatherIconUrl(WeatherResponse weatherResponse) {
        return "http://openweathermap.org/img/w/"
                + weatherResponse.getWeather().get(0).getIcon()
                + ".png";
    }

    private boolean isCityValid(String location) {
        int separatorPosition = getSeparatorPosition(location);
        return !"".equals(location) && separatorPosition > -1;
    }

    private int getSeparatorPosition(String location) {
        int separatorPosition = -1;
        checkNotNull(location, "Location can't be null");
        separatorPosition = location.indexOf(",");
        return separatorPosition;
    }

    private String getCity(String location, int position) {
        if (location != null) {
            return location.substring(0, position);
        } else {
            return "";
        }
    }
}
