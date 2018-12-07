package cn.hyperchain.hitoken.fragment;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.TranDetailActivity;
import cn.hyperchain.hitoken.adapter.BaseHistoryAdapter;
import cn.hyperchain.hitoken.adapter.OnItemClickLitener;
import cn.hyperchain.hitoken.entity.HistoryInfo;
import cn.hyperchain.hitoken.interf.NetWorkInterface;
import cn.hyperchain.hitoken.utils.SPHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by admin on 2017/11/6.
 */

public class BaseHistoryFragment extends BaseBarFragment implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate, NetWorkInterface {

    protected BGARefreshLayout mRefreshLayout;

    protected final int pageSize = 10;   //请求个数
    protected int pageIndex = 1;  //当前请求页
    protected String address = "0";
    protected String type = "";

    protected final int INITACTION = 9;   //初始化
    protected final int UPDATEACTION = 10;    //下拉刷新
    protected final int MOREACTION = 11;  //上拉加载
    protected boolean flag = true;    //是否可以加载更多
    protected int count = 0;  //第一次加载

    List<HistoryInfo> historyInfos = new ArrayList<>();
    BaseHistoryAdapter adapter;
    RecyclerView recyclerView;


    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_base_history);
        hideActionBar();

        recyclerView = rootView.findViewById(R.id.rv_base_history);
        type = getArguments().getString("type","");
        if(type.equals("ETH")) {
            address = (String) SPHelper.get(getContext(),"ethAddress","");
        } else {
            address = "0";
        }
        initRefreshLayout(rootView);
        initListView();
        beginRefreshing();
    }

    @Override
    public void initData() {
        super.initData();

    }


    protected void initRefreshLayout(View rootView) {
        mRefreshLayout = rootView.findViewById(R.id.fresh_layout);
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    protected void initListView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new BaseHistoryAdapter(historyInfos,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), TranDetailActivity.class);
                intent.putExtra("id",historyInfos.get(position).getId());
                int tranType = 0;
                if(historyInfos.get(position).getValue() > 0 ) {
                    tranType = 1;
                } else {
                    tranType = 0;
                }
                intent.putExtra("tranType",tranType);
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    public void endRefreshing() {
        mRefreshLayout.endRefreshing();
//        if (admissionsInfoList.size() == 0) {
//            bgView.setVisibility(View.VISIBLE);
//        } else {
//            bgView.setVisibility(View.INVISIBLE);
//        }
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
        int length = historyInfos.size();
        if (length % pageSize == 0 && flag) {
            pageIndex++;
            HashMap<String, String> body = new HashMap<>();
            body.put("currentpage", String.valueOf(pageIndex));
            body.put("pagesize", pageSize + "");
            netRequestAction(MOREACTION, body);
            return true;
        } else {
            Toast.makeText(getActivity(), "没有更多内容", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void firstRequest(Object object) {
        historyInfos = (List<HistoryInfo>) object;
        adapter.updateItem(historyInfos);
        endRefreshing();
    }

    @Override
    public void refreshRequest(Object object) {
        List<HistoryInfo> datas = (List<HistoryInfo>) object;

        if (datas.size() == 0 || historyInfos.size() == 0) {
            historyInfos = datas;
            adapter.updateItem(historyInfos);
        } else {
            historyInfos = datas;
            adapter.updateItem(historyInfos);
        }
        endRefreshing();
    }

    @Override
    public void moreRequest(Object object) {
        List<HistoryInfo> datas = (List<HistoryInfo>) object;

        if (datas == null || datas.size() == 0) {
            Toast.makeText(getActivity(), "无更多数据", Toast.LENGTH_SHORT).show();
            flag = false;
        } else {
            historyInfos.addAll(datas);
            adapter.updateItem(historyInfos);
        }

        endLoadingMore();
    }

    @Override
    public void netRequestAction( final int action, HashMap<String, String> body) {

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
}
