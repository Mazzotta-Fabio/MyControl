package com.example.MyControl.factory;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.MyControl.R;
import com.example.MyControl.command.InvokerCommand;
import com.example.MyControl.command.ReceiverCommand;
import com.example.MyControl.command.WriteCommand;
import com.example.MyControl.facade.DoOperationMaker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Fragment_Musica extends Fragment implements View.OnClickListener{
    private InvokerCommand invokerCommand;
    private DoOperationMaker doOperationMaker;
    private DoOperationMaker doOperationMakerFile;
    private Context context;

    public Fragment_Musica(Context context,DoOperationMaker doOperationMakerFile){
        this.context=context;
        this.doOperationMakerFile=doOperationMakerFile;
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
        Display display =getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point point = new Point();
        display.getSize(point);
        int screen_Height = point.y;
        int screen_Width = point.x;
        container.setMinimumWidth(screen_Width);
        container.setMinimumHeight(screen_Height);
        ReceiverCommand receiverCommand=new ReceiverCommand();
        WriteCommand writeCommand=new WriteCommand(receiverCommand);
        invokerCommand=new InvokerCommand(writeCommand);
        container.setBackgroundColor(Color.DKGRAY);
        new HelpThreadActivity().execute();
        return rootView;
    }

    private class HelpThreadActivity extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String address = doOperationMakerFile.getTextElaborated("ADDRESS");
                Socket socket = new Socket(address, 8004);
                invokerCommand.pressStartAll(socket);
                doOperationMaker = new DoOperationMaker(socket);
                doOperationMaker.startAllOperation();
                Log.d("DEBUG","mi sono connesso");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        try {
            invokerCommand.pressWritingActionMusic(v);
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
        if (doOperationMaker != null) {
            try {
                doOperationMaker.finishAllOperation();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
