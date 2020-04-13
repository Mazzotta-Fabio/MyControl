package com.example.MyControl.factory;

import android.app.*;
import android.content.Context;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.example.MyControl.R;
import com.example.MyControl.command.InvokerCommand;
import com.example.MyControl.command.ReceiverCommand;
import com.example.MyControl.command.WriteCommand;
import com.example.MyControl.facade.DoOperationMaker;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Fragment_Mouse extends Fragment implements View.OnTouchListener,View.OnClickListener{
    private TextView txtMouse;
    private InvokerCommand invokerCommand;
    private DoOperationMaker doOperationMakerFile;
    private Context context;
    private DoOperationMaker doOperationMaker;

    public Fragment_Mouse(Context context,DoOperationMaker doOperationMakerFile){
        super();
        this.context=context;
        this.doOperationMakerFile=doOperationMakerFile;
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
        ReceiverCommand receiverCommand=new ReceiverCommand();
        WriteCommand writeCommand=new WriteCommand(receiverCommand);
        invokerCommand=new InvokerCommand(writeCommand);
        new HelpThreadActivity().execute();
        return rootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
            invokerCommand.pressMouse(v,event,context);
        }
        catch (Exception e){
            if(event.getAction()==MotionEvent.ACTION_DOWN){
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
            invokerCommand.pressMouse(v,null,context);
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

    private class HelpThreadActivity extends AsyncTask<Void,String,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String address = doOperationMakerFile.getTextElaborated("ADDRESS");
                Socket socket=new Socket(address,8004);
                invokerCommand.pressStartAll(socket);
                doOperationMaker=new DoOperationMaker(socket);
                doOperationMaker.startAllOperation();
                String mess = doOperationMaker.getTextElaborated(null);
                Log.d("messaggio da inviare", mess);
                String[] value = new String[2];
                value[0] = mess;
                publishProgress(value);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String...values){
            super.onProgressUpdate(values);
            txtMouse.setText("Connesso con "+values[0]);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (doOperationMaker != null) {
                doOperationMaker.finishAllOperation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (doOperationMaker != null) {
                doOperationMaker.finishAllOperation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}