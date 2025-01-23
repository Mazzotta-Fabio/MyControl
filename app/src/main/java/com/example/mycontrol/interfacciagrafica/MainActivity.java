package com.example.mycontrol.interfacciagrafica;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import com.example.mycontrol.R;
import com.example.mycontrol.action.ServiceFile;
import com.example.mycontrol.adapterview.ItemSlideMenu;
import com.example.mycontrol.adapterview.SlidingMenuAdapter;
import com.example.mycontrol.factory.FactoryFragment;
import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private List<ItemSlideMenu> list;
    private SlidingMenuAdapter slidingMenuAdapter;
    private ListView listViewsliding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FactoryFragment factoryFragment;
    private String lastTitle;
    private ServiceFile serviceFile;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try{
            serviceFile=new ServiceFile(this);
            serviceFile.writeFile("ADDRESS","192.168.96.241");
        }
        catch (IOException e){
            Log.d("ERROR", e.getMessage());
        }

        //ottieni le componenti
        listViewsliding=(ListView)findViewById(R.id.list_layout);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        //calcoliamo le misure dello schermo
        WindowMetrics displayMetrics=getWindowManager().getCurrentWindowMetrics();
        final int screen_heightY=displayMetrics.getBounds().height();
        final int screen_widthX=displayMetrics.getBounds().width();
        list=new ArrayList<>();
        factoryFragment=new FactoryFragment();
        //aggiungiamo un elemento per la lista del menu
        list.add(new ItemSlideMenu(R.drawable.telecomando,"Telecomando"));
        list.add(new ItemSlideMenu(R.drawable.mouse,"Mouse"));
        list.add(new ItemSlideMenu(R.drawable.tastiera,"Tastiera"));
        list.add(new ItemSlideMenu(R.drawable.stereo,"Stereo_MP"));
        list.add(new ItemSlideMenu(R.drawable.pc,"Gestione_PC"));
        list.add(new ItemSlideMenu(R.drawable.file,"Carica File"));
        list.add(new ItemSlideMenu(R.drawable.impost,"Impostazioni"));
        list.add(new ItemSlideMenu(R.drawable.logout,"Chiudi l'applicazione"));
        //settiamo il nostro adapter
        slidingMenuAdapter=new SlidingMenuAdapter(list,this);
        listViewsliding.setAdapter(slidingMenuAdapter);
        lastTitle="Telecomando";

        //serve per mostare l'icona per far apparire il menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //selezioniamo l'item che ci serve
        listViewsliding.setItemChecked(0,true);
        //visualizza fragment
        try {
            loadFragment(0,screen_widthX,screen_heightY);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        //settiamo i click dei menu...
        listViewsliding.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                factoryFragment.destroyFragmentLoaded();
                if(position==list.size()-1){
                    finish();
                }
                else{
                    //imposta il titolo
                    lastTitle=list.get(position).getTitle();
                    setTitle(lastTitle);
                    //seleziona Item
                    listViewsliding.setItemChecked(position,true);
                    //rimpiazza fragment
                    try{
                        loadFragment(position,screen_widthX,screen_heightY);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    //chiudi menu
                    drawerLayout.closeDrawer(listViewsliding);
                }
            }
        });

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle("MyControl");
                factoryFragment.destroyFragmentLoaded();
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setTitle(lastTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.openDrawer(listViewsliding);
    }

    public void onDestroy(){
        super.onDestroy();
        try {
            serviceFile.writeFile("ADDRESS",serviceFile.readFile("ADDRESS"));
            Log.d("DEBUG","SONO IN ONDESTROY E SALVO FILE di MAIN ACTIVITY");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onPause(){
        super.onPause();
        try {
            serviceFile.writeFile("ADDRESS",serviceFile.readFile("ADDRESS"));
            Log.d("DEBUG","SONO IN SALVA FILE di MAIN ACTIVITY");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This method should be called by your Activity's onOptionsItemSelected method.
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
        actionBarDrawerToggle.syncState();
    }

    //creaiamo il metodo che crea e rimpiazza i fragment dell'applicazione

    private void loadFragment(int pos,int screen_width,int screen_height) throws Exception {
        fragment=factoryFragment.setFragment(pos,this,screen_width,screen_height,serviceFile);
        if(fragment!=null){
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.replace(R.id.main_content,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}