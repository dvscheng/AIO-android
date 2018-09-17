package com.example.AIO;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


public class EmailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class EmailViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mSubjectTextView;
        public TextView mDateTextView;

        public EmailViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.textView_email_title);
            mSubjectTextView = itemView.findViewById(R.id.textView_email_subject);
            mDateTextView = itemView.findViewById(R.id.textView_email_date);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressBar_email_item_footer);
        }
    }

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public static final String MESSAGE_STRING_CONTENT = "MESSAGE_STRING_CONTENT";

    private Context mContext;
    private List<JavaMailPackage> mEmailList;
    private FragmentManager mFragMan;
    private boolean isLoadingAdded = false;
    private int loadingFooterIndex = -1;


    // Provide a suitable constructor (depends on the kind of dataset)
    public EmailRecyclerViewAdapter(Context context, List<JavaMailPackage> emails, FragmentManager fragmentMan) {
        mContext = context;
        mEmailList = emails;
        mFragMan = fragmentMan;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;
        if (i == LOADING) {
            View loadingFooter = inflater.inflate(R.layout.item_email_loading_footer, viewGroup, false);

            viewHolder = new LoadingViewHolder(loadingFooter);

            isLoadingAdded = false;
            loadingFooterIndex = i;
        } else {
            View emailView = inflater.inflate(R.layout.item_email, viewGroup, false);

            viewHolder = new EmailViewHolder(emailView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final JavaMailPackage javaMailPackage = mEmailList.get(i);

        if (viewHolder instanceof EmailViewHolder && i != loadingFooterIndex) {
            EmailViewHolder emailViewHolder = (EmailViewHolder) viewHolder;
            emailViewHolder.mTitleTextView.setText(javaMailPackage.getFrom().getPersonal());
            emailViewHolder.mSubjectTextView.setText(javaMailPackage.getSubject());
            emailViewHolder.mDateTextView.setText(javaMailPackage.getReadableDate(false));

            // set title and subject textviews
            emailViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
        } else {
            // TODO: turn on the progressbar?
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mEmailList.size()-1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mEmailList.size();
    }

    public void add(JavaMailPackage javaMailPackage) {
        mEmailList.add(javaMailPackage);
    }

    public void addAll(List<JavaMailPackage> packageList) {
        for (JavaMailPackage javaMailPackage : packageList) {
            add(javaMailPackage);
        }
    }

    public void remove(JavaMailPackage javaMailPackage) {
        int position = mEmailList.indexOf(javaMailPackage);
        if (position >= 0) {
            mEmailList.remove(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        loadingFooterIndex = -1;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new JavaMailPackage(null, null, null, null, "loading footer"));
        notifyItemInserted(mEmailList.size() - 1);
    }

    public void removeLoadingFooter() {
        int position = loadingFooterIndex;
        if (position >= 0) {
            isLoadingAdded = false;
            loadingFooterIndex = -1;

            JavaMailPackage item = getItem(position);

            if (item != null) {
                mEmailList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public JavaMailPackage getItem(int position) {
        return mEmailList.get(position);
    }
}
