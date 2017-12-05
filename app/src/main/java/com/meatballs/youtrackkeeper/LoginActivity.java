package com.meatballs.youtrackkeeper;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.meatballs.youtrackkeeper.REST.ProjectModel;
import com.meatballs.youtrackkeeper.REST.RESTClient;
import com.meatballs.youtrackkeeper.REST.YouTrackRESTService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mTokentView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTokentView = (EditText) findViewById(R.id.token);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        try {
            mAuthTask = new UserLoginTask();
            List<String> projectsList = mAuthTask.execute((Void) null).get();
            StringBuilder sb = new StringBuilder();
            for (String s : projectsList) {
                sb.append(s);
                sb.append("\t");
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(sb.toString());
            builder1.setCancelable(true);
            AlertDialog alert11 = builder1.create();
            alert11.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        if (mAuthTask != null) {
//            return;
//        }
//
//        // Reset errors.
//        mTokentView.setError(null);
//        mPasswordView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String email = mTokentView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mTokentView.setError(getString(R.string.error_field_required));
//            focusView = mTokentView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mTokentView.setError(getString(R.string.error_invalid_email));
//            focusView = mTokentView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
//        }
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, List<String>> {

        final List<String> projects = new ArrayList<String>();

        @Override
        protected List<String> doInBackground(Void... params) {
            RESTClient client = new RESTClient(mTokentView.getText().toString());
            client.Connect();
            YouTrackRESTService service = RESTClient.getApi();
            Call<List<ProjectModel>> projectsResult = service.GetProjects();
            projectsResult.enqueue(new Callback<List<ProjectModel>>() {
                @Override
                public void onResponse(Call<List<ProjectModel>> call, Response<List<ProjectModel>> response) {
                    projects.add(response.body().toString());
                }

                @Override
                public void onFailure(Call<List<ProjectModel>> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
            return projects;
        }
    }
}

