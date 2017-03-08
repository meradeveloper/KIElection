package com.example.in.kielection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Localadmin on 2/8/2017.
 */

/*public class SpinnerAdapter {
}*/

public class SpinnerAdapter extends ArrayAdapter<String> {

    ArrayList<String> data;

    int layoutID;

    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time,int layoutID) {

        super(context, textViewResourceId, arraySpinner_time);

        this.data = arraySpinner_time;
        this.layoutID=layoutID;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row=inflater.inflate(layoutID, parent, false);
        TextView label=(TextView)row.findViewById(R.id.category_name);

        label.setText(data.get(position).toString());

        return row;
    }
}