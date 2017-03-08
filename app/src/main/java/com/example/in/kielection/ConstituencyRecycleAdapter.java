package com.example.in.kielection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.CustomVolleyRequest;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.InputFilterMinMax;
import com.example.in.kielection.Common.Preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Localadmin on 1/24/2017.
 */

public class ConstituencyRecycleAdapter extends RecyclerView.Adapter<ConstituencyRecycleAdapter.ViewHolder> {
    private List<ConstituencyData> mDataset;
    Context context;
    private ImageLoader imageLoader;
    Preferences pref;
    DB db;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvConstituency;
        public EditText etCode;
        public EditText etConfirmCode;


        public ViewHolder(View v) {
            super(v);

            tvConstituency  = (TextView) v.findViewById(R.id.tvconstituency);
            etCode          = (EditText) v.findViewById(R.id.etcode);
            etConfirmCode   = (EditText) v.findViewById(R.id.etconfirmcode);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConstituencyRecycleAdapter(Context context, List<ConstituencyData> myDataset) {
        mDataset = myDataset;
        this.context=context;
        pref=new Preferences(context);
        db=new DB(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConstituencyRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.constituency_recycle_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(final ConstituencyRecycleAdapter.ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ConstituencyData data = mDataset.get(position);

        holder.tvConstituency.setText(data.getConstituency());
        data.setEtConstituency(holder.etCode);
        data.setEtConfirmConstituency(holder.etConfirmCode);


        holder.etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //validate(holder.etCode,s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

                validate(holder.etCode,s.length());
                matching(holder.etCode,holder.etConfirmCode);
            }
        });

        holder.etConfirmCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                matching(holder.etCode,holder.etConfirmCode);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //validate(holder.etCode,s.length());
                matching(holder.etCode,holder.etConfirmCode);
            }

            @Override
            public void afterTextChanged(Editable s) {

                validate(holder.etConfirmCode,s.length());
                matching(holder.etCode,holder.etConfirmCode);
            }
        });


}

    public void validate(EditText editText , int... s)
    {
        int first    =  s.length > 0 ? (int)s[0]  : 0;
        int second    =  s.length > 1 ? (int)s[1]     : 0;

        if(first<5)
            editText.setError("Code should be 5 digit");
        else
            editText.setError(null);

    }

    public void matching(EditText etContituency,EditText etConfirmContituency)
    {
        if(!etConfirmContituency.getText().toString().isEmpty() && !etContituency.getText().toString().isEmpty())
            if(!etContituency.getText().toString().equals(etConfirmContituency.getText().toString()))
            {
                etConfirmContituency.setError("Code Not Matched");
                etContituency.setError("Code Not Matched");
            }
            else
            {
                etContituency.setError(null);
                etConfirmContituency.setError(null);
            }

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
