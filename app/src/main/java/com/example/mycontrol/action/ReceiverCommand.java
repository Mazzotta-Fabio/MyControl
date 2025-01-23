package com.example.mycontrol.action;

import com.example.mycontrol.R;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Point;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import java.io.IOException;


/**
 * classe Receiver:porta avanti le operazioni richieste per poter essere eseguite
 */
public class ReceiverCommand {
    /* coordinate per mouse*/
    private double initialTouchX;
    private double initialTouchY;
    private double initialX;
    private double initialY;

    private ServiceNetwork serviceNetwork;

    public ReceiverCommand(ServiceNetwork serviceNetwork)throws IOException{
        this.serviceNetwork=serviceNetwork;
    }

    public void writeMediaPC(View v) {
        Log.d("DEBUG","STO IN WRITEMEDIA PC");
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
                    serviceNetwork.writeSocket(message);
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
        serviceNetwork.writeSocket(message);
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
        serviceNetwork.writeSocket(message);
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
        serviceNetwork.writeSocket(message);
    }

    //mouse
    public void writeActionMouse(View v, MotionEvent e,Context context)throws IOException{
        String message="\\";
        Log.d("DEBUG","SONO nel movimento mouse"+v.getClass().getName());
        if(e!=null) {
            if ((e.getAction() == MotionEvent.AXIS_TOUCH_MAJOR)) {
                Log.d("Debug", "sono in touch click");
                message = "mouse Click SINISTRO";
            } else {
                if ((e.getAction() == MotionEvent.ACTION_MOVE)) {
                    Log.d("Debug", "sono in move touch click");

                    switch (e.getAction()) {
                        /*
                        case (MotionEvent.ACTION_DOWN):
                            initialX=e.getX();
                            initialY=e.getY();
                            initialTouchX = e.getRawX();
                            initialTouchY = e.getRawY();
                            break;
                         */
                        case (MotionEvent.ACTION_MOVE):
                            WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
                            WindowMetrics windowMetrics=wm.getCurrentWindowMetrics();
                            //Point point = new Point();
                            //display.getSize(point);
                            double x = initialX + (int) (e.getRawX() - initialTouchX);
                            double y = initialY + (int) (e.getRawY() - initialTouchY);
                            message = "mouseMuovi " + (initialX/windowMetrics.getBounds().height())*100 + " " + x + " " + (initialY/windowMetrics.getBounds().width())*100 + " " + y + " Muovi";
                            break;
                    }
                    /*
                    WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
                    Display display=wm.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    double x = e.getX()*point.x;
                    double y = e.getY()*point.y;
                    int pointerCount = e.getPointerCount();
                    /*
                    double x = e.getX();
                    double y = e.getY();
                    message = "mouseMuovi " + x + " " + y + " Muovi";
                     */
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
            serviceNetwork.writeSocket(message);
        }
    }

/*
    public void setCommunication(Socket socket)throws IOException{
        serviceMediaAdapter=ServiceMediaAdapter.createNetwork(socket);
    }
*/
}