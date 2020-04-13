package com.example.MyControl.command;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import java.io.IOException;
import java.net.Socket;

public interface Command {
    void executeCommandPC(View v)throws IOException;
    void executeCommandMusic(View v)throws IOException;
    void executeCommandTelecomando(View v)throws IOException;
    void executeCommandTastiera(int primaryCode,boolean capsLock)throws IOException;
    void executeCommandMouse(View v, MotionEvent event, Context context)throws IOException;
    void executeStartComunication(Socket socket)throws IOException;
}
