package cn.hyperchain.hitoken.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.adapter.AddressAdapter;
import cn.hyperchain.hitoken.entity.Address;
import cn.hyperchain.hitoken.entity.AddressBook;
import cn.hyperchain.hitoken.entity.AddressRecent;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.post.ID;
import cn.hyperchain.hitoken.interf.NetWorkInterface;
import cn.hyperchain.hitoken.view.SwipeItemLayout;
import cn.hyperchain.hitoken.view.letter.LetterListView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.hyperchain.hitoken.retrofit.CancelableCallback;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AddressBookActivity extends TemplateActivity  implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate
        , BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener,NetWorkInterface{


    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.ll_add_address)
    LinearLayout llAddAddress;


    @BindView(R.id.letter)
    LetterListView letter;

    private AddressAdapter adapter;
    LinearLayoutManager manager;

    protected BGARefreshLayout mRefreshLayout;

    protected final int pageSize = 1000;   //请求个数
    protected int pageIndex = 1;  //当前请求页

    protected final int INITACTION = 9;   //初始化
    protected final int UPDATEACTION = 10;    //下拉刷新
    protected final int MOREACTION = 11;  //上拉加载
    protected boolean flag = true;    //是否可以加载更多
    protected int count = 0;  //第一次加载

    List<AddressBook> addressBookList = new ArrayList<>();
    List<AddressRecent> addressRecentList = new ArrayList<>();
    RecyclerView recyclerView;

    //from = 0 来自mainActivity
    //from =1 来自 eth sendActivity
    //from = 2 来自 btc sendActivity
    int from;
    @Override
    public void initView() {
        super.initView();
        hideTitleBar();

        recyclerView = root.findViewById(R.id.recyclerView);
        mRefreshLayout = root.findViewById(R.id.fresh_layout);
        letter.setOnTouchingLetterChangedListener(new LetterListView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                for(int i=0;i<addressBookList.size();i++) {
                    if(addressBookList.get(i).getFirst_char().equals(s)) {
                        recyclerView.smoothScrollToPosition(i);
                        break;
                    }
                }
            }
        });
        Intent intent = getIntent();
        from = intent.getIntExtra("from",0);
        if(from == 0) {
            recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getBaseContext()));
        }
        setListener();
        initRefreshLayout();
    }

    protected void setListener() {
        mRefreshLayout.setDelegate(this);

        adapter = new AddressAdapter(recyclerView);
        adapter.setOnRVItemClickListener(this);
        adapter.setOnRVItemLongClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemChildLongClickListener(this);

    }

    protected void initRefreshLayout() {

        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beginRefreshing();
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
        int length = addressBookList.size();
        if (length % pageSize == 0 && flag) {
            pageIndex++;
            HashMap<String, String> body = new HashMap<>();
            body.put("currentpage", String.valueOf(pageIndex));
            body.put("pagesize", pageSize + "");
            netRequestAction(MOREACTION, body);
            return true;
        } else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void firstRequest(Object object) {
        addressBookList = (List<AddressBook>) object;
        adapter.setData(addressBookList);
        endRefreshing();
    }


    @Override
    public void refreshRequest(Object object) {
        List<AddressBook> datas = (List<AddressBook>) object;
        addressBookList = datas;
        adapter.setData(addressBookList);
        endRefreshing();
    }

    @Override
    public void moreRequest(Object object) {
        List<AddressBook> datas = (List<AddressBook>) object;

        if (datas == null || datas.size() == 0) {
            Toast.makeText(AddressBookActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
            flag = false;
        } else {
            addressBookList.addAll(datas);
            adapter.setData(addressBookList);
        }

        endLoadingMore();
    }

    @Override
    public void netRequestAction( final int action, HashMap<String, String> body) {
        int pageIndex = Integer.valueOf(body.get("currentpage"));
        int pageSize = Integer.valueOf(body.get("pagesize"));
        String type = null;
        if(from == 1) {
            type = "ETH";
        } else if(from == 2) {
            type = "BTC";
        }
        RetrofitUtil.getService().getAddress(pageIndex,pageSize,type).enqueue(new CancelableCallback<MyResult<Address>>() {
            @Override
            protected void onSuccess(Response<MyResult<Address>> response, Retrofit retrofit) {
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
                    Address address = (Address) myResult.getData();

                    List<AddressBook> datas = address.getBooks();
                    List<AddressRecent> datas2 = ( List<AddressRecent>)address.getRecent();
                    if (action == INITACTION) {

                        if(address.getRecent() != null){
                            for(int i=0;i<datas2.size();i++) {
                                AddressBook addressBook = new AddressBook();
                                addressBook.setAddress(datas2.get(i).getAddress());
                                addressBook.setUser_name(datas2.get(i).getUser_name());
                                addressBook.setTime(datas2.get(i).getTime());
                                addressBook.setFirst_char("最近");
                                datas.add(0,addressBook);
                            }
                        }
                        firstRequest(datas);
                    } else if (action == UPDATEACTION) {
                        if(address.getRecent() != null){
                            for(int i=0;i<datas2.size();i++) {
                                AddressBook addressBook = new AddressBook();
                                addressBook.setAddress(datas2.get(i).getAddress());
                                addressBook.setUser_name(datas2.get(i).getUser_name());
                                addressBook.setTime(datas2.get(i).getTime());
                                addressBook.setFirst_char("最近");
                                datas.add(0,addressBook);
                            }
                        }
                        refreshRequest(datas);
                    } else {
                        moreRequest(datas);
                    }
                } else {
                    showToast((String) myResult.getData());
                    endRefreshing();
                }
            }

            @Override
            protected void onFail(Throwable t) {
                showToast("数据加载失败，请检查网络");
                if (action == MOREACTION) {
                    endLoadingMore();
                } else {
                    endRefreshing();
                }
            }
        });
    }



    @OnClick({
            R.id.ll_back,R.id.ll_add_address
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_add_address:
                intent = new Intent(AddressBookActivity.this,AddAddressActivity.class);
                intent.putExtra("from",0);
                startActivity(intent);
                break;

        }
    }




    @Override
    public void onItemChildClick(ViewGroup parent, View childView, final int position) {
        if(childView.getId() == R.id.bt_delete) {
            AddressBook addressBook = addressBookList.get(position);
            ID id = new ID();
            id.setId(addressBook.getId());
            Gson gson = new Gson();
            String json = gson.toJson(id);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            RetrofitUtil.getService().deleteAddress(body).enqueue(new Callback<MyResult>() {
                @Override
                public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                    hideDialog();
                    MyResult result = response.body();
                    if(result.getStatusCode() == 200) {
                        showToast("删除联系人成功");
                        addressBookList.remove(position);
                        adapter.setData(addressBookList);
                    } else {
                        showToast((String)result.getData());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    hideDialog();
                    Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else if(childView.getId() == R.id.bt_edit) {
            AddressBook addressBook = addressBookList.get(position);
            Intent intent=new Intent(AddressBookActivity.this, AddAddressActivity.class);
            intent.putExtra("from",1);
            intent.putExtra("nickname",addressBook.getUser_name());
            intent.putExtra("id",addressBook.getId());
            intent.putExtra("btcAddress",addressBook.getBtc_address());
            intent.putExtra("ethAddress",addressBook.getEth_address());
            startActivity(intent);
        } else if(childView.getId() == R.id.ll_main) {
            AddressBook addressBook = addressBookList.get(position);
            if(from == 0) {
                Intent intent=new Intent(AddressBookActivity.this, AddressDetailActivity.class);
                intent.putExtra("nickname",addressBook.getUser_name());
                intent.putExtra("btcAddress",addressBook.getBtc_address());
                intent.putExtra("ethAddress",addressBook.getEth_address());
                startActivity(intent);
            } else {
                Intent intent = getIntent();
                if(addressBook.getFirst_char().equals("最近")) {
                    intent.putExtra("result", addressBook.getAddress());
                } else {
                    if(from == 1) {
                        intent.putExtra("result", addressBook.getEth_address());
                    } else if(from == 2) {
                        intent.putExtra("result", addressBook.getBtc_address());
                    }
                }

                setResult(RESULT_OK, intent);
                finish();
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

    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.SHOW_IMG;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_address_book;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
