package com.example.quizsection;

import static android.content.ContentValues.TAG;
import static com.example.quizsection.QuizChapAdmin.selected_chap_index;
import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizQuesAdmin.quesList;
import static com.example.quizsection.QuizQuesAdmin.selected_ques_index;
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

public class AdminQuesAdapter extends RecyclerView.Adapter<AdminQuesAdapter.ViewHolder> {

    private List<String> quesList;

    public AdminQuesAdapter(List<String> quesList) {
        this.quesList = quesList;
    }

    @NonNull
    @Override
    public AdminQuesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_layout, parent, false);

        return new AdminQuesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminQuesAdapter.ViewHolder holder, int position) {

        String title = quesList.get(position);

        holder.setData(title, position, this);

    }

    @Override
    public int getItemCount() {
        return quesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView quesName;
        private ImageView delete;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editQuesName;
        private Button updateQues;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quesName = itemView.findViewById(R.id.itemName);
            delete = itemView.findViewById(R.id.deleteItem);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        private void setData(String question, int position, AdminQuesAdapter adapter) {

            quesName.setText("QUESTION " + String.valueOf(position + 1));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_ques_index = position + 1;
                    Intent intent = new Intent(itemView.getContext(), QuizActivityAdmin.class);
                    intent.putExtra("ACTION", "EDIT");
                    itemView.getContext().startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                                .setTitle("Delete Question")
                            .setMessage("Are you sure you want to delete this question?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteQuestion(position, itemView.getContext(), adapter);
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

        private void deleteQuestion(final int id, Context context, AdminQuesAdapter adapter)
        {
            loadingDialog.show();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .collection("SUB" + selected_sub_index).document("CHAPTERS")
                    .collection("CHAP" + selected_chap_index)
                    .document("QUESTION" + (id+1))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Question successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting question", e);
                        }
                    });

            Map<String,Object> quesDoc = new ArrayMap<>();

            int index = 1;
            for (int i=0; i < quesList.size(); i++)
            {
                if (i != id)
                {
                    quesDoc.put("QUES" + String.valueOf(index), quesList.get(i));
                    index++;
                }
            }

            quesDoc.put("NUMQUES", index - 1);

            firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                    .collection("SUB" + selected_sub_index).document("Questions")
                    .set(quesDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Question deleted successfully", Toast.LENGTH_SHORT).show();
                            QuizQuesAdmin.quesList.remove(id);

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

    }

}
