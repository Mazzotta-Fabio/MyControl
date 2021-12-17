package com.example.mycontrol.action;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class ServiceFile {

    private Properties properties;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private PrintWriter printWriter;

    public ServiceFile(Context activity)throws IOException{
        properties=new Properties();
        outputStream=activity.openFileOutput("config.properties",activity.MODE_PRIVATE);
        inputStream=activity.openFileInput("config.properties");
        printWriter=new PrintWriter(outputStream,false);
    }


    public String readFile(String key) throws IOException{
        properties.load(inputStream);
        Log.d("DEBUG","Sono in READ FILE STO LEGGENDO IL FILE: "+properties.getProperty(key));
        return properties.getProperty(key);
    }


    public void writeFile(String key, String value) throws IOException {
        //properties.load(inputStream);
        properties.setProperty(key,value);
        properties.store(printWriter,null);
        properties.load(inputStream);
        Log.d("DEBUG","Sono in WRITE FILE STO LEGGENDO IL FILE: "+properties.getProperty(key));
    }


    public void closeStreamFile() throws IOException{
        outputStream.close();
        inputStream.close();
    }
}
