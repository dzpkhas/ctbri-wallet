package cn.hyperchain.hitoken.imagecrop;

import android.os.Environment;

/**
 * Created by sven on 2016/1/6.
 *
 */
public class Constants {
    public static final String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

    public static final String PHOTONAME = EXTERNAL_STORAGE_DIRECTORY+ "/needCrop.jpg";
    public static final String QRCODENAME = EXTERNAL_STORAGE_DIRECTORY+ "/qrcode.jpg";
    public static final String DIRPATH = Constants.EXTERNAL_STORAGE_DIRECTORY + "/HitokenImage";
    public static final String LOADPATH = Constants.EXTERNAL_STORAGE_DIRECTORY + "/HitokenFile";

    public static final String NETPICTAILAPPEND = "?imageMogr2/auto-orient/thumbnail/600x600";

    public static final String NETPICTAILAPPHEAD = "http://139.219.4.181:8080/";

    public static final int TAKE_PHOTO = 1;
    public static final int PICK_PHOTO = 2;
    public static final int CROP_IMG = 3;
}
