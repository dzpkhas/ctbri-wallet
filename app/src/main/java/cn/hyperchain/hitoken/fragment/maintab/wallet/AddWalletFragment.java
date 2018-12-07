package cn.hyperchain.hitoken.fragment.maintab.wallet;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

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
import cn.hyperchain.hitoken.adapter.WalletInfoAdapter;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;
import cn.hyperchain.hitoken.entity.WalletInfo;
import cn.hyperchain.hitoken.entity.post.AddWalletBody;
import cn.hyperchain.hitoken.entity.post.WalletItem;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.interf.NetWorkInterface;
import cn.hyperchain.hitoken.retrofit.CancelableCallback;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by admin on 2017/11/6.
 */

public class AddWalletFragment extends BaseBarFragment implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate
        , BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener,NetWorkInterface {

    protected BGARefreshLayout mRefreshLayout;

    protected final int INITACTION = 9;   //初始化
    protected final int UPDATEACTION = 10;    //下拉刷新
    protected final int MOREACTION = 11;  //上拉加载

    List<WalletInfo> walletInfos = new ArrayList<>();
    WalletInfoAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout root;

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_add_wallet);
        hideActionBar();
        recyclerView = rootView.findViewById(R.id.rv_base_house);
        mRefreshLayout = rootView.findViewById(R.id.fresh_layout);
        root = rootView.findViewById(R.id.root);
        setListener();
        initRefreshLayout();
        beginRefreshing();
    }

    @Override
    public void initData() {
        super.initData();
        WalletInfo walletInfo = null;
        walletInfo = new WalletInfo("BTC","Bitcoin",R.mipmap.icon_btc,"0x0d8775…0d2887ef",-1);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("ETH","Ethereum",R.mipmap.icon_eth,"0x0d8775…0d2887ef",-1);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("USDT","Tether",R.mipmap.icon_usdt,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("BNB","Credo",R.mipmap.icon_bnb,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("OMG","OmiseGO",R.mipmap.icon_omg,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("ZRX","OmiseGO",R.mipmap.icon_zrx,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("ONT","0xproject",R.mipmap.icon_ont,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);

        walletInfo = new WalletInfo("MKR","Maker",R.mipmap.icon_mkr,"0x0d8775…0d2887ef",0);
        walletInfos.add(walletInfo);
    }

    protected void setListener() {
        mRefreshLayout.setDelegate(this);

        adapter = new WalletInfoAdapter(recyclerView);
        adapter.setOnRVItemClickListener(this);
        adapter.setOnRVItemLongClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemChildLongClickListener(this);

    }


    protected void initRefreshLayout() {

        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), false);
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

    }

    public void endLoadingMore() {
        mRefreshLayout.endLoadingMore();
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        HashMap<String, String> body = new HashMap<>();
        netRequestAction(INITACTION, body);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
       return false;
    }

    @Override
    public void firstRequest(Object object) {
        adapter.setData(walletInfos);
        endRefreshing();
    }

    @Override
    public void refreshRequest(Object object) {
        adapter.setData(walletInfos);
        endRefreshing();
    }

    @Override
    public void moreRequest(Object object) {

    }

    @Override
    public void netRequestAction( final int action, HashMap<String, String> body) {
        RetrofitUtil.getService().getWallet().enqueue(new CancelableCallback<MyResult<List<Wallet>>>() {
            @Override
            protected void onSuccess(Response<MyResult<List<Wallet>>> response, Retrofit retrofit) {
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
                    ArrayList<Wallet> wallets = (ArrayList<Wallet>)result.getData();

                    for(int i = 0; i < wallets.size();i++) {
                        Wallet wallet = wallets.get(i);
                        for(int j=0; j < walletInfos.size();j++) {
                            if(wallet.getType().toUpperCase().equals(walletInfos.get(j).getNameShort())) {
                                if(walletInfos.get(j).getOpen() != -1) {
                                    walletInfos.get(j).setOpen(1);
                                    break;
                                }
                            }
                        }

                    }
                    if (action == INITACTION) {
                        firstRequest(walletInfos);
                    } else if (action == UPDATEACTION) {
                        refreshRequest(walletInfos);
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
    public void onItemChildClick(ViewGroup parent, final View childView, final int position) {
        if(childView.getId() == R.id.iv_switch) {
            if(walletInfos.get(position).getOpen() == 0) {

                WalletItem walletItem = new WalletItem("unknown","unknown",walletInfos.get(position).getNameShort());
                AddWalletBody addWalletBody = new AddWalletBody();
                ArrayList<WalletItem> walletItems = new ArrayList<>();
                walletItems.add(walletItem);
                addWalletBody.setWallets(walletItems);

                Gson gson = new Gson();
                String json = gson.toJson(addWalletBody);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                RetrofitUtil.getService().addWallet(body).enqueue(new Callback<MyResult>() {
                    @Override
                    public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                        hideDialog();
                        MyResult result = response.body();
                        if(result.getStatusCode() == 200) {
                            walletInfos.get(position).setOpen(1);
                            adapter.notifyItemChanged(position);
                        } else {
                            showToast((String)result.getData());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        hideDialog();
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                showToast("无法删除钱包");
            }
        }
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
