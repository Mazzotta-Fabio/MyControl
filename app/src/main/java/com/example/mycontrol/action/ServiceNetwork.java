package com.example.mycontrol.action;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;

public class ServiceNetwork {
    private BufferedReader reader;
    private BufferedWriter bufferedWriter;
    private PrintWriter writer;
    private Socket socket;

    public ServiceNetwork() {
    }

    public void setSocket(Socket socket)throws IOException{
        this.socket=socket;
        bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(bufferedWriter,true);
    }

    private String getAddress() throws IOException {
        InetAddress host = InetAddress.getLocalHost();
        return host.getHostName();
    }


    public void writeSocket(String message)  throws IOException{
        Log.d("DEBUG","Sono in WriteSocket: STO INVIANDO IL SEGUENTE MESSAGGIO "+message);
        Log.d("DEBUG","Sono in WriteSocket: VALORE NEL WRITER "+writer.toString());
        writer.println(message);
    }

    public void writeStream(File file, ByteArrayOutputStream streamFile)throws IOException{
        Log.d("DEBUG","Sono in writeStream");
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(streamFile.toByteArray());
        String codificaFile=Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        Log.d("DEBUG", "codifica file: "+codificaFile);
        writer.println("invioFile "+file.getName().trim()+" "+codificaFile);
        fileOutputStream.close();
    }

    public String readSocket() throws IOException {
        return reader.readLine();
    }


    public void sendLocalAddress() throws IOException {
        writer.println(getAddress());
    }


    public void closeSocketStream() throws IOException {
        if(socket!=null){
            writeSocket("0");
            socket.close();
            bufferedWriter.close();
            reader.close();
            writer.close();
        }
    }

}
