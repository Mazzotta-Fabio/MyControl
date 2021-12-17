package com.example.mycontrol.factory;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ServiceFile;

import java.io.IOException;

public class Fragment_Impostazioni extends Fragment {
    private EditText ipAddress;
    private Context context;
    private View rootView;
    private ServiceFile serviceFile;

    public Fragment_Impostazioni(Context context,ServiceFile serviceFile) throws IOException{
        this.context=context;
        this.serviceFile=serviceFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try{
            rootView=inflater.inflate(R.layout.fragment_impostazioni,container,false);
            ipAddress=(EditText)rootView.findViewById(R.id.ipAddress);
            container.setBackgroundColor(Color.WHITE);
            return rootView;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void onDestroy(){
        super.onDestroy();
        try{
            String ip = ipAddress.getText().toString();
            Log.d("DEBUG", "sono in salvamento delle impostazioni "+ip);
            if(!(ip.equals(""))){
                serviceFile.writeFile("ADDRESS",ip.trim());
            }
            InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.findViewById(R.id.ipAddress).getWindowToken(),0);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}