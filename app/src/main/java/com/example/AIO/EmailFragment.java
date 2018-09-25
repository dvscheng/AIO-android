package com.example.AIO;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import javax.mail.internet.InternetAddress;

public class EmailFragment extends Fragment {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<JavaMailPackage> mJavaMailPackageList;
    private EmailRecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mRecyclerViewLayoutManager;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmailFragment mTargetFragment;

    private AuthenticationPreferences mAuthPrefs;
    private AccountManager mAccountManager;

    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 0;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 4 more pages will load.
    private int TOTAL_PAGES = 5;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;
    //
    private boolean isPulledDown = false;
    // indicates the number of messages to retrieve, 0-indexed, meaning 11 is 12 messages to retrieve
    public static final int NUM_MESSAGES_TO_RETRIEVE = 11;

    public int loadingState = LOADING_INITIAL;
    private static final int LOADING_INITIAL = 0;
    private static final int LOADING_PULLED = 1;
    private static final int LOADING_SCROLLED = 2;
    private static final int LOADING_FINISHED = 3;


    /** Javamail API */
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

        mProgressBar = rootView.findViewById(R.id.progressBar_email_item_footer);

        mRecyclerView = rootView.findViewById(R.id.recyclerView_email_fragment);
        mJavaMailPackageList = new ArrayList<>();
        mRecyclerViewAdapter = new EmailRecyclerViewAdapter(mContext, mJavaMailPackageList, getFragmentManager());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mRecyclerViewLayoutManager) {
            @Override
            protected void loadMoreItems() {
                setLoadingState(LOADING_SCROLLED);
                refreshEmailRecyclerView();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // Refresh the recycler view when pulling up on recyclerview
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout_email_fragment);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentLite, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    setLoadingState(LOADING_PULLED);
                    refreshEmailRecyclerView();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mTargetFragment = this;

        /*mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());*/

        // email stuff
        mAccountManager = AccountManager.get(mContext);
        mAuthPrefs = new AuthenticationPreferences(mContext);

        setLoadingState(LOADING_INITIAL);
        refreshEmailRecyclerView();

        return rootView;
    }

    private void setLoadingState(int state) {
        loadingState = state;
        isLoading = true;
        switch (state) {
            case LOADING_INITIAL:
            case LOADING_PULLED:
                isPulledDown = true;
                mSwipeRefreshLayout.setRefreshing(true);
                break;

            case LOADING_SCROLLED:
                currentPage += 1; //Increment page index to load the next one
                mRecyclerViewAdapter.addLoadingFooter();
                break;

            case LOADING_FINISHED:
                isLoading = false;
                isPulledDown = false;
                break;
        }
    }
    /** Prompts to select email account if none has already been selected,
     * or receives messages for the currently selected email. */
    private void refreshEmailRecyclerView() {
        if (mAuthPrefs.getUsername() != null && mAuthPrefs.getToken() != null) {
            // to make sure we get a valid token we can use
            refreshTokenAndReceiveMessages();
        } else {
            chooseAccount();
        }
    }
    /** Should only be called when account has already been selected.
     * Refresh the current token and then receive messages. */
    private void refreshTokenAndReceiveMessages() {
        invalidateToken();
        requestToken();
    }
    /** Do not call by itself, call refreshTokenAndReceiveMessages() */
    private void retrieveMessages() {
        String[] params = { mAuthPrefs.getUsername(), mAuthPrefs.getToken() };
        new GetMessages().execute(params);
    }
    /** Do not call by itself, call refreshTokenAndReceiveMessages()
     * Also gets messages */
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
    /** Do not call by itself, call refreshTokenAndReceiveMessages()
     * Should be called before requestToken when refreshing. */
    private void invalidateToken() {
        mAccountManager.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, mAuthPrefs.getToken());

        mAuthPrefs.setToken(null);
    }

    /** Requests access to an email account. */
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EmailFragment.REQUEST_ACCOUNT_PICKER) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mAuthPrefs.setUsername(accountName);

                // TODO: more efficient way to check validity of tokens?
                // invalidate the token to make sure we have a token that is sure to work
                refreshTokenAndReceiveMessages();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            isPulledDown = false;
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(mContext, "Choose an email to start loading your inbox!", Toast.LENGTH_SHORT).show();
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

                // if the user pulled-down the recyclerview, then just retrieve the same # of
                // currently displayed emails. otherwise, retrieve new emails
                javax.mail.Message[] messages;
                int totalMessagesInInbox = inbox.getMessageCount();
                int currentNumMessages = mRecyclerViewAdapter.getCount(false); // getItemCount() includes the loading header
                int targetNumMessages = totalMessagesInInbox - (currentNumMessages + NUM_MESSAGES_TO_RETRIEVE);

                if (targetNumMessages < 1) {
                    TOTAL_PAGES = currentPage;
                    isLastPage = true;
                    targetNumMessages = 1;
                }
                // don't take more than the total message count of the inbox
                switch (loadingState) {
                    case LOADING_INITIAL:
                    case LOADING_PULLED:
                        // TODO: make pulling down only fetch NEW, UNREAD messages
                        messages = inbox.getMessages(totalMessagesInInbox - NUM_MESSAGES_TO_RETRIEVE, totalMessagesInInbox);
                        break;

                    case LOADING_SCROLLED:
                        messages = inbox.getMessages(targetNumMessages, totalMessagesInInbox - currentNumMessages);
                        break;

                    default:
                        Log.d("loading state error", "loadingState was not in range [0, 3]");
                        messages = new javax.mail.Message[0];
                }


                for (int i = messages.length-1; i >= 0; i--) {
                    try {
                        JavaMailPackage newPackage = new JavaMailPackage(messages[i],
                                (InternetAddress) messages[i].getFrom()[0], messages[i].getSubject(), messages[i].getReceivedDate(),
                                getText(messages[i]));
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
            switch (loadingState) {
                case LOADING_INITIAL:
                case LOADING_PULLED:
                    mRecyclerViewAdapter.clear();

                    // Set the layoutlistener to start listening for notifyDataSetChanged to finish
                    mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            setLoadingState(LOADING_FINISHED);
                            // Remove the listener to make sure it isn't called again
                            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });

                    mRecyclerViewAdapter.addAll(result);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;

                case LOADING_SCROLLED:
                    mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mRecyclerViewAdapter.removeLoadingFooter();
                            setLoadingState(LOADING_FINISHED);
                            // Remove the listener to make sure it isn't called again
                            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });

                    int position = mRecyclerViewAdapter.getItemCount();
                    mRecyclerViewAdapter.addAll(result);
                    mRecyclerViewAdapter.notifyItemRangeInserted(position, result.size());
                    break;
            }

            /*if (isPulledDown) {
                mRecyclerViewAdapter.clear();

                // Set the layoutlistener to start listening for notifyDataSetChanged to finish
                mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        // Remove the listener to make sure it isn't called again
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
            mRecyclerViewAdapter.addAll(result);

            mProgressBar.setVisibility(View.GONE);
*/
            /*// handle whether or not to display the loading footer
            if (currentPage < TOTAL_PAGES) {
                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerViewAdapter.addLoadingFooter();
            } else {
                mRecyclerViewAdapter.removeLoadingFooter();
                isLastPage = true;
            }*/

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