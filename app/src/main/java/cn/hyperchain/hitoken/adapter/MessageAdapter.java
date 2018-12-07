package cn.hyperchain.hitoken.adapter;

import android.support.v7.widget.RecyclerView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.Message;
import cn.hyperchain.hitoken.utils.DateUtils;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by admin on 2018/3/30.
 */

public class MessageAdapter extends BGARecyclerViewAdapter<Message> {

    public MessageAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_message);
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {

    }



    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, Message model) {
        Double value = new Double(model.getValue());
        value = value / 1e18;
        String tran = model.getType().toUpperCase() + ": " + value + " " + "转账" + model.getStatus();
        helper.setText(R.id.tv_tran,tran)
                .setText(R.id.tv_tran_time,DateUtils.date2(model.getTime()));
        if(model.getPlus_or_minus().equals("转账")) {
            helper.setText(R.id.tv_tran_type,"收款方:");
        } else {
            helper.setText(R.id.tv_tran_type,"付款方:");
        }
        helper.setText(R.id.tv_name,model.getAddress_or_name());
        if(position < 3) {
            helper.setBackgroundColor(R.id.root,0x0A206EE7);
        } else {
            helper.setBackgroundColor(R.id.root,0xFFFFFFFF);
        }
    }


}
