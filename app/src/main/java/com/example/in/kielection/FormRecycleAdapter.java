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
import com.example.in.kielection.Common.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Localadmin on 1/24/2017.
 */

public class FormRecycleAdapter extends RecyclerView.Adapter<FormRecycleAdapter.ViewHolder> {
    private List<FormData> mDataset;
    Context context;
    private ImageLoader imageLoader;
    Preferences pref;
    String leadCandidate="";
    DB db;
    Validation validation;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvName;
        public EditText chValue;
        public TextView ivLogo;
        public CheckBox cbLead;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.n);
            chValue = (EditText) v.findViewById(R.id.vl);
            ivLogo = (TextView) v.findViewById(R.id.i);
            cbLead = (CheckBox) v.findViewById(R.id.lead);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FormRecycleAdapter(Container context, List<FormData> myDataset) {
        mDataset = myDataset;
        this.context=context;
        pref=new Preferences(context);
        db=new DB(context);
        validation= new Validation(context);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public FormRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    /*@Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.chValue != null && holder.chValue.getText().length() == 0) {
            holder.chValue.requestFocus();
        }
    }*/

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(final FormRecycleAdapter.ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FormData data = mDataset.get(position);
        holder.tvName.setText(data .getCandidateName());
        holder.chValue.setText(data .getValue());
        holder.ivLogo.setText(data .getPartyName());

        data.setVote(holder.chValue); // for setting editText in Form Data List
        data.setLead(holder.cbLead);  // for setting Checkbox in Form Data List

        holder.cbLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.cbLead.isChecked())
                {
                    uncheck(holder.cbLead);
                }

            }
        });


        holder.chValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //block();
                InsertInDB(position,  s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {



                block(holder.chValue);
                getTotalVotes();
                //InsertInDB(position,  s.toString());
            }
        });

        holder.chValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                holder.chValue.requestFocus();
                block(holder.chValue);
                getTotalVotes();
                return false;
            }
        });
}

    public void InsertInDB(int position,String value) {

        FormData data = mDataset.get(position);
        if(!value.trim().equals(""))
        {
            if(value.charAt(0)!='0' || value.isEmpty())
            {

                HashMap<String,String> map=new HashMap();
                map.put(DB.Table.Answere.candidateId.toString(),data.getCId());
                map.put(DB.Table.Answere.userID.toString(),pref.get(Constants.userId));
                map.put(DB.Table.Answere.categoryid.toString(),data.getCatId());
                map.put(DB.Table.Answere.partyId.toString(),data.getpId());
                map.put(DB.Table.Answere.stateId.toString(),pref.get(Constants.stateId));
                map.put(DB.Table.Answere.CstationId.toString(),pref.get(Constants.cStationId));
                map.put(DB.Table.Answere.ConsId.toString(),pref.get(Constants.ConstituencyID));
                map.put(DB.Table.Answere.votes.toString(),value);
                Log.v("ChangeText","ChangeText"+value);
                db.autoInsertUpdate(DB.Table.Name.Answere, map,DB.Table.Answere.partyId.toString()+" = "+data.getpId(),null);
            }
        }
        else
        {
            HashMap<String,String> map=new HashMap();
            map.put(DB.Table.Answere.candidateId.toString(),data.getCId());
            map.put(DB.Table.Answere.userID.toString(),pref.get(Constants.userId));
            map.put(DB.Table.Answere.categoryid.toString(),data.getCatId());
            map.put(DB.Table.Answere.partyId.toString(),data.getpId());
            map.put(DB.Table.Answere.stateId.toString(),pref.get(Constants.stateId));
            map.put(DB.Table.Answere.CstationId.toString(),pref.get(Constants.cStationId));
            map.put(DB.Table.Answere.ConsId.toString(),pref.get(Constants.ConstituencyID));
            map.put(DB.Table.Answere.votes.toString(),value);
            Log.v("ChangeText","ChangeText"+value);
            db.delete(DB.Table.Name.Answere, DB.Table.Answere.partyId.toString()+"="+data.getpId(),null);
        }


    }

    void uncheck(CheckBox cb)
    {
        for (int i=0;i<mDataset.size();i++)
        {
            FormData fd=mDataset.get(i);

            if(cb!=fd.getLead())
            fd.getLead().setChecked(false);
        }
    }

    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String blockCharacterSet = "1234567890";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    public static InputFilter filter1 = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(0, 1000000, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }
    };

    public static boolean isInRange(double a, double b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }


    InputFilter filterset =  new InputFilter.LengthFilter(0);
    InputFilter filterremove = new InputFilter.LengthFilter(7);

    void block(EditText et)
    {

        if (!Container.spinStatus.getSelectedItem().equals("Select"))
        {
            et.setFilters(new InputFilter[] { filter });
            //et.setFilters(new InputFilter[] { new InputFilterMinMax("0","0")});
            Log.v("spinStatus",Container.spinStatus.getSelectedItem().toString());
            Log.v("spinStatus",et.toString());
            et.setError("Can't Edit at the time of Selecting Status!");
        }
        else
        {
            //et.setError(null);
            //et.setFilters(new InputFilter[] {});
            Log.v("edittext1","edittext:"+et.getText().toString());
            //et.setFilters(new InputFilter[] { new InputFilterMinMax("0","1000000")});
        }

        if (!Container.spinLead.getSelectedItem().equals("Select Leading Candidate"))
        {
            et.setFilters(new InputFilter[] { filter });
            //et.setFilters(new InputFilter[] { new InputFilterMinMax("0","0")});
            et.setError("Can't Edit at the time of Submit Lead!");
        }

        else if(Container.spinStatus.getSelectedItem().equals("Select"))
        {
            et.setError(null);
            //et.setFilters(new InputFilter[] {});
            Log.v("edittext","edittext:"+et.getText().toString());
            et.setFilters(new InputFilter[] { filter1});
        }

        boolean status=true;

        /* checking for leading candidate */

        EditText leadet=getLeadEditText();

        for (int i=0;i<mDataset.size();i++)
        {
            FormData fd=mDataset.get(i);

            if(leadet==null)
            {
                fd.getVote().setError("You cannot enter vote before submitting Leading Candidate");
                Log.v("leadnull","null");
            }

            if(fd.getVote()!=null && leadet!=null)
            if(!fd.getVote().getText().toString().isEmpty() && !leadet.getText().toString().isEmpty())
            {
                Log.v("emptystate","nonempty");
                status=false;

                if(Integer.parseInt(fd.getVote().getText().toString())>Integer.valueOf(leadet.getText().toString()))
                {
                    fd.getVote().setError(Utils.getSpinnerItemId(DB.Table.Name.PartyDetail,DB.Table.PartyDetail.CandidateName.toString(),DB.Table.PartyDetail.CandidateID.toString(),leadCandidate,context)+" should have more Votes!");
                }
                else
                    fd.getVote().setError(null);

            }


        }


    }

    public EditText getLeadEditText() {

        EditText leadet = null;
        for (int i = 0; i < mDataset.size(); i++) {
            FormData fd = mDataset.get(i);


            if(!Container.RoundID2.isEmpty())
            {
                leadCandidate=validation.getLeadingCandidate(Container.ConstituencyID, "lead", Container.RoundID2);
                Log.v("leadCandidate","leadCandidate"+leadCandidate);
                if (leadCandidate.equals(fd.getCId())) {

                    Log.v("leadingEdit", "leadingEdit1"+String.valueOf(fd.getVote()));
                    leadet = (EditText) fd.getVote();

                }
            }


            //Log.v("leadingEdit", "leadingEdit"+String.valueOf(fd.getVote()));

        }
        return leadet;
    }



    void getTotalVotes()
    {
        int TotalVotes=0;
        for (int i=0;i<mDataset.size();i++)
        {
            FormData fd=mDataset.get(i);

            if(!fd.getVote().getText().toString().isEmpty())
                TotalVotes=TotalVotes+Integer.parseInt(fd.getVote().getText().toString());

            Container.etTotalVotes.setText(String.valueOf(TotalVotes));

        }
    }

    private void loadImage(String url,TextView imageView){


        imageLoader = CustomVolleyRequest.getInstance(context)
                .getImageLoader();
      /*  imageLoader.get(url, ImageLoader.getImageListener(imageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));*/
      //  imageView.setImageUrl(url, imageLoader);
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
