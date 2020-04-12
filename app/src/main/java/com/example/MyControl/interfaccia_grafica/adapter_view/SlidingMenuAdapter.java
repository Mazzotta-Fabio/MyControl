package com.example.MyControl.interfaccia_grafica.adapter_view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.MyControl.R;
import com.example.MyControl.interfaccia_grafica.model.ItemSlideMenu;
import java.util.List;



public class SlidingMenuAdapter extends BaseAdapter {
    private Context context;
    private List<ItemSlideMenu> list;
    public SlidingMenuAdapter(List<ItemSlideMenu> list, Context context) {
        this.context=context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context, R.layout.item_sliding_menu,null);
        ImageView img=(ImageView)v.findViewById(R.id.item_gestionePC);
        TextView txt=(TextView)v.findViewById(R.id.item_title);
        ItemSlideMenu item=list.get(position);
        img.setImageResource(item.getImgId());
        txt.setText(item.getTitle());
        return v;
    }
}


