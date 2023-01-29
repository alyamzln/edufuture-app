package com.example.quizsection;

import static android.content.ContentValues.TAG;
import static com.example.quizsection.QuizChapAdmin.selected_chap_index;
import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizSubAdmin.selected_sub_index;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
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

public class AdminSubAdapter extends RecyclerView.Adapter<AdminSubAdapter.ViewHolder> {

    private List<String> subj_list;

    public AdminSubAdapter(List<String> subj_list) {
        this.subj_list = subj_list;
    }

    @NonNull
    @Override
    public AdminSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_layout, parent, false);

        return new AdminSubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String subject = subj_list.get(position);

        holder.setData(subject, position, this);

    }

    @Override
    public int getItemCount() {
        return subj_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subName;
        private ImageView delete;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editSubName;
        private Button updateSub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subName = itemView.findViewById(R.id.itemName);
            delete = itemView.findViewById(R.id.deleteItem);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_item_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tv_editSubName = editDialog.findViewById(R.id.edit_item);
            updateSub = editDialog.findViewById(R.id.edit_btn);

        }

        private void setData(String subject, int position, AdminSubAdapter adapter) {
            subName.setText(subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuizSubAdmin.selected_sub_index = position + 1;
                    Intent intent = new Intent(itemView.getContext(), QuizChapAdmin.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    tv_editSubName.setText(subj_list.get(position));
                    editDialog.show();

                    return false;
                }
            });

            updateSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(tv_editSubName.getText().toString().isEmpty())
                    {
                        tv_editSubName.setError("Enter level name");
                        return;
                    }

                    updateSubject(tv_editSubName.getText().toString(), position, itemView.getContext(), adapter);


                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Subject")
                            .setMessage("Are you sure you want to delete this subject?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteSubject(position, itemView.getContext(), adapter);
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

        private void deleteSubject(final int id, Context context, AdminSubAdapter adapter)
        {
            loadingDialog.show();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> subDoc = new ArrayMap<>();

            int index = 1;

            for (int i=0; i < subj_list.size(); i++)
            {
                if (i != id)
                {
                    subDoc.put("SUB" + String.valueOf(index), subj_list.get(i));
                    index++;
                }
            }

            subDoc.put("NUMSUB", index - 1);

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .set(subDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                            QuizSubAdmin.subjList.remove(id);

                            subDoc.put("NAME", "FORM " + (id + 1));

                            firestore.collection("QUIZ1").document("LEV" + selected_lev_index).set(subDoc);

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

        private void updateSubject(String subNewName, int pos, Context context, AdminSubAdapter adapter)
        {
            editDialog.dismiss();

            loadingDialog.show();

            Map<String,Object> subData = new ArrayMap<>();
                subData.put("SUB" + (pos + 1), subNewName);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .update(subData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Subject name changed successfully", Toast.LENGTH_SHORT);
                            subj_list.set(pos, subNewName);

                            adapter.notifyDataSetChanged();

                            loadingDialog.dismiss();

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
