package cn.hyperchain.hitoken.view.fileDisplay;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

/**
 * Created by 12457 on 2017/8/21.
 */

public interface LoadFileApi {

    @GET
    Call<ResponseBody> loadPdfFile(@Url String fileUrl);

}
