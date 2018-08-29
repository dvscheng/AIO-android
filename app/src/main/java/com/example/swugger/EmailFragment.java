package com.example.swugger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class EmailFragment extends Fragment {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<JavaMailPackage> mJavaMailPackageList;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EmailFragment mTargetFragment;

    private AuthenticationPreferences mAuthPrefs;
    private AccountManager mAccountManager;

    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final String SCOPE = "https://mail.google.com";
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;


    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    public EmailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_email, container, false);


        mContext = getContext();

        mRecyclerView = rootView.findViewById(R.id.recyclerView_email_fragment);
        mJavaMailPackageList = new ArrayList<>();
        mRecyclerViewAdapter = new EmailRecyclerViewAdapter(mContext, mJavaMailPackageList, getFragmentManager());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mTargetFragment = this;

        /*mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());*/

        // email stuff
        mAccountManager = AccountManager.get(mContext);
        mAuthPrefs = new AuthenticationPreferences(mContext);

        if (mAuthPrefs.getUsername() != null || mAuthPrefs.getToken() != null) {
            // to make sure we get a valid token we can use
            invalidateToken();
            requestToken();
            // TODO: make a method that calls these two and implies that retrieveMessages is called
        } else {
            chooseAccount();
        }

        return rootView;
    }

    private void retrieveMessages() {
        String[] params = { mAuthPrefs.getUsername(), mAuthPrefs.getToken() };
        new GetMessages().execute(params);
    }

    private void chooseAccount() {
        Intent intent = AccountManager.newChooseAccountIntent(null,
                null,
                new String[]{ GOOGLE_ACCOUNT_TYPE },
                false,
                null,
                null,
                null,
                null);
        startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
    }

    private void requestToken() {
        Account userAccount = null;
        String username = mAuthPrefs.getUsername();
        for (Account account : mAccountManager.getAccountsByType(GOOGLE_ACCOUNT_TYPE)) {
            if (account.name.equals(username)) {
                userAccount = account;
                break;
            }
        }

        mAccountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, getActivity(),
                new GetTokenCallback(), null);
    }

    /** Should be called before requestToken when refreshing. */
    private void invalidateToken() {
        // TODO: can change this to mAccountManager
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, mAuthPrefs.getToken());

        mAuthPrefs.setToken(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EmailFragment.REQUEST_ACCOUNT_PICKER) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mAuthPrefs.setUsername(accountName);

                // TODO: more efficient way to check validity of tokens?
                // invalidate the token to make sure we have a token that is sure to work
                invalidateToken();
                requestToken();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // resend
        }
    }

    private class GetTokenCallback implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, REQUEST_AUTHORIZATION);
                } else {
                    String token = bundle
                            .getString(AccountManager.KEY_AUTHTOKEN);
                    mAuthPrefs.setToken(token);

                    retrieveMessages();
                }
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class GetMessages extends AsyncTask<String, Void, List<JavaMailPackage>> {

        @Override
        protected List<JavaMailPackage> doInBackground(String... strings) {
            Properties props = new Properties();
            //IMAPS protocol
            props.setProperty("mail.store.protocol", "imap");
            //Set host address
            props.setProperty("mail.imap.host", "imap.gmail.com");
            //Set specified port
            props.setProperty("mail.imap.port", "993");
            //Using SSL
            props.setProperty("mail.imap.ssl.enable", "true");
            // used for Gmail
            props.setProperty("mail.imap.auth.mechanisms", "XOAUTH2");
            //props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            //props.setProperty("mail.imaps.socketFactory.fallback", "false");
            //Setting IMAP session
            Session imapSession = Session.getInstance(props);
            List<JavaMailPackage> javaMailPackages = new ArrayList<>();

            try {
                Store store = imapSession.getStore("imap");

                String username = strings[0];
                String token = strings[1];
                //Connect to server by sending username and password.
                //Example mailServer = imap.gmail.com, username = abc, password = abc
                store.connect("imap.gmail.com", username, token);
                //Get all mails in Inbox Folder
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                //Return result to array of message

                javax.mail.Message[] messages = inbox.getMessages();
                for (javax.mail.Message msg : messages) {
                    try {
                        JavaMailPackage newPackage = new JavaMailPackage(msg, msg.getFrom()[0].toString(), msg.getSubject(), getText(msg));
                        javaMailPackages.add(newPackage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                /*for (Message msg : inbox.getMessages()) {
                    str += getText(msg);
                }
                return str;*/


            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } /*catch (IOException e) {
                e.printStackTrace();
            }*/

            return javaMailPackages;
        }

        @Override
        protected void onPostExecute(List<JavaMailPackage> result) {
            mJavaMailPackageList.clear();
            mJavaMailPackageList.addAll(result);

            mRecyclerViewAdapter.notifyDataSetChanged();

            /*WebView webView = findViewById(R.id.web_view);
            webView.loadDataWithBaseURL("email://", result, "text/html", "utf-8", null);*/
        }

        private boolean textIsHtml = true;

        /**
         * Return the primary text content of the message.
         */
        private String getText(Part p) throws
                MessagingException, IOException {
            if (p.isMimeType("text/*")) {
                String s = (String)p.getContent();
                textIsHtml = p.isMimeType("text/html");
                return s;
            }

            if (p.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                Multipart mp = (Multipart)p.getContent();
                String text = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        if (text == null)
                            text = getText(bp);
                        continue;
                    } else if (bp.isMimeType("text/html")) {
                        String s = getText(bp);
                        if (s != null)
                            return s;
                    } else {
                        return getText(bp);
                    }
                }
                return text;
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null)
                        return s;
                }
            }

            return null;
        }
    }
}