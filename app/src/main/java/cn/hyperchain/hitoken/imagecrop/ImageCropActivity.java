package cn.hyperchain.hitoken.imagecrop;


import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.TemplateActivity;

/**
 * Created by Administrator on 2016/1/6.
 */
public class ImageCropActivity extends TemplateActivity {

    @Override
    protected void initView() {
        super.initView();
        hideTitleBar();
        getSupportFragmentManager().beginTransaction().add(R.id.id_container, new ImageCropFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    @Override
    protected TemplateActivity.SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_image_crop;
    }
}
