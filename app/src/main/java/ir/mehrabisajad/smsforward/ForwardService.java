package ir.mehrabisajad.smsforward;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ForwardService {
  @GET("/")
  Call<Void> send(@Query("code") String code);
}