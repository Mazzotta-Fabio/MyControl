package com.example.MyControl.facade;

import com.example.MyControl.adapter_pattern.ServiceMediaAdapter;
import java.io.IOException;
import java.net.Socket;

public class OperatorNetwork implements DoOperations {
    private ServiceMediaAdapter serviceMediaAdapter;
    public OperatorNetwork(Socket socket)throws IOException{
        serviceMediaAdapter=ServiceMediaAdapter.createNetwork(socket);
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
    public void writingStream(String message) throws IOException {
        /*do nothing*/
    }
}
