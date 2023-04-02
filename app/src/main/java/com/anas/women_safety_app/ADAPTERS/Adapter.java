package com.anas.women_safety_app.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.women_safety_app.MODELS.Model;
import com.anas.women_safety_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class Adapter extends FirebaseRecyclerAdapter<Model, Adapter.VIEWHOLDER> {


    Context context;
    public Adapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VIEWHOLDER holder, int position, @NonNull Model model) {
        holder.txtName.setText(model.getName());
        holder.txtPhone.setText(model.getPhone());
        holder.txtType.setText(model.getType());

        String Phone = model.getPhone();

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                context.startActivity(i);

            }
        });


    }

    @NonNull
    @Override
    public VIEWHOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row,parent,false);
        return new VIEWHOLDER(view);
    }

    public class VIEWHOLDER extends RecyclerView.ViewHolder {

        TextView txtName,txtPhone,txtType;
        ImageView btnCall;
        public VIEWHOLDER(@NonNull View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            txtPhone=itemView.findViewById(R.id.txtPhone);
            txtType=itemView.findViewById(R.id.txtType);
            btnCall=itemView.findViewById(R.id.btnCall);
        }
    }
}
