package com.example.in.kielection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Localadmin on 1/24/2017.
 */

public class UpdatetConstituencyRecycleAdapter extends RecyclerView.Adapter<UpdatetConstituencyRecycleAdapter.ViewHolder> {


    private ArrayList<HashMap<String,String>> mDataset;
    public static   ArrayList<HashMap<String,Object>> mObjectset= new ArrayList<>();

    Context context;

    Preferences pref;

    DB db;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvSubmit;
        public EditText etConstituency;
        public RadioButton rbYes;
        public RadioButton rbNo;

        public ViewHolder(View v) {
            super(v);
            etConstituency = (EditText) v.findViewById(R.id.et);
            tvSubmit = (TextView) v.findViewById(R.id.submit);
            rbYes = (RadioButton) v.findViewById(R.id.yes);
            rbNo = (RadioButton) v.findViewById(R.id.no);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UpdatetConstituencyRecycleAdapter(Context context, ArrayList<HashMap<String,String>> myDataset) {
        mDataset = myDataset;
        this.context=context;
        pref=new Preferences(context);
        db=new DB(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_adapter, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.etConstituency.setText(mDataset.get(position).get("ConstituencyID"));
        HashMap<String,Object> objectMap= new HashMap<>();
        objectMap.put("edittext",holder.etConstituency);
        mObjectset.add(objectMap);

        holder.rbYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etConstituency.setEnabled(false);
            }
        });

        holder.rbNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etConstituency.setEnabled(true);
            }
        });
}

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
