package ru.rudedude.sandbox.network.openweathermap;


import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.rudedude.sandbox.model.openweathermap.WeatherResponse;
import rx.Observable;

public interface OpenWeatherMapService {
    @GET("/data/2.5/weather")
    Observable<WeatherResponse> getForecastByCity(@Query("q") String city,
                                                  @Query("appid") String apiKey);
}
