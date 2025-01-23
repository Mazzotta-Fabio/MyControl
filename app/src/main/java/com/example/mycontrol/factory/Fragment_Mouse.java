package com.example.mycontrol.factory;

import androidx.fragment.app.*;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_Mouse extends Fragment implements View.OnTouchListener,View.OnClickListener{
    private TextView txtMouse;
    private ServiceNetwork serviceNetwork;
    private ServiceFile serviceFile;
    private Context context;
    private Handler handler;
    private ReceiverCommand receiverCommand;

    public Fragment_Mouse(Context context,ServiceFile serviceFile)throws IOException{
        this.context=context;
        this.serviceFile=serviceFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("DEBUGFILEFRAGMOUSE","sto in fragment mouse");
        View rootView=inflater.inflate(R.layout.fragment_mouse,container,false);
        txtMouse=(TextView)rootView.findViewById(R.id.txtMouse);
        ArrayList<View> buttons=(rootView.findViewById(R.id.pulsantiera).getTouchables());
        for(View v:buttons){
            v.setOnClickListener(this);
        }
        txtMouse.setOnClickListener(this);
        rootView.setOnTouchListener(this);
        container.setBackgroundColor(Color.RED);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
            receiverCommand.writeActionMouse(v,event,context);
        }
        catch (Exception e){
            Log.d("DEBUG","SONO NELL'ECCEZIONE di fragment mouse");
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                txtMouse.setText("Non Connesso");
                AlertDialog.Builder ac = new AlertDialog.Builder(context);
                ac.setTitle("Errore!");
                ac.setMessage("Impossibile connettersi al PC!");
                ac.setNeutralButton("Ok", null);
                ac.show();
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        try{
            receiverCommand.writeActionMouse(v,null,context);
        }
        catch (Exception e){
            AlertDialog.Builder ac = new AlertDialog.Builder(context);
            ac.setTitle("Errore!");
            ac.setMessage("Impossibile connettersi al PC!");
            ac.setNeutralButton("Ok", null);
            ac.show();
            e.printStackTrace();
        }
    }

    private class HelpThreadActivity implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("DEBUG","SONO NEL THREAD MOUSE");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                receiverCommand=new ReceiverCommand(serviceNetwork);
                Log.d("DEBUG", "Sono in FragmentMouse Mi sono connesso " + address);
                String value = serviceNetwork.readSocket();
                Log.d("DEBUG", "Messaggio ricevuto" + value);
                handler.post(new UpdateGui(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateGui implements Runnable {
        private String value;
        UpdateGui(String value) {
            this.value = value;
        }
        public void run() {
            Log.d("DEBUG","SONO NEL THREAD UPDATE GUI");
            if(value!=null) {
                txtMouse.setText("Connesso con " + value);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (serviceNetwork != null) {
                serviceNetwork.closeSocketStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    @Override
    public void onPause() {
        super.onPause();
        try {
            if (serviceNetwork != null) {
                serviceNetwork.closeSocketStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

}