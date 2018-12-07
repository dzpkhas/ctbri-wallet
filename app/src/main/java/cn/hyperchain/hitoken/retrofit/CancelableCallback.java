package cn.hyperchain.hitoken.retrofit;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 2016/7/29.
 */
public abstract class CancelableCallback<T> implements Callback<T> {

    private boolean canceled;
    private Throwable t;

    public CancelableCallback() {
        this.canceled = false;
    }

    public void cancel() {
        canceled = true;
    }

    protected abstract void onSuccess(Response<T> response, Retrofit retrofit);

    protected abstract void onFail(Throwable t);

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
        if (canceled) {
            return;
        }
        onSuccess(response, retrofit);
    }

    @Override
    public void onFailure(Throwable t) {
        if (canceled) {
            return;
        }
        onFail(t);
    }
}
