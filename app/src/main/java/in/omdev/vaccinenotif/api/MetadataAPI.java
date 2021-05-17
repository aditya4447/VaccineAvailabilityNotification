package in.omdev.vaccinenotif.api;

import in.omdev.vaccinenotif.entity.Districts;
import in.omdev.vaccinenotif.entity.States;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface MetadataAPI {

    @Headers({"User-Agent: "})
    @GET
    Call<States> getStates(@Url String url);

    @Headers({"User-Agent: "})
    @GET
    Call<Districts> getDistricts(@Url String url);



}
