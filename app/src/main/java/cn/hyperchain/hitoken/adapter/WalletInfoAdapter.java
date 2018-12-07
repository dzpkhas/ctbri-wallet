package cn.hyperchain.hitoken.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.WalletInfo;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by admin on 2018/3/30.
 */

public class WalletInfoAdapter extends BGARecyclerViewAdapter<WalletInfo> {

    public WalletInfoAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_add_wallet);
    }
    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        viewHolderHelper.setItemChildClickListener(R.id.iv_switch);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, WalletInfo model) {
        helper.setText(R.id.tv_name, model.getName())
                .setText(R.id.tv_name_short,model.getNameShort())
                .setText(R.id.tv_token,model.getToken())
                .setImageResource(R.id.iv_icon,model.getIconRes());
        if(model.getOpen() == -1) {
            helper.setVisibility(R.id.iv_switch,View.GONE);
        } else if(model.getOpen() == 0) {
            helper.setImageResource(R.id.iv_switch,R.mipmap.switch_close);
        }else if(model.getOpen() == 1) {
            helper.setImageResource(R.id.iv_switch,R.mipmap.switch_open);
        }

    }


}
