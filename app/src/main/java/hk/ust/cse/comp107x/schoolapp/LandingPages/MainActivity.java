package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import hk.ust.cse.comp107x.schoolapp.tool.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.Views.RegistrationActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.Views.ViewPageActivity;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_GOOGLE_LOGIN = 1;
    private boolean mGoogleIntentInProgress;
    private boolean mGoogleLoginClicked;
    private ConnectionResult mGoogleConnectionResult;
    private ProgressDialog mAuthProgressDialog;

    private static final int STATE_SIGNED_IN = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private int mSignInProgress;

    private PendingIntent mSignInIntent;
    private int mSignInError;

    private static final int RC_SIGN_IN = 0;

    private Button mGoogleLogin;
    private Button mEmailLogin;
    private Button mFacebookLogin;

    private ProgressDialog mProgress;

    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "signin1";
    private Boolean exit = false;

    SharedPreferences preferences;

    // facebook dependencies
    CallbackManager callbackManager;
    Firebase ref;

    private static final String[] LOGIN_PERMISSIONS_FB = new String[]{"email"};

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
//            Toast.makeText(LandingPage.this, R.string.gp_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionSuspended(int i) {
//            Toast.makeText(LandingPage.this, R.string.gp_disconnected, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_SIGNED_IN;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        ref = new Firebase(Constants.FIREBASE_URL);

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        mEmailLogin = (Button) findViewById(R.id.email_button);

        mGoogleApiClient = buildApiClient();
        mGoogleLogin = (Button) findViewById(R.id.sign_in_button);

        mFacebookLogin = (Button) findViewById(R.id.signUpButton);

        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveSignInError();
            }
        });

        // Email and password sign up

        mEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });

        // Facebook Login

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        mProgress = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true, false);
                                        if (response != null) {
                                            try {

                                                if (mProgress != null)
                                                    mProgress.dismiss();

                                                AccessToken token = loginResult.getAccessToken();

                                                onFacebookAccessTokenChange(token);

                                            } catch (Exception ex) {

                                                if (mProgress != null)
                                                    mProgress.dismiss();

                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name,id,email,gender,picture,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                        if (mProgress != null)
                            mProgress.dismiss();
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        if (mProgress != null)
                            mProgress.dismiss();

                        Utils.showLongMessage(Constants.CHECK_CONNECTION, MainActivity.this);
                    }
                });

        mFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("user_friends");
                permissions.add("email");
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, permissions);


            }
        });
    }

    public void onFacebookAccessTokenChange(AccessToken loginToken) {

        preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

        final SharedPreferences.Editor editor = preferences.edit();

        final Firebase userRef = ref.child("users");

        if (loginToken != null) {

            ref.authWithOAuthToken("facebook", loginToken.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    final UserDetails details = new UserDetails();
                    details.setName(authData.getProviderData().get("displayName").toString());
                    details.setId(authData.getUid());

                    String emailAvailable = authData.getProviderData().get("email").toString();
                    details.email = (emailAvailable == null) ? "email@email.com" : emailAvailable;
                    ;
                    details.setAccessToken(authData.getToken());

                    userRef.child(authData.getUid()).setValue(details);

                    if (authData != null) {

                        String uid = authData.getUid();

                        editor.putString(Constants.USER_TOKEN, uid);
                        editor.putString(Constants.USER_NAME, authData.getProviderData().get("displayName").toString());
                        editor.putString(Constants.USER_EMAIL, emailAvailable);

                        editor.apply();

                        alertDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
//                    userDetails.put("profile", picture.get))
                    //We are already logged in and we can go to Landing Page
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    System.out.println("An error has occurred" + firebaseError);

                }
            });

        } else {
            ref.unauth();
        }

    }

    // Google Login authentication
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;

                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);

            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            showDialog(DIALOG_PLAY_SERVICES_ERROR);
        }
    }

    private void loginAndGetToken() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {

                    String scope = "oauth2:profile email";
                    token = GoogleAuthUtil.getToken(MainActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);

                } catch (IOException transientEx) {

                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();

                } catch (UserRecoverableAuthException e) {

                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {

                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);

                    }

                } catch (GoogleAuthException authEx) {

                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();

                }

                return token;
            }

            @Override
            protected void onPostExecute(final String token) {

                preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

                final SharedPreferences.Editor editor = preferences.edit();


                final Firebase userRef = ref.child("users");


                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                    final UserDetails details = new UserDetails();
                    details.setName(currentPerson.getDisplayName());
                    details.email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    details.setAccessToken(token);

                    userRef.child(currentPerson.getId()).setValue(details);

                    String uid = currentPerson.getId();

                    editor.putString(Constants.USER_TOKEN, uid);
                    editor.putString(Constants.USER_NAME, currentPerson.getDisplayName());
                    editor.putString(Constants.USER_EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));

                    editor.apply();

                    alertDialog();
//                        Person.Image personPhoto = currentPerson.getImage ();
//                        String personGooglePlusProfile = currentPerson.getUrl ();
//                        String formatedImagePhoto = String.valueOf (personPhoto).replace ("\\","");

                }

            }
        };
        task.execute();
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Do you want to register a school?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, ViewPageActivity.class));

            }
        });


        builder.create();
        builder.show();
    }

    public void signUpPage(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpPageActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public GoogleApiClient buildApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //life is changing alot is happening
        //we wanna get the token here when the baga connects

        try {
            loginAndGetToken();
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //reconnect
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //we wanna know why it failed
        if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }
    }


}
