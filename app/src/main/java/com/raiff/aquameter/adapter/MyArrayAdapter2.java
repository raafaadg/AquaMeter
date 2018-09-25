package com.raiff.aquameter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raiff.aquameter.R;
import com.raiff.aquameter.model.MyDataModel2;

import java.util.List;

public class MyArrayAdapter2 extends ArrayAdapter<MyDataModel2> {

    List<MyDataModel2> modelList;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public MyArrayAdapter2(Context context, List<MyDataModel2> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        modelList = objects;
    }

    @Override
    public MyDataModel2 getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.layout_row_view2, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        MyDataModel2 item = getItem(position);

        vh.tv_id.setText(item.getId());
        vh.tv_time.setText(item.getTime());
        vh.tv_data.setText(item.getData());


        return vh.rootView;
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;

        public final TextView tv_id;
        public final TextView tv_time;
        public final TextView tv_data;

        private ViewHolder(RelativeLayout rootView,
                           TextView tv_id,
                           TextView tv_time,
                           TextView tv_data) {
            this.rootView = rootView;
            this.tv_id = tv_id;
            this.tv_time = tv_time;
            this.tv_data = tv_data;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            TextView tv_id = (TextView) rootView.findViewById(R.id.tv_id);
            TextView tv_time = (TextView) rootView.findViewById(R.id.tv_time);
            TextView tv_data = (TextView) rootView.findViewById(R.id.tv_data);

            return new ViewHolder(rootView,
                    tv_id,
                    tv_time,
                    tv_data);
        }
    }
}