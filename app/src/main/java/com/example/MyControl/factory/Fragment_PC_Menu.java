package com.example.MyControl.factory;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.MyControl.R;
import com.example.MyControl.command.InvokerCommand;
import com.example.MyControl.command.ReceiverCommand;
import com.example.MyControl.command.WriteCommand;
import com.example.MyControl.facade.DoOperationMaker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Fragment_PC_Menu extends Fragment implements View.OnClickListener{
    private InvokerCommand invokerCommand;
    private TextView txtMouse;
    private DoOperationMaker doOperationMaker;
    private int screen_width;
    private int screen_height;
    private Context context;
    private DoOperationMaker doOperationMakerFile;

    public Fragment_PC_Menu(Context context,int screen_height, int screen_width,DoOperationMaker doOperationMakerFile){
        this.screen_height=screen_height;
        this.screen_width=screen_width;
        this.context=context;
        this.doOperationMakerFile=doOperationMakerFile;
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
        txtMouse.setOnClickListener(this);
        ReceiverCommand receiverCommand=new ReceiverCommand();
        WriteCommand writeCommand=new WriteCommand(receiverCommand);
        invokerCommand=new InvokerCommand(writeCommand);
        container.setBackgroundColor(Color.WHITE);
        new HelpThreadActivity().execute();
        return rootView;
    }

    private class HelpThreadActivity extends AsyncTask<Void,String,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try{
                String address = doOperationMakerFile.getTextElaborated("ADDRESS");
                Socket socket=new Socket(address,8004);
                invokerCommand.pressStartAll(socket);
                doOperationMaker=new DoOperationMaker(socket);
                doOperationMaker.startAllOperation();
                String mess=doOperationMaker.getTextElaborated(null);
                Log.d("messaggio da inviare",mess);
                String [] value=new String[2];
                value[0]=mess;
                publishProgress(value);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            txtMouse.setText("Connesso con "+ values[0]);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            invokerCommand.pressWritingActionPC(v);
        }
        catch (Exception e){
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
        if(doOperationMaker!=null){
            try{
                doOperationMaker.finishAllOperation();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}