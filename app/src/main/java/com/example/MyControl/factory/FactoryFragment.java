package com.example.MyControl.factory;

import android.app.*;
import com.example.MyControl.facade.DoOperationMaker;
import com.example.MyControl.interfaccia_grafica.MainActivity;

public class FactoryFragment {
    private Fragment fragment;

    public FactoryFragment(){
        fragment=null;
    }

    public Fragment setFragment(int position, MainActivity activity, int screen_width, int screen_height, DoOperationMaker doOperationMaker){
        switch (position) {
            case 0:
                fragment = new Fragment_Telecomando(activity,doOperationMaker);
                break;
            case 1:
                fragment = new Fragment_Mouse(activity,doOperationMaker);
                break;
            case 2:
                fragment = new Fragment_Tastiera(activity,doOperationMaker);
                break;
            case 3:
                fragment = new Fragment_Musica(activity,doOperationMaker);
                break;
            case 4:
                fragment = new Fragment_PC_Menu(activity,screen_height,screen_width,doOperationMaker);
                break;
            case 5:
                fragment = new Fragment_Impostazioni(activity, doOperationMaker);
                break;
        }
        return fragment;
    }

    public void destroyFragmentLoaded(){
        if(fragment!=null){
            fragment.onDestroy();
        }
    }
}
