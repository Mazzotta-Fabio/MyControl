package com.example.MyControl.adapter_pattern;

import android.content.Context;
import java.io.IOException;
import java.net.Socket;

public abstract class ServiceMediaAdapter {

    public static class ServiceMediaAdapterNetwork extends ServiceMediaAdapter{

        private ServiceMediaNetwork serviceMediaNetwork;

        public ServiceMediaAdapterNetwork(Socket socket)throws IOException{
            serviceMediaNetwork=new MediaNetwork(socket);
        }

        public void openMedia() throws IOException {
            serviceMediaNetwork.sendLocalAddress();
        }

        public void writeMedia(String message) throws IOException {
            serviceMediaNetwork.writeSocket(message);
        }

        public String readMedia(String key) throws IOException {
            return serviceMediaNetwork.readSocket();
        }

        public void closeMedia() throws IOException {
            serviceMediaNetwork.closeSocketStream();
        }
    }

    public static class ServiceMediaAdapterFile extends ServiceMediaAdapter{
        private ServiceMediaFile serviceMediaFile;

        public ServiceMediaAdapterFile(Context context)throws IOException{
            serviceMediaFile=new MediaFile(context);
        }

        public void closeMedia()throws IOException{
            serviceMediaFile.closeStreamFile();
        }

        public String readMedia(String key) throws IOException {
            return serviceMediaFile.readFile(key);
        }

        public void writeMedia(String message)throws IOException{
            String parole[]=message.split(" ");
            if(parole.length==2){
                serviceMediaFile.writeFile(parole[0],parole[1]);
            }
        }

        public void openMedia() throws IOException {
            /*do nothing*/
        }
    }

    public static ServiceMediaAdapterNetwork createNetwork(Socket socket)throws IOException{
        return new ServiceMediaAdapterNetwork(socket);
    }
    public static ServiceMediaAdapterFile createFile(Context context)throws IOException{
        return new ServiceMediaAdapterFile(context);
    }

    public abstract void openMedia() throws IOException;
    public abstract void closeMedia()throws IOException;
    public abstract void writeMedia(String message)throws IOException;
    public abstract String readMedia(String key)throws IOException;

}