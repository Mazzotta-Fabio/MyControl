package com.example.mycontrol.factory;

import androidx.fragment.app.Fragment;
import android.app.*;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_Telecomando extends Fragment implements View.OnClickListener{

    private TextView txtTelecomando;
    private Context context;
    private ServiceFile serviceFile;
    private ServiceNetwork serviceNetwork;
    private ReceiverCommand receiverCommand;
    private Handler handler;


    public Fragment_Telecomando(Context context,ServiceFile serviceFile)throws IOException{
        this.context=context;
        this.serviceFile=serviceFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("DEBUG","sto in fragment telecomando");
        View rootView=inflater.inflate(R.layout.fragment_telecomando,container,false);
        container=(LinearLayout)rootView.findViewById(R.id.layout_telecomando);
        WindowMetrics displayMetrics=getActivity().getWindowManager().getCurrentWindowMetrics();
        int screen_heightY=displayMetrics.getBounds().width();
        int screen_widthX=displayMetrics.getBounds().height();
        container.setMinimumHeight(screen_heightY);
        container.setMinimumWidth(screen_widthX);
        txtTelecomando=(TextView) rootView.findViewById(R.id.txtTelecomando);
        ArrayList<View>buttons=(rootView.findViewById(R.id.table_content).getTouchables());
        container.setBackgroundColor(Color.YELLOW);
        for(View v:buttons){
            Button b=(Button)v;
            b.setOnClickListener(this);
        }
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
        public void run() {
            try {
                Log.d("DEBUG","SONO NEL THREAD TELECOMANDO");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Log.d("DEBUG","Valore di indirizzo "+address);
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                Log.d("DEBUG", "sono in fragment Telecomando Sto agganciato");
                receiverCommand = new ReceiverCommand(serviceNetwork);
                String value=serviceNetwork.readSocket();
                Log.d("DEBUG", "VALORE LETTO DAL SOCKET: "+value);
                handler.post(new UpdateGui(value));
            } catch (IOException e) {
                Log.d("DEBUG","SONO NELL'ECCEZIONE DEL TELECOMANDO");
                Log.d("DEBUG", e.getMessage());
            }

        }
    }

    private class UpdateGui implements Runnable {
        private String value;
        private UpdateGui(String value){
            this.value=value;
        }
        @Override
        public void run() {
            Log.d("DEBUG","SONO NEL THREAD UPDATE GUI");
            if(value!=null){
                txtTelecomando.setText("Connesso con "+value);
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            receiverCommand.writeMediaTelecomando(v);
        }
        catch (Exception e) {
            AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
            ac.setTitle("Errore!");
            ac.setMessage("Impossibile connettersi al PC!");
            ac.setNeutralButton("Ok", null);
            ac.show();
            txtTelecomando.setText("Non Connesso");
            Log.d("ERRORE",e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceNetwork != null) {
            try {
                serviceNetwork.closeSocketStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}