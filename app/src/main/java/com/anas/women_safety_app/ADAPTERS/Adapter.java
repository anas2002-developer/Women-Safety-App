package com.anas.women_safety_app.ADAPTERS;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anas.women_safety_app.MODELS.Model;
import com.anas.women_safety_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.LL_Contact.getContext());
                alertDialog.setTitle("Delete Contact");
                alertDialog.setMessage("Are you sure ?");
                alertDialog.setCancelable(true);

                alertDialog.setPositiveButton("Yes", (dialog, which) -> {

                    FirebaseDatabase.getInstance().getReference().child("SURAKSHAK").child(FirebaseAuth.getInstance().getUid()).child("CONTACTS").child(getRef(position).getKey()).removeValue();
                    Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show();
                });

                alertDialog.setNegativeButton("No", (dialog, which) -> {

                });
                alertDialog.show();

            }
        });

        holder.LL_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.contact_dialog);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_bg));
                }
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EditText eName = dialog.findViewById(R.id.eName);
                EditText ePhone = dialog.findViewById(R.id.ePhone);
                EditText eType = dialog.findViewById(R.id.eType);

                eName.setText(model.getName());
                ePhone.setText(model.getPhone());
                eType.setText(model.getType());

                Button btnUpdate = dialog.findViewById(R.id.btnAdd);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                btnUpdate.setOnClickListener(v1 -> {

                    String Name = eName.getText().toString();
                    String Phone = ePhone.getText().toString();
                    String Type = eType.getText().toString();

                    if (Name.equals("")){
                        Toast.makeText(context, "Blank Field!", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Map<String,Object> map = new HashMap<>();
                        map.put("name",Name);
                        map.put("phone",Phone);
                        map.put("type",Type);

                        FirebaseDatabase.getInstance().getReference().child("SURAKSHAK").child(FirebaseAuth.getInstance().getUid()).child("CONTACTS").child(getRef(position).getKey())
                                .updateChildren(map);

                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

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
        ImageView btnCall, btnDelete;
        LinearLayout LL_Contact;
        public VIEWHOLDER(@NonNull View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            txtPhone=itemView.findViewById(R.id.txtPhone);
            txtType=itemView.findViewById(R.id.txtType);
            btnCall=itemView.findViewById(R.id.btnCall);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            LL_Contact=itemView.findViewById(R.id.LL_Contact);
        }
    }
}
