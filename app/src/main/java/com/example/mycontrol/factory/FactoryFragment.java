package com.example.mycontrol.factory;

import androidx.fragment.app.*;

import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.interfacciagrafica.MainActivity;



public class FactoryFragment {
    private Fragment fragment;

    public FactoryFragment(){
        fragment=null;
    }

    public Fragment setFragment(int position, MainActivity activity, int screen_width, int screen_height, ServiceFile srv) throws Exception{
        switch (position) {
            case 0:
                //fatto
                fragment = new Fragment_Telecomando(activity,srv);
                break;
            case 1:
                fragment = new Fragment_Mouse(activity,srv);
                break;
            case 2:
                //fatto
                fragment = new Fragment_Tastiera(activity,srv);
                break;
            case 3:
                fragment = new Fragment_Musica(activity,srv);
                break;
            case 4:
                fragment = new Fragment_PC_Menu(activity,screen_height,screen_width,srv);
                break;
            case 5:
                fragment = new Fragment_File(activity,srv);
                break;
            case 6:
                fragment = new Fragment_Impostazioni(activity,srv);
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
