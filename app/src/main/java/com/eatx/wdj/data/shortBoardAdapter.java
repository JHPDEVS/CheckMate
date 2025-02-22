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
import com.eatx.wdj.data.model.Post;
import com.eatx.wdj.R;
import com.eatx.wdj.ui.login.MainActivity;
import com.eatx.wdj.ui.main.Board;
import com.eatx.wdj.ui.main.DetailedView;
import com.eatx.wdj.ui.main.delBoardRequest;
import com.eatx.wdj.ui.main.nameRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class shortBoardAdapter extends RecyclerView.Adapter<shortBoardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Post> posts = new ArrayList<>();
    private String password;
    private int bno , no;

    public shortBoardAdapter (Context context,List<Post> posts){
        this.mContext = context;
        this.posts = posts;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle,  mCom , mWriter;
        private LinearLayout mContainer;

        public MyViewHolder (View view){
            super(view);

            mTitle = view.findViewById(R.id.mSubject);
            mCom = view.findViewById(R.id.mCom);
            mWriter = view.findViewById(R.id.mWriter);
            mContainer = view.findViewById(R.id.shortboard_container);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_board_item,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Post post = posts.get(position);

        holder.mCom.setText( "["+post.getType()+"]");
        holder.mTitle.setText(post.getSubject());
        holder.mWriter.setText(post.getWriter());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = position;
                Intent intent = new Intent(mContext, DetailedView.class);

                intent.putExtra("type",post.getType());
                intent.putExtra("title",post.getSubject());
                intent.putExtra("content",post.getContent());
                intent.putExtra("writer",post.getWriter());
                intent.putExtra("date",post.getWdate());

                bno = post.getBno();
                password = post.getPassword();
                showContent(post.getSubject(),post.getContent(),post.getWriter(),post.getWdate(),post.getBno(),post.getPassword());
                //mContext.startActivity(intent);

            }
        });
    }
    private void showContent(String title, String content,String writer , String wdate, int bno, String password) {
        final Dialog dialog = Util.getCustomDialog(mContext, R.layout.board_dialog);
        dialog.setCancelable(false);
        dialog.show();
        TextView dTitle = dialog.findViewById(R.id.dialog_title);
        TextView dContent = dialog.findViewById(R.id.dialog_content);
        ImageView dClose = dialog.findViewById(R.id.dialog_close_img);
        Button bClose = dialog.findViewById(R.id.dialog_close);
        TextView dDate = dialog.findViewById(R.id.wdate);
        TextView dWriter = dialog.findViewById(R.id.writer);
        dTitle.setText(title);
        dContent.setText(content);
        dDate.setText(wdate);
        dWriter.setText(writer);
        dContent.setMovementMethod(new ScrollingMovementMethod());
        int boardNum = bno;
        String pw = password;
        dClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog delete = new ProgressDialog(mContext);
                delete.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                delete.setMessage("게시글 삭제하는중~");
                delete.setCanceledOnTouchOutside(false);
                delete.show();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(bno + "번호");
                        System.out.println(password + "비번");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            System.out.println(success);
                            if(success) {
                                System.out.println(bno  + "지워짐");
                                delete.dismiss();
                            } else {
                                System.out.println("오류 ");
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                System.out.println(boardNum);
                System.out.println(pw);
                delBoardRequest delBoardRequest = new delBoardRequest(boardNum,pw,responseListener);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                queue.add(delBoardRequest);
                removeItem(no);
                System.out.println(no + "번째 데이터 삭제됨");
                dialog.dismiss();
            }
        });
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    public void updateData(int position) {
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }
}