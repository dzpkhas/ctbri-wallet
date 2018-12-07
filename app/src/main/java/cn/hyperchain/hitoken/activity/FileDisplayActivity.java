package cn.hyperchain.hitoken.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.OnClick;


public class FileDisplayActivity extends TemplateActivity {


    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.tv_title)
    TextView tvTitle;


    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        Intent intent = getIntent();
        int from = intent.getIntExtra("from",0);
        if(from == 0) {
            tvTitle.setText("用户协议");
            pdfView.fromAsset("agreement.pdf").defaultPage(1)
                    .swipeVertical(true)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
//                        // 当用户在翻页时候将回调。
//                        Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
                        }
                    }).load();
        } else {
            tvTitle.setText("帮助");
            pdfView.fromAsset("problem.pdf").defaultPage(1)
                    .swipeVertical(true)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
//                        // 当用户在翻页时候将回调。
//                        Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
                        }
                    }).load();
        }




    }


    @OnClick({
            R.id.ll_back
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_file_display;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}