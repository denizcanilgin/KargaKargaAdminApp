package com.example.kkadmin2.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kkadmin2.R;
import com.parse.ParseObject;
import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter extends ArrayAdapter<ParseObject> {


    LayoutInflater inflater;
    Context context;
    ArrayList<ParseObject> list_applications;

    public ApplicationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ParseObject> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list_applications = objects;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflater.inflate(R.layout.listview_item_applications,null);

        TextView tv_userId = convertView.findViewById(R.id.tv_userId);
        TextView tv_scenario = convertView.findViewById(R.id.tv_scenario);

        String str_userId = "";
        String str_scenario = "";

        if(list_applications.get(position).get("userId") != null)
            str_userId = list_applications.get(position).get("userId").toString();
        else
            str_userId = list_applications.get(position).get("user_id").toString();

        if(list_applications.get(position).get("scenario") != null)
            str_scenario = list_applications.get(position).get("scenario").toString();
        else
            str_scenario = list_applications.get(position).get("select_application_object_id").toString();




        tv_userId.setText(str_userId);
        tv_scenario.setText(str_scenario);


        return convertView;
    }
}
