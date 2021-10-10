package com.example.MyControl.factory;

import android.app.*;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.MyControl.R;
import com.example.MyControl.facade.DoOperationMaker;
import java.io.IOException;

public class Fragment_Impostazioni extends Fragment {
    private EditText ipAddress;
    private DoOperationMaker doOperationMaker;
    private Context context;
    private View rootView;
    public Fragment_Impostazioni(Context context,DoOperationMaker doOperationMaker){
        this.doOperationMaker=doOperationMaker;
        this.context=context;
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
            Log.d("ADDRESS", ip);
            doOperationMaker.writeAnything("ADDRESS " + ip);
            InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.findViewById(R.id.ipAddress).getWindowToken(),0);
        }
        catch (IOException e){
         e.printStackTrace();
        }
    }
}