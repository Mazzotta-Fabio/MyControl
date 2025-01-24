package com.example.mycontrol.factory;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_PC_Menu extends Fragment implements View.OnClickListener{

    private ServiceNetwork serviceNetwork;
    private ServiceFile serviceFile;
    private ReceiverCommand receiverCommand;
    private TextView txtMouse;
    private int screen_width;
    private int screen_height;
    private Context context;
    private Handler handler;

    public Fragment_PC_Menu(Context context,int screen_height, int screen_width,ServiceFile serviceFile)throws IOException{
        this.screen_height=screen_height;
        this.screen_width=screen_width;
        this.context=context;
        this.serviceFile=serviceFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.fragment_pc,container,false);
        container=(LinearLayout)rootView.findViewById(R.id.container_pc);
        container.setMinimumHeight(screen_height);
        container.setMinimumWidth(screen_width);
        txtMouse=(TextView)rootView.findViewById(R.id.txtPC);
        ArrayList<View> buttons=(rootView.findViewById(R.id.table).getTouchables());
        for(View v:buttons){
            Button b=(Button)v;
            b.setOnClickListener(this);
        }
        container.setBackgroundColor(Color.WHITE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
         /*
        utile per gestire i processi in maniera asincrona. qui ne creiamo uno
        */
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //permette di inviare messaggi ad thread associato
        handler = new Handler(Looper.getMainLooper());
        executor.execute(new HelpThreadActivity());
        return rootView;
    }

    private class HelpThreadActivity implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("DEBUG","SONO NEL THREAD pc_menu");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                Log.d("DEBUG", "sono in fragment pc_menu Sto agganciato");
                receiverCommand = new ReceiverCommand(serviceNetwork);
                String value=serviceNetwork.readSocket();
                handler.post(new UpdateGui(value));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class UpdateGui implements Runnable{
        private String value;
        private UpdateGui(String value){
            this.value=value;
        }
        @Override
        public void run() {
            Log.d("DEBUG","SONO NEL THREAD UPDATE GUI");
            if(value!=null) {
                txtMouse.setText("Connesso con " + value);
            }
        }
    }


    public void onClick(View v) {
        try {
            receiverCommand.writeMediaPC(v);
        }
        catch (Exception e){
            txtMouse.setText("Non Connesso");
            AlertDialog.Builder ac=new AlertDialog.Builder(v.getContext());
            ac.setTitle("Errore!");
            ac.setMessage("Impossibile connettersi al PC!");
            ac.setNeutralButton("Ok",null);
            ac.show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceNetwork!=null){
            try{
                serviceNetwork.closeSocketStream();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}