package com.example.MyControl.command;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Point;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import com.example.MyControl.R;
import com.example.MyControl.adapter_pattern.ServiceMediaAdapter;
import java.io.IOException;
import java.net.Socket;

/**
 * classe Receiver:porta avanti le operazioni richieste per poter essere eseguite
 */
public class ReceiverCommand {
    private ServiceMediaAdapter serviceMediaAdapter;

    public ReceiverCommand(){}

    public void writeMediaPC(View v) {
        Log.d("SERVICEMEDIAADAPTER",serviceMediaAdapter.toString());
        final String message;
        Button b=(Button) v;
        AlertDialog.Builder alert=new AlertDialog.Builder(v.getContext());
        int id=b.getId();
        switch (id) {
            case R.id.btnSpegni:
                alert.setTitle("Sei sicuro di voler di spegnere il PC?");
                message = "gestionePC SPEGNI";
                break;
            case R.id.btnIberna:
                alert.setTitle("Sei sicuro di ibernare il PC?");
                message = "gestionePC IBERNA";
                break;
            case R.id.btnRiavvia:
                alert.setTitle("Sei sicuro di riavviare il PC?");
                message = "gestionePC RIAVVIA";
                break;
            case R.id.btnDisconnetti:
                alert.setTitle("Sei sicuro di disconnettere il PC?");
                message = "gestionePC DISCONETTI";
                break;
            default:message=" ";
        }
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    serviceMediaAdapter.writeMedia(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setNeutralButton("Annulla",null);
        alert.show();
    }
    //telecomando
    public void writeMediaTelecomando(View v)throws IOException{
        String message=null;
        Button b=(Button)v;
        int id=b.getId();
        switch (id) {
            case R.id.avanti:
                message = "tastiera A Telecomando";
                break;
            case R.id.indietro:
                message = "tastiera I Telecomando";
                break;
            case R.id.destra:
                message = "tastiera D Telecomando";
                break;
            case R.id.sinistra:
                message = "tastiera S Telecomando";
                break;
        }
        serviceMediaAdapter.writeMedia(message);
    }
    //musica
    public void writeMediaMusic(View v)throws IOException{
        String message=null;
        Button b=(Button) v;
        int id=b.getId();
        switch (id) {
            case R.id.muto:
                message = "gestioneMDP METTIMUTO";
                break;
            case R.id.chiudi:
                message = "gestioneMDP CHIUDIMDP";
                break;
            case R.id.stop:
                message = "gestioneMDP STOP";
                break;
            case R.id.Volumebasso:
                message = "gestioneMDP VOLUMEBASSO";
                break;
            case R.id.Vaiindietro:
                message="gestioneMDP VAIINDIETRO";
                break;
            case R.id.pausaEsegui:
                message="gestioneMDP PAUSAESEGUI";
                break;
            case R.id.schermointero:
                message="gestioneMDP SCHERMOINTERO";
                break;
            case R.id.Volumealto:
                message="gestioneMDP VOLUMEALTO";
                break;
            case R.id.Vaiavanti:
                message="gestioneMDP VAIAVANTI";
                break;
        }
        serviceMediaAdapter.writeMedia(message);
    }
    //tastiera
    public void writeMediaKeyboard(int code,boolean capsLock)throws IOException{
        char letter;
        switch(code){
            case 35:
                letter='\'';
                break;
            case 64:
                letter='$';
                break;
            case 853:
                letter='<';
                break;
            case 854:
                letter='>';
                break;
            case 855:
                letter='#';
                break;
            case 32:
                letter='^';
                break;
            default:
                letter=(char)code;
                if(Character.isLetter(letter)&&capsLock){
                    letter=Character.toUpperCase(letter);
                }
                break;
        }
        String message="tastiera " + letter + " SelezioneTasto";
        serviceMediaAdapter.writeMedia(message);
    }

    //mouse
    public void writeActionMouse(View v, MotionEvent e)throws IOException{
        String message="\\";
        Log.d("DEBUGVIEW",v.getClass().getName());
        if(e!=null) {
            if ((e.getAction() == MotionEvent.ACTION_DOWN)) {
                Log.d("Debug", "sono in touch click");
                message = "mouse Click SINISTRO";
            } else {
                if ((e.getAction() == MotionEvent.ACTION_MOVE)) {
                    Log.d("Debug", "sono in move touch click");
                    double x = e.getX()/*/width*/;
                    double y = e.getY()/*/height*/;
                    message = "mouseMuovi " + x + " " + y + " Muovi";
                }
            }
        }
        if(v.getClass().getName().equals("androidx.appcompat.widget.AppCompatButton")){
            Button b=(Button)v;
            switch (b.getId()) {
                case R.id.tasto_destro:
                    message = "mouse Click DESTRO";
                    break;
                case R.id.tasto_sinistro:
                    message = "mouse Click SINISTRO";
                    break;
            }
        }
        if(!(message.equals("\\"))){
            serviceMediaAdapter.writeMedia(message);
        }
    }

    public void setCommunication(Socket socket)throws IOException{
        serviceMediaAdapter=ServiceMediaAdapter.createNetwork(socket);
    }
}