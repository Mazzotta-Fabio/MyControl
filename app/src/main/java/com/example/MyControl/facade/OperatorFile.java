package com.example.MyControl.facade;

import android.content.Context;
import com.example.MyControl.adapter_pattern.ServiceMediaAdapter;
import java.io.IOException;

public class OperatorFile implements DoOperations {

    private ServiceMediaAdapter serviceMediaAdapter;
    public OperatorFile(Context context)throws IOException{
        serviceMediaAdapter=ServiceMediaAdapter.createFile(context);
    }
    @Override
    public String readMedia(String key) throws IOException {
        return serviceMediaAdapter.readMedia(key);
    }

    @Override
    public void closeStream() throws IOException {
        serviceMediaAdapter.closeMedia();
    }

    @Override
    public void setStream() throws IOException {
        serviceMediaAdapter.openMedia();
    }

    @Override
    public void writingStream(String message) throws IOException{
        serviceMediaAdapter.writeMedia(message);
    }
}
