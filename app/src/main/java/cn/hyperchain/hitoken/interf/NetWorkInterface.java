package cn.hyperchain.hitoken.interf;

import java.util.HashMap;

/**
 * Created by admin on 2017/11/24.
 */

public interface NetWorkInterface {

    void firstRequest(Object object);   //初始化请求

    void refreshRequest(Object object); //刷新

    void moreRequest(Object object);    //更多

    void netRequestAction(final int action, HashMap<String, String> body);  //网络操作

}
