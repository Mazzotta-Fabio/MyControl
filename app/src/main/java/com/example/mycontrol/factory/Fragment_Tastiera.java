package com.example.mycontrol.factory;


import android.app.*;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.*;
import android.media.AudioManager;
import android.os.*;
import android.util.Log;
import android.view.*;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_Tastiera extends Fragment implements KeyboardView.OnKeyboardActionListener{
    private Keyboard keyboard;
    private KeyboardView keyboardView;
    private boolean capsLock;
    private Context context;
    private ServiceNetwork serviceNetwork;
    private ServiceFile serviceFile;
    private ReceiverCommand receiverCommand;

    public Fragment_Tastiera(Context context,ServiceFile serviceFile)throws IOException{
        this.context=context;
        this.serviceFile=serviceFile;
        capsLock=false;
        //serviceNetwork=new ServiceNetwork();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        keyboardView=(KeyboardView)inflater.inflate(R.layout.fragment_tastiera,container,false);
        keyboard=new Keyboard(context,R.xml.qwerty);
        keyboardView.setKeyboard(keyboard);
        container.setBackgroundColor(Color.CYAN);
        keyboardView.setOnKeyboardActionListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*
        utile per gestire i processi in maniera asincrona. qui ne creiamo uno
        */
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //permette di inviare messaggi ad thread associato
        executor.execute(new HelpThreadActivity());
        return keyboardView;
    }

    private class HelpThreadActivity implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("DEBUG","SONO NEL THREAD Tastiera");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                Log.d("DEBUG", "sono in fragment Tastiera Sto agganciato");
                receiverCommand=new ReceiverCommand(serviceNetwork);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(serviceNetwork!=null){
            try{
                serviceNetwork.closeSocketStream();
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
                receiverCommand.writeMediaKeyboard(primaryCode, capsLock);
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
