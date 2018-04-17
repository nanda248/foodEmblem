package com.example.jiongyi.foodemblem;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiongyi.foodemblem.fragment.RegisterCustomerDialogFragment;
import com.example.jiongyi.foodemblem.fragment.ReservationDialogFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Spinner spinner = (Spinner) findViewById(R.id.genderddl);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final EditText etemail = (EditText)findViewById(R.id.emailinput);
        final EditText etfname = (EditText)findViewById(R.id.fnameinput);
        final EditText etlname =(EditText)findViewById(R.id.lnameinput);
        final EditText passinput = (EditText)findViewById(R.id.passwordinput);
        final EditText cfmpassinput = (EditText)findViewById(R.id.cfmpasswordinput);
        final EditText contactnum = (EditText)findViewById(R.id.contactnoinput);
        Button regbtn = (Button)findViewById(R.id.registerBtn);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call Ws
                String email = etemail.getText().toString();
                String name = etlname.getText().toString() + " " + etfname.getText().toString();
                String password = passinput.getText().toString();
                String cfmpass = cfmpassinput.getText().toString();
                String contactno = contactnum.getText().toString();
                String gender = spinner.getSelectedItem().toString();
                if (!cfmpass.equals(password)){
                    cfmpassinput.setError("Passwords do not match!");
                }
                else if (TextUtils.isEmpty(etemail.getText()) || TextUtils.isEmpty(etfname.getText()) || TextUtils.isEmpty(etlname.getText())
                        || TextUtils.isEmpty(passinput.getText()) || TextUtils.isEmpty(cfmpassinput.getText()) || TextUtils.isEmpty(contactnum.getText())
                        || gender.equals("")){
                    Toast.makeText(RegisterActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject RegisterCustomerReq = new JSONObject();
                        JSONObject customer = new JSONObject();
                        customer.put("email", email);
                        customer.put("password",password);
                        customer.put("gender",gender);
                        customer.put("contactNo",contactno);
                        customer.put("name",name);
                        RegisterCustomerReq.put("customer",customer);
                        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                        registerUser(RegisterCustomerReq,dialog);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    public void registerUser(final JSONObject customer, final ProgressDialog dialog){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Creating Account..");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                String data = "";
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://192.168.137.1:8080/FoodEmblemV1-war/Resources/Customer/RegisterCustomer");
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(customer.toString());
                    wr.flush();
                    wr.close();
                    InputStream in = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(in);

                    int inputStreamData = inputStreamReader.read();
                    while (inputStreamData != -1) {
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }
                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(String jsonString) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    DialogFragment dialogFragment = new RegisterCustomerDialogFragment();
                    dialogFragment.show(getFragmentManager(),"RegisterCustomerDialog");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
