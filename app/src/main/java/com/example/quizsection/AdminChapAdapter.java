package com.example.quizsection;

import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizSubAdmin.selected_sub_index;

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

public class AdminChapAdapter extends RecyclerView.Adapter<AdminChapAdapter.ViewHolder> {

    private List<String> chap_list;

    public AdminChapAdapter(List<String> chap_list) {
        this.chap_list = chap_list;
    }

    @NonNull
    @Override
    public AdminChapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_layout, parent, false);

        return new AdminChapAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminChapAdapter.ViewHolder holder, int position) {

        String chapter = chap_list.get(position);

        holder.setData(chapter, position, this);

    }

    @Override
    public int getItemCount() {
        return chap_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chapName;
        private ImageView delete;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editChapName;
        private Button updateChap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chapName = itemView.findViewById(R.id.itemName);
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

            tv_editChapName = editDialog.findViewById(R.id.edit_item);
            updateChap = editDialog.findViewById(R.id.edit_btn);

        }

        private void setData(String subject, int position, AdminChapAdapter adapter) {

            chapName.setText(subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuizChapAdmin.selected_chap_index = position + 1;
                    Intent intent = new Intent(itemView.getContext(), QuizQuesAdmin.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    tv_editChapName.setText(chap_list.get(position));
                    editDialog.show();

                    return false;
                }
            });

            updateChap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(tv_editChapName.getText().toString().isEmpty())
                    {
                        tv_editChapName.setError("Enter chapter name");
                        return;
                    }

                    updateChapter(tv_editChapName.getText().toString(), position, itemView.getContext(), adapter);


                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Chapter")
                            .setMessage("Are you sure you want to delete this chapter?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteChapter(position, itemView.getContext(), adapter);
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

        private void deleteChapter(final int id, Context context, AdminChapAdapter adapter)
        {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> chapDoc = new ArrayMap<>();

            int index = 1;

            for (int i=0; i < chap_list.size(); i++)
            {
                if (i != id)
                {
                    chapDoc.put("CHAP" + String.valueOf(index), chap_list.get(i));
                    index++;
                }
            }

            chapDoc.put("NUMCHAP", index - 1);

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .collection("SUB" + selected_sub_index).document("CHAPTERS")
                    .set(chapDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Chapter deleted successfully", Toast.LENGTH_SHORT).show();
                            QuizChapAdmin.chapList.remove(id);

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

        private void updateChapter(String chapNewName, int pos, Context context, AdminChapAdapter adapter)
        {
            editDialog.dismiss();

            loadingDialog.show();

            Map<String,Object> chapData = new ArrayMap<>();
            chapData.put("CHAP" + (pos + 1), chapNewName);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .collection("SUB" + selected_sub_index).document("CHAPTERS")
                    .update(chapData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Chapter name changed successfully", Toast.LENGTH_SHORT);
                            chap_list.set(pos, chapNewName);

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
