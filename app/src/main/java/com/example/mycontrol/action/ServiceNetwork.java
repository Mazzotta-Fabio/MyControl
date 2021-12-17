package com.example.mycontrol.action;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class ServiceNetwork {

    private BufferedReader reader;
    private BufferedWriter bufferedWriter;
    private PrintWriter writer;
    private Socket socket;
    private DataOutputStream dataOutputStream;

    public ServiceNetwork() {
    }


    public boolean checkSocket(){
        return socket.isConnected();
    }

    public void setSocket(Socket socket)throws IOException{
        this.socket=socket;
        bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(bufferedWriter,true);
        //per inviare file
        dataOutputStream=new DataOutputStream(socket.getOutputStream());
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

    public void writeStream(File file, String nomeFile)throws IOException{
        //writer.println("invioFile "+nomeFile);
        Log.d("DEBUG","Sono in writeStream: dimensione: "+ file.length());
        writer.println("invioFile "+nomeFile+" "+100);
        FileInputStream fis=new FileInputStream(file);
        int bytes=0;
        byte [] buffer=new byte[4*1024];
        while ((bytes=fis.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        dataOutputStream.close();
       /*
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        int bytes=0;
        byte [] buffer=new byte[4*1024];
        dataOutputStream.writeLong(file.length());

        while ((bytes=fis.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
            Log.d("DEBUG","SONO QUI");
        }
        dataOutputStream.close();
        fis.close();
        /*
        BufferedReader br=new BufferedReader(new InputStreamReader(fis));
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        PrintWriter pw=new PrintWriter(bw,true);
        writer.println("invioFile "+nomeFile+" "+100);
        String line=null;
        while((line=br.readLine())!=null){
            pw.println(line);
        }
        br.close();
        pw.close();
        bw.close();
        Log.d("DEBUG","Sono in writeStream: sto scrivendo lo stream");
        /*
        byte[] buffer = new byte[4*1024];
        int bytes=0,i=0;
        while ((bytes=fis.read(buffer))!=-1){
            socket.getOutputStream().write(buffer,0,bytes);
            i=i+bytes;
        }
        fis.close();
        //socket.close();
        Log.d("DEBUG","Sono in write stream ho copiato "+i);
        /*
        int count,i=0;
        while ((count = fis.read(buffer)) > 0) {
            i = i + count;
        }

        byte [] newBuffer=new byte[4*1024];
        int bytes=0;
        while ((bytes=fis.read(newBuffer))!=-1){
            dataOutputStream.write(newBuffer,0,bytes);
            dataOutputStream.flush();
        }
        fis.close();
        /*
        BufferedInputStream bis=new BufferedInputStream(fis);
        bis.read(sizeFile,0,sizeFile.length);
        OutputStream os=socket.getOutputStream();
        os.write(sizeFile,0,sizeFile.length);
        os.flush();
        //writer.println(fis);
        /*
        //ottieni uno stream di byte da un URI
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        int count,i=0;
        byte[] buffer = new byte[8192]; // or 4096, or more
        while ((count = fis.read(buffer)) > 0){
            i=i+count;
            out.write(buffer, 0, count);
        }
        fis.close();
        out.close();
        /*
        BufferedReader br=new BufferedReader(new InputStreamReader(fis));
        OutputStream os=socket.getOutputStream();
        Log.d("DEBUG","sono in WriteStream");
        String line=null;
        while((line=br.readLine())!=null){
            os.write(line.getBytes());
        }
        Log.d("DEBUG","HO FINITO");
        /*
        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
        out.reset();
        out.writeObject(fis);
        out.flush();
        out.close();
        */
        /*
        byte[] buf = new byte[16*1024];
        InputStream in=fis;
        OutputStream out=socket.getOutputStream();
        int read,i=0;
        Log.d("DEBUG","SONO IN WRITESTREAM PRIMA DEL CICLO");
        while ((read = in.read(buf)) > 0) {
            //Log.d("DEBUG","SONO NEL CICLO"+read);
            out.write(buf, 0, read);
            i=i+read;
        }
        out.close();
        fis.close();
        in.close();
        Log.d("DEBUG","SONO IN WRITESTREAM DIMENSIONE: "+i);
         */
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
