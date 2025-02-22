package com.eatx.wdj.data;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.eatx.wdj.data.model.Checkers;
import com.eatx.wdj.data.model.Post;
import com.eatx.wdj.R;
import com.eatx.wdj.data.model.Rankers;
import com.eatx.wdj.ui.login.MainActivity;
import com.eatx.wdj.ui.main.Board;
import com.eatx.wdj.ui.main.Check;
import com.eatx.wdj.ui.main.DetailedView;
import com.eatx.wdj.ui.main.delBoardRequest;
import com.eatx.wdj.ui.main.nameRequest;
import com.google.android.gms.common.data.DataHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CheckStateAdapter extends RecyclerView.Adapter<CheckStateAdapter.MyViewHolder> implements Filterable{

    private Context mContext;
    private List<Checkers> checkers = new ArrayList<>();
    private List<Checkers> filteredList = new ArrayList<>();
    private String password;
    private int bno , no;

    public CheckStateAdapter (Context context,List<Checkers> checkers){
        this.mContext = context;
        this.checkers = checkers;
        this.filteredList  = checkers;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = checkers;
                } else {
                    List<Checkers> filteringList = new ArrayList<Checkers>();
                    for(Checkers item : checkers) {
                        if(item.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                     filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Checkers>)results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView id, name , sid, classR , time, countLate;
        private LinearLayout mContainer;

        public MyViewHolder (View view){
            super(view);

            name = view.findViewById(R.id.nameR);
            sid = view.findViewById(R.id.sidR);
            classR = view.findViewById(R.id.classR);
            time = view.findViewById(R.id.time);
            mContainer = view.findViewById(R.id.checkState_container);


        }
    }
    private void showContent(String nameValue, int wSid,String wClass, String wdate, String content ,String serverTime) {
        final Dialog dialog = Util.getCustomDialog(mContext, R.layout.dialog_checkstate);
        dialog.setCancelable(false);
        dialog.show();
        TextView name = dialog.findViewById(R.id.name);
        TextView dContent = dialog.findViewById(R.id.dialog_content);
        Button bClose = dialog.findViewById(R.id.dialog_close);
        TextView wDate = dialog.findViewById(R.id.wdate);
        TextView sid = dialog.findViewById(R.id.wSID);
        TextView classValue = dialog.findViewById(R.id.wClass);
        TextView st  = dialog.findViewById(R.id.server_time);
        st.setText(serverTime);
        name.setText(nameValue);
        dContent.setText(content);
        wDate.setText(wdate);
        sid.setText(String.valueOf(wSid));
        classValue.setText(wClass);
        dContent.setMovementMethod(new ScrollingMovementMethod());

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_check_item,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Checkers check = filteredList.get(position);

        holder.name.setText(check.getName());
        holder.sid.setText(String.valueOf(check.getSid()));
        holder.classR.setText(check.getClassValue());
        holder.time.setText(check.getTimestamp());

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = position;

                showContent(check.getName(), check.getSid(),check.getClassValue() ,check.getDate() +"\n"+ check.getTimestamp(),"지각 패널티 : +" + check.getRun()+"바퀴" + "\n" , "서버에 요청된 시간 : " + check.getServerDate() + " " + check.getServerTime());
                //mContext.startActivity(intent);

            }
        });
    }

    public void updateData(int position) {
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        checkers.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterList(List<Checkers> filteredlist) {
        checkers = filteredlist;
        notifyDataSetChanged();
    }
}