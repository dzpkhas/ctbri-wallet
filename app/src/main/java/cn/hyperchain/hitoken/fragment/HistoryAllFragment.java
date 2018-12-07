package cn.hyperchain.hitoken.fragment;


import android.widget.Toast;

import cn.hyperchain.hitoken.entity.HistoryInfo;
import cn.hyperchain.hitoken.entity.MyResult;

import java.util.HashMap;
import java.util.List;

import cn.hyperchain.hitoken.retrofit.CancelableCallback;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by admin on 2017/11/6.
 */

public class HistoryAllFragment extends BaseHistoryFragment {

    @Override
    public void netRequestAction(final int action, HashMap<String, String> body) {

        int pageIndex = Integer.valueOf(body.get("currentpage"));
        int pageSize = Integer.valueOf(body.get("pagesize"));

        RetrofitUtil.getService().getTxlist(address,type,pageIndex,pageSize,true,true).enqueue(new CancelableCallback<MyResult<List<HistoryInfo>>>() {
            @Override
            protected void onSuccess(Response<MyResult<List<HistoryInfo>>> response, Retrofit retrofit) {
                MyResult myResult = response.body();
                if (myResult == null) {
                    if (action == MOREACTION) {
                        endLoadingMore();
                    } else {
                        endRefreshing();
                    }
                    return;
                }
                if (myResult.getStatusCode() == 200) {
                    List<HistoryInfo> datas = (List<HistoryInfo>) myResult.getData();
                    if (action == INITACTION) {
                        firstRequest(datas);
                    } else if (action == UPDATEACTION) {
                        refreshRequest(datas);
                    } else {
                        moreRequest(datas);
                    }
                } else {
                    Toast.makeText(getContext(), (String) myResult.getData(), Toast.LENGTH_SHORT).show();
                    endRefreshing();
                }
            }

            @Override
            protected void onFail(Throwable t) {
                Toast.makeText(getActivity(), "数据加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                if (action == MOREACTION) {
                    endLoadingMore();
                } else {
                    endRefreshing();
                }
            }
        });
    }
    
}
