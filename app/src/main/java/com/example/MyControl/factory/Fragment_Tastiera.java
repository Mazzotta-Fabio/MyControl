package com.example.MyControl.factory;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.*;
import android.media.AudioManager;
import android.os.*;
import android.view.*;
import com.example.MyControl.R;
import com.example.MyControl.command.InvokerCommand;
import com.example.MyControl.command.ReceiverCommand;
import com.example.MyControl.command.WriteCommand;
import com.example.MyControl.facade.DoOperationMaker;
import java.io.IOException;
import java.net.Socket;
@SuppressLint("ValidFragment")
public class Fragment_Tastiera extends Fragment implements KeyboardView.OnKeyboardActionListener{
    private InvokerCommand invokerCommand;
    private Keyboard keyboard;
    private KeyboardView keyboardView;
    private DoOperationMaker doOperationMaker;
    private boolean capsLock;
    private Context context;
    private DoOperationMaker doOperationMakerFile;

    public Fragment_Tastiera(Context context, DoOperationMaker doOperationMakerFile){
        this.context=context;
        this.doOperationMakerFile=doOperationMakerFile;
        capsLock=false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        keyboardView=(KeyboardView)inflater.inflate(R.layout.fragment_tastiera,container,false);
        keyboard=new Keyboard(context,R.xml.qwerty);
        keyboardView.setKeyboard(keyboard);
        container.setBackgroundColor(Color.CYAN);
        ReceiverCommand receiverCommand=new ReceiverCommand();
        WriteCommand writeCommand=new WriteCommand(receiverCommand);
        invokerCommand=new InvokerCommand(writeCommand);
        keyboardView.setOnKeyboardActionListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new HelpThreadActivity().start();
        return keyboardView;
    }

    private class HelpThreadActivity extends Thread {
        @Override
        public void run() {
            try {
                String address = doOperationMakerFile.getTextElaborated("ADDRESS");
                Socket socket = new Socket(address, 8004);
                invokerCommand.pressStartAll(socket);
                doOperationMaker = new DoOperationMaker(socket);
                doOperationMaker.startAllOperation();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            //return null;
        }
    }

    @Override
    public void onDestroy(){
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
    @Override
    public void onPress(int primaryCode) {
       playClick(primaryCode);
        try {
            if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                capsLock=!capsLock;
                keyboard.setShifted(capsLock);
                keyboardView.invalidateAllKeys();
            } else {
                invokerCommand.pressKey(primaryCode, capsLock);
            }
        }
        catch(Exception e){
            AlertDialog.Builder ac=new AlertDialog.Builder(keyboardView.getContext());
            ac.setTitle("Errore!");
            ac.setMessage("Impossibile connettersi al PC!");
            ac.setNeutralButton("Ok",null);
            ac.show();
            e.printStackTrace();
        }
    }
    private void playClick(int codeKey){
        AudioManager audio=(AudioManager)context.getSystemService(keyboardView.getContext().AUDIO_SERVICE);
        switch(codeKey){
            case 32:
                audio.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case 854:
                audio.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case 853:
                audio.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case 855:
                audio.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                audio.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                break;
        }
    }
    @Override
    public void onRelease(int primaryCode) {}
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {}
    @Override
    public void onText(CharSequence text) {}
    @Override
    public void swipeLeft() {}
    @Override
    public void swipeRight() {}
    @Override
    public void swipeDown() {}
    @Override
    public void swipeUp() {}
}
