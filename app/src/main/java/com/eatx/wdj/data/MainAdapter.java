package com.eatx.wdj.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eatx.wdj.*;
import com.eatx.wdj.data.model.Post;
import com.eatx.wdj.data.model.TimeTableModel;
import com.eatx.wdj.data.model.mainModel;
import com.eatx.wdj.ui.login.MainActivity;
import com.eatx.wdj.ui.main.Board;
import com.eatx.wdj.ui.main.Check;
import com.eatx.wdj.ui.main.CheckActivity;
import com.eatx.wdj.ui.main.MainFragment;
import com.eatx.wdj.ui.MapsActivity;
import com.eatx.wdj.ui.main.TimeTable;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    private int mBackground;
    private ArrayList<mainModel> mValues = new ArrayList<>();
    View mView ;
    private RecyclerView recyclerView , timetableRecylcerView;
    private Context mContext;
    private List<Post> posts;
    private List<TimeTableModel> timetables;
    private RecyclerView.Adapter mAdapter , tableAdapter;
    private MainActivity activity;
    final static private String Boardurl = "https://ckmate.shop/.well-known/Board.php";
    final static private String TimeTableUrl = "https://ckmate.shop/.well-known/TodayTimeTable.php";
    public MainAdapter(Context mContext,ArrayList<mainModel> dataList , MainActivity activity) {
//        TypedValue mTypedValue = new TypedValue();
//        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        mBackground = mTypedValue.resourceId;
        this.mContext = mContext;
        mValues = dataList;
        this.activity = activity;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == codeViewType.ViewType.LEFT_CONTENT) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.row_main_fragment, parent, false);
            return new MainViewHolder(mView);
        }
        else if(viewType == codeViewType.ViewType.SHORT_BOARD) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.row_board, parent, false);
            return new BoardView(mView);
        }
        else if(viewType == codeViewType.ViewType.CHECK) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.row_check, parent, false);
            return new CheckView(mView);
        }
        else if(viewType == codeViewType.ViewType.TIME_TABLE) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.row_timetable, parent, false);
            return new TimeTableV(mView);
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.row_check, parent, false);
            return new CheckView(mView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            MainViewHolder holder2 = (MainViewHolder) holder;
            mainModel mInfo = getItemData(position);

            holder2.mTitle.setText(mInfo.mTitle);
            holder2.mText.setText(mInfo.mText);
            holder2.mSimpleTitle.setText(mInfo.mSimpleTitle);
            holder2.mSimpleText.setText(mInfo.mSimpleText);


            //버튼등에도 동일하게 지정할 수 있음 holder.버튼이름.setOnClickListener..형식으로
            holder2.mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                    if (position == 0) {
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(view.getContext(), CheckActivity.class);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        } else if(holder instanceof  CheckView) {
            CheckView checkView = (CheckView) holder;
            mainModel mainModel = getItemData(position);

            checkView.checkText.setText(mainModel.getName()+"님 출석하러 가세요");

            checkView.goCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MapsActivity.class);
                    v.getContext().startActivity(intent);
                }
            });


        }else if(holder instanceof  BoardView) {
            BoardView boardView = (BoardView) holder;
            mainModel mainModel = getItemData(position);

            boardView.mBoardTitle.setText(mainModel.getBoardTitle());
            recyclerView = mView.findViewById(R.id.shortboard);
            recyclerView.addItemDecoration(new DividerItemDecoration(mView.getContext(), 1));
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            posts = new ArrayList<>();
            mAdapter = new shortBoardAdapter(mContext,posts);
            getBoard();

            boardView.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)MainActivity.mContext).setBoardTab(4);
                    ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new Board())
                            .commit();
                }
            });
        } else if(holder instanceof TimeTableV) {
            TimeTableV timeTableV = (TimeTableV) holder;
            mainModel mainModel = getItemData(position);


            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Calendar time = Calendar.getInstance();
            String dayLongName = time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            String time1 = format1.format(time.getTime());
            timeTableV.classText.setText("오늘의 시간표");
            timeTableV.date.setText(" ("+time1+" " + dayLongName+")");
            timetableRecylcerView = mView.findViewById(R.id.timetablerecycle);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
            timetableRecylcerView.setLayoutManager(mLayoutManager);
            timetables = new ArrayList<>();
            tableAdapter = new TimeTableAdapter(mContext,timetables);
            getTimeTable();
            timeTableV.goMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)MainActivity.mContext).setBoardTab(3);
                    ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new TimeTable())
                            .commit();
                }
            });
        }

    }
    private void getBoard (){
        posts.clear();
        System.out.println("다이얼로그");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Boardurl,
                response -> {
                    try {

                        JSONArray array = new JSONArray(response);
                        System.out.println(array.length() + "길이");
                        int count=0;
                        while(count<array.length()){

                            JSONObject object = array.getJSONObject(count);
                            int bno = object.getInt("bno");
                            String id = object.getString("id");
                            String type = object.getString("type");
                            String password = object.getString("password");
                            String subject = object.getString("subject");
                            String content = object.getString("content");
                            String writer = object.getString("writer");
                            String wdate = object.getString("wdate");

                            Post post = new Post();
                            post.setBno(bno);
                            post.setId(id);
                            post.setPassword(password);
                            post.setSubject(subject);
                            post.setContent(content);
                            post.setWriter(writer);
                            post.setWdate(wdate);
                            post.setType(type);
                            posts.add(post);
                            mAdapter.notifyDataSetChanged();
                            count++;
                        }

                    }catch (Exception e){
                        System.out.println(e + "에러발생");
                    }

                    recyclerView.setAdapter(mAdapter);
                },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, error.toString(),Toast.LENGTH_LONG).show();

            }
        });

        Volley.newRequestQueue(mContext.getApplicationContext()).add(stringRequest);

    }
    private void getTimeTable (){
        timetables.clear();
        System.out.println("시간표");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, TimeTableUrl,
                response -> {
                    try {

                        JSONArray array = new JSONArray(response);
                        System.out.println(array.length() + "길이");
                        int count2=0;
                        while(count2<array.length()){

                            JSONObject object = array.getJSONObject(count2);
                            String classTitle = object.getString("classTitle");
                            String classPlace = object.getString("classPlace");
                            String professorName = object.getString("professorName");
                            int day = object.optInt("day",0);
                            int startHour = object.optInt("startHour",0);
                            int EndTime = object.optInt("EndTime",0);
                            String classValue = object.getString("class");

                            TimeTableModel times = new TimeTableModel();
                            times.setClassTitle(classTitle);
                            times.setClassPlace(classPlace);
                            times.setProfessorName(professorName);
                            times.setDay(day);
                            times.setStartTime(startHour);
                            times.setEndTime(EndTime);
                            times.setClassValue(classValue);
                            timetables.add(times);
                            tableAdapter.notifyDataSetChanged();
                            count2++;
                        }

                    }catch (Exception e){
                        System.out.println(e + "에러발생");
                    }

                    timetableRecylcerView.setAdapter(tableAdapter);
                },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, error.toString(),Toast.LENGTH_LONG).show();

            }
        });

        Volley.newRequestQueue(mContext.getApplicationContext()).add(stringRequest);

    }
    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getViewType();
    }






    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public mainModel getItemData(int position) {
        return mValues.get(position);
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {
        //        public final View mView;
        public final LinearLayout mSimpleLayout;
        public final TextView mTitle, mText, mSimpleTitle, mSimpleText ;

        public MainViewHolder(View mView) {
            super(mView);
//            this.mView = mView;

            mTitle = (TextView) mView.findViewById(R.id.mTitle);
            mText = (TextView) mView.findViewById(R.id.mText);
            mSimpleLayout = (LinearLayout) mView.findViewById(R.id.mSimpleLayout);
            mSimpleTitle = (TextView) mView.findViewById(R.id.mSimpleTitle);
            mSimpleText = (TextView) mView.findViewById(R.id.mSimpleText);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Toast.makeText(v.getContext(), pos + "클릭", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public class BoardView extends RecyclerView.ViewHolder {
        //        public final View mView;
        public final TextView mBoardTitle;
        public final TextView moreButton;
        public BoardView(View mView) {
            super(mView);
//            this.mView = mView;

       //     mID = (TextView) mView.findViewById(R.id.mID);
            mBoardTitle = (TextView) mView.findViewById(R.id.boardSubject);
            moreButton = (TextView) mView.findViewById(R.id.goToBoard);
        }
    }
    public class CheckView extends RecyclerView.ViewHolder {
        //        public final View mView;
        public final TextView checkText;
        public final RelativeLayout goCheck;
        public CheckView(View mView) {
            super(mView);
//            this.mView = mView;

            //     mID = (TextView) mView.findViewById(R.id.mID);
            checkText = (TextView) mView.findViewById(R.id.checkText);
            goCheck = (RelativeLayout) mView.findViewById(R.id.goCheck);
        }
    }
    public class TimeTableV extends RecyclerView.ViewHolder {
        //        public final View mView;
        public final TextView classText , date, goMore;
        public TimeTableV(View mView) {
            super(mView);
//            this.mView = mView;

            //     mID = (TextView) mView.findViewById(R.id.mID);
            classText = (TextView) mView.findViewById(R.id.classText);
            date = (TextView) mView.findViewById(R.id.today);
            goMore = (TextView) mView.findViewById(R.id.goMore);
        }
    }
}