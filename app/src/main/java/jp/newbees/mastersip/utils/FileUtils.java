package jp.newbees.mastersip.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vietbq on 1/23/17.
 */

public class FileUtils {
    private FileUtils(){
        //Prevent init constructor
    }

    public final static String saveBitmapToFile(Bitmap bitmap){
        FileOutputStream out = null;
        try {
            String filename = "photo.png";
            File sd = Environment.getExternalStorageDirectory();
            File dest = new File(sd, filename);
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public final static boolean deleteFilePath(String filePath) {
        File file = new File(filePath);
        boolean deleted = file.delete();
        return deleted;
    }
}
