package com.example.MyControl.facade;

import android.content.Context;
import java.io.IOException;
import java.net.Socket;

public class DoOperationMaker {

    private DoOperations doOperations;

    public DoOperationMaker(Socket socket)throws IOException{
        doOperations=new OperatorNetwork(socket);
    }
    public DoOperationMaker(Context context)throws IOException{
        doOperations=new OperatorFile(context);
    }
    public void finishAllOperation()throws IOException{
        doOperations.closeStream();
    }
    public void startAllOperation()throws IOException{
        doOperations.setStream();
    }
    public String getTextElaborated(String key)throws IOException{
        return doOperations.readMedia(key);
    }
    public void writeAnything(String message)throws IOException{
        doOperations.writingStream(message);
    }
}