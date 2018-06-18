package ai.suntrust.swihighprecisioncassavafarm;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("/api/farm/activate1/{id}")
    Call<String> activateFarm1(@Path(value="id", encoded=true) String id);

    @POST("/api/farm/deactivate1/{id}")
    Call<String> deactivateFarm1(@Path(value="id", encoded=true) String id);

    @POST("/api/farm/set/sampling_time/{id}/{value}")
    Call<String> setFarmSamplingTime(@Path(value="id", encoded=true) String id,
                                 @Path(value="value", encoded=true) String value);

    @POST("/api/farm/set/valve1/channel/{id}/{value}")
    Call<String> setValve1Channel(@Path(value="id", encoded=true) String id,
                                 @Path(value="value", encoded=true) String value);

    @POST("/api/farm/set/valve2/channel/{id}/{value}")
    Call<String> setValve2Channel(@Path(value="id", encoded=true) String id,
                                  @Path(value="value", encoded=true) String value);

    @POST("/api/farm/set/humidity/channel/{id}/{value}")
    Call<String> setHumidityChannel(@Path(value="id", encoded=true) String id,
                                  @Path(value="value", encoded=true) String value);

    @POST("/api/farm/set/humidity_critical_point/{id}/{value}")
    Call<String> setHumidityCriticalPoint(@Path(value="id", encoded=true) String id,
                                    @Path(value="value", encoded=true) String value);

    @POST("/api/farm/set/rain/channel/{id}/{value}")
    Call<String> setRainChannel(@Path(value="id", encoded=true) String id,
                                    @Path(value="value", encoded=true) String value);

    @POST("/api/mainpump/turnon/{id}/{key}/{field}")
    Call<String> turnonMainpump(@Path(value="id", encoded=true) String id,
                                @Path(value="key", encoded=true) String key,
                                @Path(value="field", encoded=true) String field);

    @POST("/api/mainpump/turnoff/{id}/{key}/{field}")
    Call<String> turnoffMainpump(@Path(value="id", encoded=true) String id,
                                 @Path(value="key", encoded=true) String key,
                                 @Path(value="field", encoded=true) String field);

    @POST("/api/valve/turnon/{id}/{key}/{field}")
    Call<String> turnonValve(@Path(value="id", encoded=true) String id,
                                @Path(value="key", encoded=true) String key,
                                @Path(value="field", encoded=true) String field);

    @POST("/api/valve/turnoff/{id}/{key}/{field}")
    Call<String> turnoffValve(@Path(value="id", encoded=true) String id,
                                 @Path(value="key", encoded=true) String key,
                                 @Path(value="field", encoded=true) String field);

    @POST("/api/valve/reset_timer/{id}/{field}")
    Call<String> resetValveTimer(@Path(value="id", encoded=true) String id,
                              @Path(value="field", encoded=true) String field);

}
