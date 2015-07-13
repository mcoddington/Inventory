package com.caci.inventory;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mikec_000 on 6/25/2015.
 */
public class TestDataUtility {
    //TODO turn into DBs
    public static final String[] FILELIST = {"cars", "planets"};
    public static final String[] FILEDATA = {"Maserati,911", "Earth,Neptune"};

    public static void searchForData(Activity parent, String data, TextView resultTxt) {
        boolean itemFound = false;
        resultTxt.append("Searching datasets for "+data+"\n");
        for (String filename:FILELIST) {
            resultTxt.append("Searching dataset "+filename+"...\n");
            itemFound = findItemInFile(parent, data, filename);
            if (itemFound) {
                resultTxt.append("Item Found!\n");
                break;
            } else {
                resultTxt.append("Item not in dataset "+filename+"\n");
            }
        }
    }

    public static void createTestData(Activity parent) {
        for (int i=0; i<FILELIST.length; i++) {
            try {
                File f = new File(parent.getFilesDir()+File.separator+FILELIST[i]);
                if (!f.exists()) {
                    FileOutputStream fosC = parent.openFileOutput(FILELIST[i], Context.MODE_PRIVATE);
                    fosC.write(FILEDATA[i].getBytes());
                    fosC.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean findItemInFile(Activity parent, String itemName, String filename) {
        boolean itemFound = false;
        try {
            FileInputStream fis = parent.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            fis.close();
            String fileContents = sb.toString();
            itemFound = fileContents.contains(itemName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemFound;
    }
}
