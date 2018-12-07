package cn.hyperchain.hitoken.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.HistoryInfo;
import cn.hyperchain.hitoken.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by admin on 2018/3/30.
 */

public class BaseHistoryAdapter extends RecyclerView.Adapter<BaseHistoryAdapter.ViewHolder> {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    Context mcontext;

    public BaseHistoryAdapter(List<HistoryInfo> historyInfos, Context mcontext) {
        this.historyInfos = historyInfos;
        this.mcontext = mcontext;
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(historyInfos.get(position).getUser_name());
        holder.tv_desp.setText(historyInfos.get(position).getMessage());
        String date = "";
        try {
            date = DateUtils.timedate(historyInfos.get(position).getTime());
        } catch (NumberFormatException e) {
            Log.e("date format","" +historyInfos.get(position).getTime());
        }

        holder.tv_time.setText(date);
        holder.tv_status.setText(historyInfos.get(position).getStatus());
        String f = "" ;
        if(historyInfos.get(position).getValue() > 0) {
            f = "+";
        }
        holder.tv_money.setText(f + historyInfos.get(position).getValue());
        Log.d("position","" + position);
        String year = DateUtils.timeyear(historyInfos.get(position).getTime());
        if(position != 0 ) {
            try {
                String preYear = DateUtils.timeyear(historyInfos.get(position - 1).getTime());
                if(year.equals(preYear)) {
                    holder.fl_header.setVisibility(View.GONE);
                    holder.v_line.setVisibility(View.VISIBLE);
                } else {
                    holder.fl_header.setVisibility(View.VISIBLE);
                    holder.v_line.setVisibility(View.GONE);
                    holder.tv_year.setText(year);
                }
            } catch (NumberFormatException e) {
                Log.e("date format","" +historyInfos.get(position).getTime());
            }

        } else {
            holder.fl_header.setVisibility(View.VISIBLE);
            holder.v_line.setVisibility(View.GONE);
            holder.tv_year.setText(year);
        }
    }

    @Override
    public int getItemCount() {
        return historyInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_headimg;
        TextView tv_name;
        TextView tv_desp;
        TextView tv_time;
        TextView tv_status;
        TextView tv_money;
        TextView tv_year;
        FrameLayout fl_header;
        View v_line;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_headimg = itemView.findViewById(R.id.iv_headimg);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_desp = itemView.findViewById(R.id.tv_desp);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_year = itemView.findViewById(R.id.tv_year);
            fl_header = itemView.findViewById(R.id.fl_header);
            v_line = itemView.findViewById(R.id.v_line);


        }
    }


    public void updateItem(List<HistoryInfo> datas) {
        this.historyInfos = datas;
        notifyDataSetChanged();
    }
}
