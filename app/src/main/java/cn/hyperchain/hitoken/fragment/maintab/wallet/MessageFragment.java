package cn.hyperchain.hitoken.fragment.maintab.wallet;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.adapter.MessageAdapter;
import cn.hyperchain.hitoken.entity.Message;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.interf.NetWorkInterface;
import cn.hyperchain.hitoken.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.hyperchain.hitoken.retrofit.CancelableCallback;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by admin on 2017/11/6.
 */

public class MessageFragment extends BaseBarFragment implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate
        , BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener,NetWorkInterface{

    protected BGARefreshLayout mRefreshLayout;

    protected final int pageSize = 10;   //请求个数
    protected int pageIndex = 1;  //当前请求页

    protected final int INITACTION = 9;   //初始化
    protected final int UPDATEACTION = 10;    //下拉刷新
    protected final int MOREACTION = 11;  //上拉加载
    protected boolean flag = true;    //是否可以加载更多
    protected int count = 0;  //第一次加载

    List<Message> messages = new ArrayList<>();
    MessageAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout llNull;
    RelativeLayout root;
    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_message);
        hideActionBar();
        recyclerView = rootView.findViewById(R.id.rv_message);
        llNull = rootView.findViewById(R.id.ll_null);
        root = rootView.findViewById(R.id.root);
        mRefreshLayout = rootView.findViewById(R.id.fresh_layout);
        setListener();
        initRefreshLayout();
        beginRefreshing();
    }

    protected void setListener() {
        mRefreshLayout.setDelegate(this);

        adapter = new MessageAdapter(recyclerView);
        adapter.setOnRVItemClickListener(this);
        adapter.setOnRVItemLongClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemChildLongClickListener(this);

    }

    protected void initRefreshLayout() {

        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }


    public void endRefreshing() {
        mRefreshLayout.endRefreshing();
        if(messages.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
            llNull.setVisibility(View.VISIBLE);
        } else {
//            recyclerView.setVisibility(View.VISIBLE);
            llNull.setVisibility(View.GONE);
        }
    }

    public void endLoadingMore() {
        mRefreshLayout.endLoadingMore();
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        pageIndex = 1;
        HashMap<String, String> body = new HashMap<>();
        body.put("currentpage", String.valueOf(pageIndex));
        body.put("pagesize", pageSize + "");
        if (count == 0) {
            netRequestAction(INITACTION, body);
            count++;
        } else {
            netRequestAction(UPDATEACTION, body);
        }
        flag = true;
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        int length = messages.size();
        if (length % pageSize == 0 && flag) {
            pageIndex++;
            HashMap<String, String> body = new HashMap<>();
            body.put("currentpage", String.valueOf(pageIndex));
            body.put("pagesize", pageSize + "");
            netRequestAction(MOREACTION, body);
            return false;
        } else {
            ToastUtil.showToast(getActivity(),"没有更多内容");
            return false;
        }
    }

    @Override
    public void firstRequest(Object object) {
        messages = (List<Message>) object;
        adapter.setData(messages);
        endRefreshing();
    }

    @Override
    public void refreshRequest(Object object) {
        messages = (List<Message>) object;
        adapter.setData(messages);
        endRefreshing();
    }

    @Override
    public void moreRequest(Object object) {
        List<Message> datas = (List<Message>) object;

        if (datas.size() == 0) {
            ToastUtil.showToast(getActivity(),"没有更多内容");
            flag = false;
        } else {
            messages.addAll(datas);
            adapter.setData(messages);
        }

        endLoadingMore();
    }
    @Override
    public void netRequestAction( final int action, HashMap<String, String> body) {
        int currentpage = Integer.valueOf(body.get("currentpage"));
        int pagesize = Integer.valueOf(body.get("pagesize"));
        RetrofitUtil.getService().getMessageList(currentpage,pagesize).enqueue(new CancelableCallback<MyResult<List<Message>>>() {
            @Override
            protected void onSuccess(Response<MyResult<List<Message>>> response, Retrofit retrofit) {
                MyResult result = response.body();
                if (result == null) {
                    if (action == MOREACTION) {
                        endLoadingMore();
                    } else {
                        endRefreshing();
                    }
                    return;
                }
                if (result.getStatusCode() == 200) {
                    final List<Message> datas = (List<Message>) result.getData();
                    if (action == INITACTION) {
                        firstRequest(datas);
                    } else if (action == UPDATEACTION) {
                        refreshRequest(datas);
                    } else {
                        moreRequest(datas);
                    }
                } else {
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


    @Override
    protected boolean hasBackBtn() {
        return false;
    }

    @Override
    protected boolean hasRightBtn() {
        return false;
    }

    @Override
    protected boolean isRightImg() {
        return false;
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {

    }

    @Override
    public boolean onItemChildLongClick(ViewGroup parent, View childView, int position) {
        return false;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {

    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        return false;
    }
}
