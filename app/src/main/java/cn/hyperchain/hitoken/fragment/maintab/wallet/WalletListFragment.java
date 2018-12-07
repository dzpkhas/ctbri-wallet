package cn.hyperchain.hitoken.fragment.maintab.wallet;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.DetailActivity;
import cn.hyperchain.hitoken.activity.GatherActivity;
import cn.hyperchain.hitoken.activity.SendActivity;
import cn.hyperchain.hitoken.adapter.WalletAdapter;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.interf.NetWorkInterface;
import cn.hyperchain.hitoken.thread.WalletRefreshThread;
import cn.hyperchain.hitoken.utils.SPHelper;

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

public class WalletListFragment extends BaseBarFragment implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate
        , BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener,NetWorkInterface {

    protected BGARefreshLayout mRefreshLayout;

    protected final int INITACTION = 9;   //初始化
    protected final int UPDATEACTION = 10;    //下拉刷新
    protected final int MOREACTION = 11;  //上拉加载

    List<Wallet> walletInfos = new ArrayList<>();
    WalletAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout root;

    WalletRefreshThread thread;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case WalletRefreshThread.SUCCESSFORINDUCTIONCOUNT:
                    ArrayList<Wallet> wallets = (ArrayList<Wallet>) msg.obj;
                    Wallet wallet = null;
                    for(int i = 0; i < wallets.size();i++) {
                        wallet = wallets.get(i);
                        if(wallet.getType().toUpperCase().equals("ETH")) {
                            SPHelper.put(getActivity(),"ethAddress",wallet.getAddress());
                            SPHelper.put(getActivity(),"ethNonce",wallet.getNonce());
                            SPHelper.put(getActivity(),"ethAccountId",wallet.getAccount_id());
                            MainFragment walletFragment = (MainFragment) getParentFragment();
                            walletFragment.tvProperty.setText("" + wallet.getCny());
                            break;

                        }
                    }
                    if(wallet != null) {
                        for(int i=0;i<walletInfos.size();i++) {
                            if(walletInfos.get(i).getType().toUpperCase().equals("ETH")) {
                                walletInfos.get(i).setBalance(wallet.getBalance());
                                walletInfos.get(i).setCny(wallet.getCny());
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }
                    }
//                    showToast("main定时器启动");
                    break;

            }

        }

    };

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_wallet_list);
        hideActionBar();
        recyclerView = rootView.findViewById(R.id.recyclerview);
        mRefreshLayout = rootView.findViewById(R.id.fresh_layout);
        root = rootView.findViewById(R.id.root);
        setListener();
        initRefreshLayout();
        beginRefreshing();
    }

    private void initThreads() {
        thread = new WalletRefreshThread(handler);
        thread.run();

    }

    @Override
    public void onResume() {
        super.onResume();
        initThreads();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(thread);
//        showToast("main thead 销毁");
    }
    protected void setListener() {
        mRefreshLayout.setDelegate(this);

        adapter = new WalletAdapter(recyclerView);
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
                    walletInfos = (ArrayList<Wallet>)result.getData();
                    for(int i=0;i<walletInfos.size();i++) {
                        Wallet wallet = walletInfos.get(i);
                        if(wallet.getType().toUpperCase().equals("ETH")) {
                            SPHelper.put(getActivity(),"ethAddress",wallet.getAddress());
                            SPHelper.put(getActivity(),"ethNonce",wallet.getNonce());
                            SPHelper.put(getActivity(),"ethAccountId",wallet.getAccount_id());
                            MainFragment walletFragment = (MainFragment) getParentFragment();
                            walletFragment.tvProperty.setText("" + wallet.getCny());
                            break;
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
        if(childView.getId() == R.id.iv_send) {
            if(walletInfos.get(position).getType().toUpperCase().equals("ETH")) {
                Intent intent = new Intent(getActivity(),SendActivity.class);
                intent.putExtra("type","ETH");
                startActivity(intent);
            } else {
                showToast("btc 暂不支持");
            }
        } else if(childView.getId() == R.id.iv_income) {
            Intent intent = new Intent(getActivity(),GatherActivity.class);
            intent.putExtra("type",walletInfos.get(position).getType().toUpperCase());
            startActivity(intent);
        } else if(childView.getId() == R.id.ll_switch) {
            if(!walletInfos.get(position).isOpen()) {
                walletInfos.get(position).setOpen(true);
                adapter.notifyItemChanged(position);
            } else {
                walletInfos.get(position).setOpen(false);
                adapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public boolean onItemChildLongClick(ViewGroup parent, View childView, int position) {
        return false;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        Intent intent = new Intent(getActivity(),DetailActivity.class);
        intent.putExtra("type",walletInfos.get(position).getType().toUpperCase());
        startActivity(intent);
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        return false;
    }
}
