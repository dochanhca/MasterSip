package jp.newbees.mastersip.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
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

    public final static String saveBitmapToFile(Bitmap bitmap, String filename){
        FileOutputStream out = null;
        try {
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

    public final static String saveBitmapToFile(Bitmap bitmap){
        return FileUtils.saveBitmapToFile(bitmap, "photo.png");
    }

    public final static boolean deleteFilePath(String filePath) {
        File file = new File(filePath);
        boolean deleted = file.delete();
        return deleted;
    }

    public static String saveImageBytesToFile(byte[] fileBytes, String filename) {
        FileOutputStream out = null;
        try {
            File sd = Environment.getExternalStorageDirectory();
            File dest = new File(sd, filename);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
            bos.write(fileBytes);
            bos.flush();
            bos.close();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String saveImageBytesToFile(byte[] fileBytes) {
        return FileUtils.saveImageBytesToFile(fileBytes, "default.png");
    }
}
