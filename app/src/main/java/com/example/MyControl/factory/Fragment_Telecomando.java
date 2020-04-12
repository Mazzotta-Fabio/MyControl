package com.example.MyControl.factory;

import android.app.*;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.MyControl.R;
import com.example.MyControl.command.InvokerCommand;
import com.example.MyControl.command.ReceiverCommand;
import com.example.MyControl.command.WriteCommand;
import com.example.MyControl.facade.DoOperationMaker;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Fragment_Telecomando extends Fragment implements View.OnClickListener{
    private InvokerCommand invokerCommand;
    private TextView txtTelecomando;
    private DoOperationMaker doOperationMaker;
    private Context context;
    private DoOperationMaker doOperationMakerFile;

    public Fragment_Telecomando(Context context,DoOperationMaker doOperationMakerFile){
        this.context=context;
        this.doOperationMakerFile=doOperationMakerFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("DEBUGFILEFRAGMOUSE","sto in fragment telecomando");
        View rootView=inflater.inflate(R.layout.fragment_telecomando,container,false);
        container=(LinearLayout)rootView.findViewById(R.id.layout_telecomando);
        Display display=getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point point=new Point();
        display.getSize(point);
        int screen_heightY=point.y;
        int screen_widthX=point.x;
        container.setMinimumHeight(screen_heightY);
        container.setMinimumWidth(screen_widthX);
        txtTelecomando=(TextView) rootView.findViewById(R.id.txtTelecomando);
        ArrayList<View>buttons=(rootView.findViewById(R.id.table_content).getTouchables());
        container.setBackgroundColor(Color.YELLOW);
        for(View v:buttons){
            Button b=(Button)v;
            b.setOnClickListener(this);
        }
        ReceiverCommand receiverCommand=new ReceiverCommand();
        WriteCommand writeCommand=new WriteCommand(receiverCommand);
        invokerCommand=new InvokerCommand(writeCommand);
        new HelpThreadActivity().execute();
        txtTelecomando.setOnClickListener(this);
        return rootView;
    }

    private class HelpThreadActivity extends AsyncTask<Void,String,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d("DEBUG","STO NEL THREAD");
                String address = doOperationMakerFile.getTextElaborated("ADDRESS");
                Socket socket = new Socket(address, 8004);
                Log.d("DEBUG","Sto agganciato");
                invokerCommand.pressStartAll(socket);
                doOperationMaker = new DoOperationMaker(socket);
                doOperationMaker.startAllOperation();
                String mess = doOperationMaker.getTextElaborated(null);
                Log.d("messaggio da inviare", mess);
                String[] value = new String[2];
                value[0] = mess;
                publishProgress(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            txtTelecomando.setText("Connesso con "+values[0]);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            invokerCommand.pressWritingActionTelecomando(v);
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