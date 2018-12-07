package cn.hyperchain.hitoken.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.Wallet;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by admin on 2018/3/30.
 */

public class WalletAdapter extends BGARecyclerViewAdapter<Wallet> {

    public WalletAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_wallet);
    }
    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        viewHolderHelper.setItemChildClickListener(R.id.iv_send);
        viewHolderHelper.setItemChildClickListener(R.id.iv_income);
        viewHolderHelper.setItemChildClickListener(R.id.ll_switch);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, Wallet model) {
        helper.setText(R.id.tv_unix, model.getType())
                .setText(R.id.tv_name_short,model.getType())
                .setText(R.id.tv_num,"" +model.getBalance())
                .setText(R.id.tv_rmb,"" +model.getCny())
                .setImageResource(R.id.iv_icon,getIconRes(model.getType()));
        if(model.isOpen()) {
            helper.setVisibility(R.id.ll_operation,View.VISIBLE)
                .setImageResource(R.id.iv_switch,R.mipmap.up);
        } else {
            helper.setVisibility(R.id.ll_operation,View.GONE)
                    .setImageResource(R.id.iv_switch,R.mipmap.down);
        }
    }

    private int getIconRes(String nameShort) {
        if(nameShort.toUpperCase().equals("BTC")) {
            return R.mipmap.icon_btc;
        } else if(nameShort.toUpperCase().equals("ETH")) {
            return R.mipmap.icon_eth;
        }  else if(nameShort.toUpperCase().equals("USDT")) {
            return R.mipmap.icon_usdt;
        } else if(nameShort.toUpperCase().equals("BNB")) {
            return R.mipmap.icon_bnb;
        } else if(nameShort.toUpperCase().equals("OMG")) {
            return R.mipmap.icon_omg;
        } else if(nameShort.toUpperCase().equals("ZRX")) {
            return R.mipmap.icon_zrx;
        } else if(nameShort.toUpperCase().equals("ONT")) {
            return R.mipmap.icon_ont;
        } else if(nameShort.toUpperCase().equals("MKR")) {
            return R.mipmap.icon_mkr;
        }
        return 0;
    }


}
