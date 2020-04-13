package com.example.MyControl.interfaccia_grafica;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.MyControl.R;
import com.example.MyControl.facade.DoOperationMaker;
import com.example.MyControl.factory.FactoryFragment;
import com.example.MyControl.interfaccia_grafica.adapter_view.SlidingMenuAdapter;
import com.example.MyControl.interfaccia_grafica.model.ItemSlideMenu;
import java.io.File;
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
    private DoOperationMaker doOperationMaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try{
            doOperationMaker=new DoOperationMaker(this);
            File file=new File("config.properties");
            Log.d("DEBUG","ho istanziato il file");
            if(!(file.exists())){
                Log.d("File","esiste");
                doOperationMaker.writeAnything("ADDRESS 172.20.10.6");
            }
            else{
                Log.d("FILE","non esiste");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //ottieni le componenti
        listViewsliding=(ListView)findViewById(R.id.list_layout);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        //calcoliamo le misure dello schermo
        Display display=getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point point=new Point();
        display.getSize(point);
        final int screen_heightY=point.y;
        final int screen_widthX=point.x;
        list=new ArrayList<>();
        factoryFragment=new FactoryFragment();
        //aggiungiamo un elemento per la lista del menu
        list.add(new ItemSlideMenu(R.drawable.telecomando,"Telecomando"));
        list.add(new ItemSlideMenu(R.drawable.mouse,"Mouse"));
        list.add(new ItemSlideMenu(R.drawable.tastiera,"Tastiera"));
        list.add(new ItemSlideMenu(R.drawable.stereo,"Stereo_MP"));
        list.add(new ItemSlideMenu(R.drawable.pc,"Gestione_PC"));
        list.add(new ItemSlideMenu(R.drawable.impost,"Impostazioni"));
        //settiamo il nostro adapter
        slidingMenuAdapter=new SlidingMenuAdapter(list,this);
        listViewsliding.setAdapter(slidingMenuAdapter);
        lastTitle="Mouse";

        //serve per mostare l'icona per far apparire il menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //selezioniamo l'item che ci serve
        listViewsliding.setItemChecked(0,true);
        //visualizza fragment
        loadFragment(0,screen_widthX,screen_heightY);

        //settiamo i click dei menu...
        listViewsliding.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                factoryFragment.destroyFragmentLoaded();
                //imposta il titolo
                lastTitle=list.get(position).getTitle();
                setTitle(lastTitle);
                //seleziona Item
                listViewsliding.setItemChecked(position,true);
                //rimpiazza fragment
                loadFragment(position,screen_widthX,screen_heightY);
                //chiudi menu
                drawerLayout.closeDrawer(listViewsliding);
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

    private void loadFragment(int pos,int screen_width,int screen_height){
        Fragment fragment=factoryFragment.setFragment(pos,this,screen_width,screen_height,doOperationMaker);
        if(fragment!=null){
            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.replace(R.id.main_content,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}