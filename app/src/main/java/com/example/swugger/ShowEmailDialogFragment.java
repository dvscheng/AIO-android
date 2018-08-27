package com.example.swugger;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.fragment.app.DialogFragment;


public class ShowEmailDialogFragment extends DialogFragment {

    private WebView mWebView;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // the content
        View root = inflater.inflate(R.layout.dialog_email_fragment, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.FullscreenDialogFragmentTheme);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));       // mandatory for fullscreen... why?
        // dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        String result = getArguments().getString("MESSAGE_STRING_CONTENT");
        mWebView = root.findViewById(R.id.web_view);
        mWebView.loadDataWithBaseURL("email://", result, "text/html", "utf-8", null);

        return dialog;
        //return super.onCreateDialog(savedInstanceState);
    }
}
