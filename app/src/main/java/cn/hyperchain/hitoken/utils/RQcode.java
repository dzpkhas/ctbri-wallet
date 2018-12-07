package cn.hyperchain.hitoken.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

import static android.graphics.Color.WHITE;

public class RQcode {
    private static final String CHARSET = "utf-8";
    private static final int BLACK = 0xff000000;
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    public static Bitmap getRQcode(String content){
        Hashtable<EncodeHintType, Object> hashTable = new Hashtable<EncodeHintType, Object>();
        hashTable.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hashTable.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hashTable.put(EncodeHintType.MARGIN, 1);
        Bitmap bitmap =null;
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hashTable);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = BLACK;
                    } else {
                        pixels[y * width + x] = WHITE;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
