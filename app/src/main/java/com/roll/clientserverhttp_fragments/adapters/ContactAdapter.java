package com.roll.clientserverhttp_fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.roll.clientserverhttp_fragments.R;
import com.roll.clientserverhttp_fragments.entities.User;

import java.util.ArrayList;

/**
 * Created by RDL on 23.02.2017.
 */

public class ContactAdapter extends BaseAdapter {
    private ArrayList<User> users = new ArrayList<>();
    private Context context;
    private ViewClickListener listener;

    public ContactAdapter(Context context, ArrayList<User> users, ViewClickListener listener) {

        this.context = context;
        this.users = users;
        this.listener = listener;
    }


    public interface ViewClickListener {
        void btnViewClick(View view, int position);
    }

    public void addUser(User user) {
        users.add(0, user);
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<User> contacts) {
        users = contacts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.contact_row, parent, false);
        }
        User user = users.get(position);
        TextView nameTxt = (TextView) view.findViewById(R.id.txt_name);
        TextView emailTxt = (TextView) view.findViewById(R.id.txt_email);
        final Button viewBtn = (Button) view.findViewById(R.id.btn_view);

        nameTxt.setText(user.getFullName());
        emailTxt.setText(user.getEmail());
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.btnViewClick(viewBtn, position);
                }
            }
        });

        return view;
    }
}
