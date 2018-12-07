package cn.hyperchain.hitoken.imagecrop;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileUtis {

    public static File saveBitmap2File(Bitmap bitmap, String dirPath, String fileName) {

        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File file = new File(dirPath,fileName);

        FileOutputStream fos = null;
        try {

            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static Bitmap scaleBitmap(Bitmap bm, int width, int height) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmWidth, (float) height / bmHeight);
        return Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, true);
    }

    /**
     * 旋转图片
     * @param angle 旋转角度
     * @param bitmap 要处理的Bitmap
     * @return 处理后的Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle,(float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return resizedBitmap;
    }


    /**
     * 删除裁剪临时生成图片,上传到七牛后应该调用此方法删除.
     */
    public static boolean deleteAllTempImg(){
        File dir = new File(Constants.DIRPATH);
        if (dir.exists()) {
            String names[] = dir.list();
            for (String str : names) {
                File deleteFile = new File(dir,str);
                deleteFile.delete();
            }
            return true;
        }
        return true;
    }

}

