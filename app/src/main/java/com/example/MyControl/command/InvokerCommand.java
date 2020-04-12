package com.example.MyControl.command;

import android.util.Log;
import android.view.*;
import java.io.IOException;
import java.net.Socket;

public class InvokerCommand {
    private Command writingAction;

    public InvokerCommand(Command writingAction){
        this.writingAction=writingAction;
    }

    public void pressWritingActionMusic(View v)throws IOException{
        Log.d("WRITINGACTION",writingAction.toString());
        writingAction.executeCommandMusic(v);
    }

    public void pressWritingActionPC(View v)throws IOException{
        Log.d("WRITINGACTION55",writingAction.toString());
        writingAction.executeCommandPC(v);
    }

    public void pressWritingActionTelecomando(View v)throws IOException{
        Log.d("WRITINGACTION56",writingAction.toString());
        writingAction.executeCommandTelecomando(v);
    }
    public void pressKey(int code,boolean capsLock)throws IOException{
        writingAction.executeCommandTastiera(code,capsLock);
    }

    public void pressMouse(View v, MotionEvent event)throws IOException{
        writingAction.executeCommandMouse(v,event);
    }

    public void pressStartAll(Socket socket)throws IOException{
        writingAction.executeStartComunication(socket);
    }
}