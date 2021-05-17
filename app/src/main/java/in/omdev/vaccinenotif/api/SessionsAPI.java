package in.omdev.vaccinenotif.api;

import in.omdev.vaccinenotif.entity.Centers;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SessionsAPI {


    @Headers({"User-Agent: "})
    @GET
    Call<Centers> getSessionsByPin(@Url String url, @Query("pincode") String pincode,
                                   @Query("date") String date);

    @Headers({"User-Agent: "})
    @GET
    Call<String> getSessionsStringByPin(@Url String url, @Query("pincode") String pincode,
                                     @Query("date") String date);
    @Headers({"User-Agent: "})
    @GET
    Call<Centers> getSessionsByDistrict(@Url String url, @Query("district_id") int district_id,
                                        @Query("date") String date);
}
