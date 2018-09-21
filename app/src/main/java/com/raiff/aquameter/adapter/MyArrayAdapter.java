package com.raiff.aquameter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.raiff.aquameter.R;
import com.raiff.aquameter.model.MyDataModel;

import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<MyDataModel> {

    List<MyDataModel> modelList;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public MyArrayAdapter(Context context, List<MyDataModel> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        modelList = objects;
    }

    @Override
    public MyDataModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.layout_row_view, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        MyDataModel item = getItem(position);

        vh.tv_title.setText(item.getTitle());
        vh.tv_data.setText(item.getData());


        return vh.rootView;
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;

        public final TextView tv_title;
        public final TextView tv_data;

        private ViewHolder(RelativeLayout rootView,
                           TextView tv_title,
                           TextView tv_data) {
            this.rootView = rootView;
            this.tv_title = tv_title;
            this.tv_data = tv_data;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            TextView tv_data = (TextView) rootView.findViewById(R.id.tv_data);

            return new ViewHolder(rootView,
                    tv_title,
                    tv_data);
        }
    }
}