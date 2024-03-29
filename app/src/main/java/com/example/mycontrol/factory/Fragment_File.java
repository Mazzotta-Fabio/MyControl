package com.example.mycontrol.factory;

import android.app.Activity;
import android.app.AlertDialog;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mycontrol.R;
import com.example.mycontrol.action.ReceiverCommand;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.action.ServiceNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fragment_File extends Fragment implements View.OnClickListener{
    private String chooseItem;
    private ServiceFile serviceFile;
    private ServiceNetwork serviceNetwork;
    private Context context;
    private TextView txtConnessiFile;
    private TextView txtNomeFile;
    private ArrayList<Uri>nomiFile;
    private Handler handler;

    public Fragment_File(Context context,ServiceFile serviceFile)throws IOException{
        this.serviceFile=serviceFile;
        this.context=context;
        nomiFile=new ArrayList<Uri>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.fragment_file,container,false);
        ArrayList<View> buttons=(rootView.findViewById(R.id.layout_file).getTouchables());
        for(View v:buttons) {
            Button b = (Button) v;
            b.setOnClickListener(this);
        }
        txtConnessiFile=(TextView)rootView.findViewById(R.id.txtconnessofile);
        txtNomeFile=(TextView)rootView.findViewById(R.id.txtnomefile);
        container.setBackgroundColor(Color.GREEN);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*
        utile per gestire i processi in maniera asincrona. qui ne creiamo uno
        */
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //permette di inviare messaggi ad thread associato
        handler = new Handler(Looper.getMainLooper());
        executor.execute(new HelpThreadActivity());
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.CaricaFile){
            nomiFile.removeAll(nomiFile);
            //fileDaCaricare.removeAll(fileDaCaricare);
            //imposta alert
            AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
            ac.setTitle("Seleziona cosa vuoi inviare!");
            final String listItemArr[] = {"Immagini", "Documenti", "Video", "Audio"};
            ac.setSingleChoiceItems(listItemArr, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int itemIndex) {
                    Log.d("DEBUG","VALORE "+listItemArr[itemIndex]);
                    chooseItem = listItemArr[itemIndex];
                }
            });

            ac.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    partiIntent(chooseItem);
                }
            });
            ac.show();
        }
        else{
            //invio dati
            if(nomiFile.size()==0){
                AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
                ac.setTitle("Errore!");
                ac.setMessage("Devi caricare dei file prima di effettuare l'invio!");
                ac.setNeutralButton("Ok", null);
                ac.show();
            }
            else{
                boolean flag=true;
                Log.d("DEBUG","SONO IN INVIAFILE dimensioneNomiFile "+nomiFile.size());
                for(int i=0;i<nomiFile.size();i++){
                    try{
                        Log.d("DEBUG","SONO IN INVIAFILE sto leggendo file n° "+ (i+1));
                        //serviceNetwork.writeSocket("invioFile "+nomiFile.get(i));
                        File file=context.getFileStreamPath(getFileName(nomiFile.get(i)));
                        serviceNetwork.writeStream(file,getFileName(nomiFile.get(i)));
                        //serviceNetwork.writeStream(context.openFileInput(getFileName(nomiFile.get(i))),getFileName(nomiFile.get(i)));
                    }
                    catch (Exception e){
                        flag=false;
                        txtConnessiFile.setText("Non Connesso");
                        AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
                        ac.setTitle("Errore!");
                        ac.setMessage("Impossibile connettersi al PC!");
                        ac.setNeutralButton("Ok", null);
                        ac.show();
                    }
                }
                if(flag){
                    AlertDialog.Builder ac = new AlertDialog.Builder(v.getContext());
                    ac.setTitle("Messaggio!");
                    ac.setMessage("Hai inviato correttamente i file");
                    ac.setNeutralButton("Ok", null);
                    ac.show();
                }
                nomiFile.removeAll(nomiFile);
                txtNomeFile.setText("Nessun file selezionato");
            }
        }
    }

    public void partiIntent(String chooseItem){
        Intent in = null;
        switch (chooseItem) {
            case "Immagini":
                //usato per accedere alla galleria e per prendere il file selezionato
                in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
            case "Video":
                //usato per accedere ai video
                in = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                break;
            case "Audio":
                //usato per accedere ai file audio
                in = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                break;
            case "Documenti":
                //per accdere ai documenti
                in= new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("*/*");
                break;
        }
        if(!(chooseItem.equals("Documenti"))){
            Log.d("DEBUG","SONO QUII IN NON DOCUMENTI");
            in.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        }
        someActivityResultLauncher.launch(in);
        Log.d("DEBUG","SONO QUII HO FINITO");
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("DEBUG","SONO QUIII");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri=data.getData();
                        if(uri!=null){
                            Log.d("DEBUG","SONO QUIII "+uri.getPath());
                            //importiamo il file selezionato
                            importFile(uri);
                            aggiornaGui();
                        }
                        else{
                            ClipData clipDatas=data.getClipData();
                            Log.d("DEBUG","SONO QUI ho ottenuto i dati "+clipDatas.getItemCount());
                            if(clipDatas.getItemCount()>4){
                                AlertDialog.Builder ac = new AlertDialog.Builder(context);
                                ac.setTitle("Errore!");
                                ac.setMessage("Non puoi selezionare più di 4 immagini!");
                                ac.setPositiveButton("OK",null);
                                ac.show();
                                Log.d("DEBUG","SONO in lancia alert");
                            }
                            else{
                                for(int i=0;i<clipDatas.getItemCount();i++) {
                                    ClipData.Item item = clipDatas.getItemAt(i);
                                    Uri uri1 = item.getUri();
                                    Log.d("DEBUG", "SONO QUIII in Clip" + uri1.getPath());
                                    //importiamo il file selezionato
                                    importFile(uri1);
                                }
                                aggiornaGui();
                            }
                        }
                    }
                }
            });

    private void aggiornaGui(){
        String etichetta="";
        for(int i=0;i<nomiFile.size();i++){
            etichetta=etichetta +getFileName(nomiFile.get(i)) + "\n";
        }
        txtNomeFile.setText(etichetta);
        //nomiFile.removeAll(nomiFile);
    }

    private void importFile(Uri uri){
        Log.d("DEBUG","sono in Import File");
        try{
            String fileName=getFileName(uri);
            Log.d("DEBUG","sono qui dopo getFileName "+fileName);
            if(fileName==null){
                Log.d("DEBUG","SONO IN STATO NON TROVATO URI");
            }
            else{
                //creiamo il file temporaneo che deve essere creato
                Log.d("DEBUG","sono qui prima di filecopy");
                //nomiFile.add(fileName);
                nomiFile.add(uri);
                copyToTempFile(fileName,uri);
                FileInputStream os=context.openFileInput(fileName);
                byte [] buf=new byte[1024];
                int len,i=0;
                while((len=os.read(buf))>0){
                    i=i+len;
                    //Log.d("DEBUG","SONO QUI IL FILE NON è Vuoto "+len);
                }
                Log.d("DEBUG","SONO IN DIMESIONE FILE: "+i);
                //inviamo fileCopy al socket
                if(i>100000){
                    AlertDialog.Builder ac = new AlertDialog.Builder(context.getApplicationContext());
                    ac.setTitle("Errore!");
                    ac.setMessage("Il File è troppo grande (MAX 10 MB)!");
                    ac.setPositiveButton("OK",null);
                    ac.show();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getFileName(Uri uri){
        //otteniamo informazioni di questo uri tramite un cursore
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        if(cursor.getCount()<=0){
            cursor.close();
            throw  new IllegalArgumentException("Impossibile ottenere il nome del file, il cursore è vuoto");
        }
        cursor.moveToFirst();
        String fileName=cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    private void copyToTempFile(String fileName,Uri uri) throws Exception{
        //ottieni uno stream di byte da un URI
        BufferedReader br =new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri)));
        Log.d("DEBUG","sono qui in copy: dopo getResolver");
        FileOutputStream os=context.openFileOutput(fileName, Context.MODE_PRIVATE);
        Log.d("DEBUG","sono qui in copy: apriamo file in scrittura");
        String line=null;
        while((line=br.readLine())!=null){
            os.write(line.getBytes());
        }
        Log.d("DEBUG","Sono DOPO IL WHILE: ");
    }

    private class HelpThreadActivity implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("DEBUG","Sono nel thread FragmentFile");
                serviceNetwork=new ServiceNetwork();
                String address = serviceFile.readFile("ADDRESS");
                Socket socket = new Socket(address, 8004);
                serviceNetwork.setSocket(socket);
                serviceNetwork.sendLocalAddress();
                Log.d("DEBUG", "sono in fragment file mi sono connesso");
                String value = serviceNetwork.readSocket();
                handler.post(new UpdateGui(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateGui implements Runnable{
        private String value;
        UpdateGui(String value) {
            this.value=value;
        }
        public void run() {
            if(value!=null){
                txtConnessiFile.setText("Connesso con "+ value);
            }
        }
    }


    public void onDestroy(){
        super.onDestroy();
        try{
            if(serviceNetwork!=null) {
                serviceNetwork.closeSocketStream();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
