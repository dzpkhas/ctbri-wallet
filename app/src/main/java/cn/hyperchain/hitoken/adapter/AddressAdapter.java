package cn.hyperchain.hitoken.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.AddressBook;
import cn.hyperchain.hitoken.utils.DateUtils;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by admin on 2018/3/30.
 */

public class AddressAdapter extends BGARecyclerViewAdapter<AddressBook> {

    public AddressAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_address);
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        viewHolderHelper.setItemChildClickListener(R.id.bt_edit);
        viewHolderHelper.setItemChildClickListener(R.id.bt_delete);
        viewHolderHelper.setItemChildClickListener(R.id.ll_main);
    }



    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, AddressBook addressBook) {
        if(position != 0 ) {
            String preChar = getData().get(position - 1).getFirst_char();

            if(preChar.equals(addressBook.getFirst_char())) {
                helper.setVisibility(R.id.ll_tag,View.GONE);
            } else {
                helper.setVisibility(R.id.ll_tag,View.VISIBLE);
                helper.setText(R.id.tv_tag,addressBook.getFirst_char());
            }
        } else {
            helper.setVisibility(R.id.ll_tag,View.VISIBLE);
            helper.setText(R.id.tv_tag,addressBook.getFirst_char());
            if(addressBook.getFirst_char().equals("最近")) {
                helper.setVisibility(R.id.ll_tag,View.VISIBLE)
                        .setImageResource(R.id.iv_circle,R.mipmap.circle_yellow);
            } else {
                helper.setImageResource(R.id.iv_circle,R.mipmap.circle);
            }
        }

        helper.setText(R.id.tv_name,addressBook.getUser_name());
        if(addressBook.getFirst_char().equals("最近")) {
            helper.setVisibility(R.id.tv_time,View.VISIBLE)
                    .setText(R.id.tv_time,DateUtils.date1(addressBook.getTime()));
            if(addressBook.getUser_name().equals("")) {
                helper.setText(R.id.tv_name,addressBook.getAddress());
            }
        } else {
            helper.setVisibility(R.id.tv_time,View.GONE);
        }
    }


}
