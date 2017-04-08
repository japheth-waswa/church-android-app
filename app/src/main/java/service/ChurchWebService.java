package service;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChurchWebService {

    //app-run-false
    public static void getEntireBible(Context context) {

        //todo perform bible_books,bible_chapters,bible_verses file creaations and write data to them.(remove after getting data i files)

        try {
            File fileName = new File(context.getFilesDir().getPath() + "/bible_books.txt");
            FileOutputStream fOut = new FileOutputStream(fileName, true);
            //FileOutputStream fOut = openFileOutput("bible_books", Context.MODE_PRIVATE);
            String str = "Hello world 18";
            fOut.write(str.getBytes());
            fOut.close();

            FileInputStream fin = new FileInputStream(fileName);
            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            fin.close();
            Log.e("jeff-waswa", temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
