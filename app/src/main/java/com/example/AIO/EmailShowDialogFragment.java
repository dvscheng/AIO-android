package com.example.AIO;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.DialogFragment;


public class EmailShowDialogFragment extends DialogFragment {

    private WebView mWebView;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // the content
        View root = inflater.inflate(R.layout.dialog_show_email_fragment, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.FullscreenDialogFragmentTheme);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));       // mandatory for fullscreen... why?
        // dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mWebView = root.findViewById(R.id.webView_show_email_dialog_fragment);
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        String result = getArguments().getString(EmailRecyclerViewAdapter.MESSAGE_STRING_CONTENT);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        // baseUrl used to be "email://"
        mWebView.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);


        return dialog;
        //return super.onCreateDialog(savedInstanceState);
    }

    public static String changedHeaderHtml(String htmlText) {

        String head = "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" /></head>";

        String closedTag = "</body></html>";
        String changeFontHtml = head + htmlText + closedTag;
        return changeFontHtml;
    }
}
