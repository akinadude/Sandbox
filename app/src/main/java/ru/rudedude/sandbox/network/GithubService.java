package ru.rudedude.sandbox.network;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.rudedude.sandbox.model.GithubRepo;
import rx.Observable;

public interface GithubService {
    @GET("users/{user}/starred")
    Observable<List<GithubRepo>> getStarredRepositories(@Path("user") String userName);
}
