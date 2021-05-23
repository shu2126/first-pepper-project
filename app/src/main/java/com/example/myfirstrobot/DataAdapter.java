package com.example.myfirstrobot;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataItem> mDataList;
    private Activity activity=null;

    public DataAdapter(Activity activity, ArrayList<DataItem> dataList) {
        this.mDataList = dataList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == Position.ViewType.LEFT_CONTENT){
            view = inflater.inflate(R.layout.left_item,parent,false);
            return new LeftViewHolder(view);
        }
        else if(viewType == Position.ViewType.CENTER_CONTENT){
            view = inflater.inflate(R.layout.left_item,parent,false);
            return new CenterViewHolder(view);
        }
        else {
            view = inflater.inflate(R.layout.right_item,parent,false);
            return new RightViewHolder(view);
        }
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof LeftViewHolder){
            ((LeftViewHolder)viewHolder).pepper_msg.setText(mDataList.get(position).getContent());
        }
        else  if(viewHolder instanceof CenterViewHolder){
            ((LeftViewHolder)viewHolder).pepper_msg.setText(mDataList.get(position).getContent());
        }
        else{
            ((RightViewHolder)viewHolder).human_msg.setText(mDataList.get(position).getContent());
        }

    }

    // 리사이클러뷰안에서 들어갈 뷰 홀더의 개수
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    // 이 메소드는 ViewType때문에 오버라이딩(구별하기 위함)
    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getViewType();
    }

    // "리사이클러뷰에 들어갈 뷰 홀더", 그리고 "그 뷰 홀더에 들어갈 아이템들을 셋팅"

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView pepper_msg;

        public LeftViewHolder(View itemView) {
            super(itemView);
            pepper_msg = (TextView)itemView.findViewById(R.id.pepper_msg);

        }
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView pepper_msg;

        public CenterViewHolder(View itemView) {
            super(itemView);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView human_msg;

        public RightViewHolder(View itemView) {
            super(itemView);
            human_msg = (TextView)itemView.findViewById(R.id.human_msg);
        }
    }

}