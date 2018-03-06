package ru.rudedude.sandbox.network.stackexchange;


import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.rudedude.sandbox.model.stackexchange.UsersResponse;
import rx.Observable;

public interface StackExchangeService {
    @GET("/2.2/users?order=desc&pagesize=10&sort=reputation&site=stackoverflow")
    Observable<UsersResponse> getTenMostPopularSOusers();

    @GET("/2.2/users?order=desc&sort=reputation&site=stackoverflow")
    Observable<UsersResponse> getMostPopularSoUsers(@Query("pagesize") int howmany);
}
