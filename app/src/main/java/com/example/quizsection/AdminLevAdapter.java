package com.example.quizsection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.ArrayMap;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class AdminLevAdapter extends RecyclerView.Adapter<AdminLevAdapter.ViewHolder> {

    private List<LevelModel> lev_list;

    public AdminLevAdapter(List<LevelModel> lev_list) {
        this.lev_list = lev_list;
    }

    @NonNull
    @Override
    public AdminLevAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminLevAdapter.ViewHolder holder, int position) {

        String title = lev_list.get(position).getName();

        holder.setData(title, position, this);

    }

    @Override
    public int getItemCount() {
        return lev_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView levName;
        private ImageView delete;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editLevName;
        private Button updateLev;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            levName = itemView.findViewById(R.id.itemName);
            delete = itemView.findViewById(R.id.deleteItem);

            loadingDialog = new Dialog(itemView.getContext()) ;
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext()) ;
            editDialog.setContentView(R.layout.edit_item_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            tv_editLevName = editDialog.findViewById(R.id.edit_item);
            updateLev = editDialog.findViewById(R.id.edit_btn);
        }

        private void setData(String title, int position, AdminLevAdapter adapter)
        {
            levName.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuizLevAdmin.selected_lev_index = position + 1;
                    Intent intent = new Intent(itemView.getContext(), QuizSubAdmin.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    tv_editLevName.setText(lev_list.get(position).getName());
                    editDialog.show();

                    return false;
                }
            });

            updateLev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(tv_editLevName.getText().toString().isEmpty())
                    {
                        tv_editLevName.setError("Enter level name");
                        return;
                    }

                    updateLevel(tv_editLevName.getText().toString(), position, itemView.getContext(), adapter);


                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Level")
                            .setMessage("Are you sure you want to delete this level?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteLevel(position, itemView.getContext(), adapter);
                                }
                            }).setNegativeButton("Cancel",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0 ,50 ,0);
                    alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setLayoutParams(params);

                }
            });
        }

        private void deleteLevel(final int id, Context context, AdminLevAdapter adapter)
        {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> levDoc = new ArrayMap<>();

            int index = 1;

            for (int i=0; i < lev_list.size(); i++)
            {
                if (i != id)
                {
                    levDoc.put("LEV" + String.valueOf(index), lev_list.get(i).getName());
                    index++;
                }
            }

            levDoc.put("Count", index - 1);

            firestore.collection("QUIZ1").document("Level")
                    .set(levDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Level deleted successfully", Toast.LENGTH_SHORT).show();
                            QuizLevAdmin.levList.remove(id);
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();

                        }
                    });
        }

        private void updateLevel(String levNewName, int pos, Context context, AdminLevAdapter adapter)
        {
            editDialog.dismiss();

            loadingDialog.show();

            Map<String,Object> levData = new ArrayMap<>();
            levData.put("NAME", levNewName);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ1").document("LEV" + (pos+1))
                    .update(levData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Map<String,Object> levDoc = new ArrayMap<>();
                            levDoc.put("LEV" + String.valueOf(pos + 1), levNewName);

                            firestore.collection("QUIZ1").document("Level")
                                    .update(levDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(context, "Level name changed successfully", Toast.LENGTH_SHORT);
                                            QuizLevAdmin.levList.get(pos).setName(levNewName);
                                            adapter.notifyDataSetChanged();

                                            loadingDialog.dismiss();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();

                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();

                        }
                    });
        }
    }
}



