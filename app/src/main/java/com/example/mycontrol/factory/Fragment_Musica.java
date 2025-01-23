package com.example.mycontrol.factory;

import android.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_Musica extends Fragment implements View.OnClickListener{
    private ServiceFile serviceFile;
    private ServiceNetwork serviceNetwork;
    private Context context;
    private ReceiverCommand receiverCommand;

    public Fragment_Musica(Context context,ServiceFile serviceFile)throws IOException{
        this.context=context;
        this.serviceFile=serviceFile;
        //serviceNetwork=new ServiceNetwork();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.fragment_musica,container,false);
        container=(LinearLayout)rootView.findViewById(R.id.main_layout_musica);
        ArrayList<View> buttons=(rootView.findViewById(R.id.main_layout_musica).getTouchables());
        for(View v:buttons){
            Button b=(Button)v;
            b.setOnClickListener(this);
        }
        WindowMetrics displayMetrics=getActivity().getWindowManager().getCurrentWindowMetrics();
        int screen_Width=displayMetrics.getBounds().width();
        int screen_Height=displayMetrics.getBounds().height();
        container.setMinimumWidth(screen_Width);
        container.setMinimumHeight(screen_Height);
        container.setBackgroundColor(Color.DKGRAY);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
          /*
        utile per gestire i processi in maniera asincrona. qui ne creiamo uno
        */
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new HelpThreadActivity());
        return rootView;
    }

    private class HelpThreadActivity implements Runnable{
        @Override
        public void run(){
            try {
                Log.d("DEBUG","SONO NEL THREAD MUSICA");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                receiverCommand = new ReceiverCommand(serviceNetwork);
                Log.d("DEBUG","SONO in fragentMusica mi sono connesso");
                receiverCommand = new ReceiverCommand(serviceNetwork);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            receiverCommand.writeMediaMusic(v);
        }
        catch (Exception e) {
            AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
            ac.setTitle("Errore!");
            ac.setMessage("Impossibile connettersi al PC!");
            ac.setNeutralButton("Ok", null);
            ac.show();
            e.printStackTrace();
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
