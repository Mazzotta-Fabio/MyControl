package com.example.MyControl.command;

import android.view.MotionEvent;
import android.view.View;
import java.io.IOException;
import java.net.Socket;

public class WriteCommand implements Command {
    private ReceiverCommand receiverCommand;
    public WriteCommand(ReceiverCommand receiverCommand){
        this.receiverCommand=receiverCommand;
    }

    @Override
    public void executeCommandPC(View v)  {
        receiverCommand.writeMediaPC(v);
    }

    @Override
    public void executeCommandMusic(View v) throws IOException {
        receiverCommand.writeMediaMusic(v);
    }

    @Override
    public void executeCommandTelecomando(View v) throws IOException {
        receiverCommand.writeMediaTelecomando(v);
    }

    @Override
    public void executeCommandTastiera(int primaryCode,boolean capsLock) throws IOException {
        receiverCommand.writeMediaKeyboard(primaryCode,capsLock);
    }

    @Override
    public void executeCommandMouse(View v,MotionEvent e) throws IOException {
        receiverCommand.writeActionMouse(v,e);
    }

    @Override
    public void executeStartComunication(Socket socket) throws IOException {
        receiverCommand.setCommunication(socket);
    }
}