package com.example.AIO;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


public class EmailRecyclerViewAdapter extends RecyclerView.Adapter<EmailRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mSubjectTextView;
        public TextView mDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.textView_email_title);
            mSubjectTextView = itemView.findViewById(R.id.textView_email_subject);
            mDateTextView = itemView.findViewById(R.id.textView_email_date);
        }
    }

    public static final String MESSAGE_STRING_CONTENT = "MESSAGE_STRING_CONTENT";

    private Context mContext;
    private List<JavaMailPackage> mEmailList;
    private FragmentManager mFragMan;


    // Provide a suitable constructor (depends on the kind of dataset)
    public EmailRecyclerViewAdapter(Context context, List<JavaMailPackage> emails, FragmentManager fragmentMan) {
        mContext = context;
        mEmailList = emails;
        mFragMan = fragmentMan;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View emailView = inflater.inflate(R.layout.item_email, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(emailView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final JavaMailPackage javaMailPackage = mEmailList.get(i);

        viewHolder.mTitleTextView.setText(javaMailPackage.getFrom().getPersonal());
        viewHolder.mSubjectTextView.setText(javaMailPackage.getSubject());
        viewHolder.mDateTextView.setText(javaMailPackage.getReadableDate(false));

        // set title and subject textviews
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailShowDialogFragment emailDialog = new EmailShowDialogFragment();

                String content = javaMailPackage.getContent();
                Bundle args = new Bundle();
                args.putString(MESSAGE_STRING_CONTENT, content);
                emailDialog.setArguments(args);

                emailDialog.show(mFragMan, "show email");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEmailList.size();
    }
}
