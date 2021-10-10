package com.example.MyControl.adapter_pattern;

import android.content.Context;
import java.io.*;
import java.util.Properties;

public class MediaFile implements ServiceMediaFile {
    private Properties properties;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private PrintWriter printWriter;

    public MediaFile(Context activity)throws IOException{
        properties=new Properties();
        outputStream=activity.openFileOutput("config.properties",activity.MODE_PRIVATE);
        inputStream=activity.openFileInput("config.properties");
        printWriter=new PrintWriter(outputStream,false);
    }

    @Override
    public String readFile(String key) throws IOException{
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    @Override
    public void writeFile(String key, String value) throws IOException {
        properties.setProperty(key,value);
        properties.store(printWriter,null);
    }

    @Override
    public void closeStreamFile() throws IOException{
        outputStream.close();
        inputStream.close();
    }
}