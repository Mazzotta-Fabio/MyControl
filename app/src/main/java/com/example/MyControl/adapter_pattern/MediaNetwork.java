package com.example.MyControl.adapter_pattern;

import android.util.Log;
import java.io.*;
import java.net.*;

public class MediaNetwork implements ServiceMediaNetwork {

    private BufferedReader reader;
    private BufferedWriter bufferedWriter;
    private PrintWriter writer;
    private Socket socket;

    public MediaNetwork(Socket socket ) throws IOException{
        this.socket=socket;
        bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(bufferedWriter,true);
    }

    private String getAddress() throws IOException{
        InetAddress host = InetAddress.getLocalHost();
        return host.getHostName();
    }

    @Override
    public void writeSocket(String message)  {
        Log.d("MESSAGGIO:MEDNET ",message);
        Log.d("WRITER:MEDNET",writer.toString());
        writer.println(message);
    }

    @Override
    public String readSocket() throws IOException {
        return reader.readLine();
    }

    @Override
    public void sendLocalAddress() throws IOException {
        writer.println(getAddress());
    }

    @Override
    public void closeSocketStream() throws IOException {
        writeSocket("0");
        socket.close();
        bufferedWriter.close();
        reader.close();
        writer.close();
    }
}