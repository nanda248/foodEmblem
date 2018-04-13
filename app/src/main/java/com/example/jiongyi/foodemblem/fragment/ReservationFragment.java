package com.example.jiongyi.foodemblem.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.HomeActivity;
import com.example.jiongyi.foodemblem.LoginActivity;
import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.custom_adapters.CustomerReservationAdapter;
import com.example.jiongyi.foodemblem.room.CustomerReservation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class ReservationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected View mview;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ReservationFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ReservationFragment newInstance(String param1, String param2) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        this.mview = view;
        TextView txtview = ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar_title);
        txtview.setText("Reservations");
        final ProgressDialog dialog = new ProgressDialog(getContext());
        TabLayout tabLayout = view.findViewById(R.id.reservationtab);
        loadReservations(null,dialog,view);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabSelected(final TabLayout.Tab tab){
                loadReservations(tab,dialog,view);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void loadReservations(final TabLayout.Tab tab, final ProgressDialog dialog, final View view){
        //Populate list based on selectedtab
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Loading");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String tabId = "";
                    String status = "";
                    if (tab != null) {
                        tabId = (String) tab.getText();
                    }
                    if (tabId.equals("Upcoming") || tabId.equals("")){
                        status = "Active";
                    }
                    else {
                        status = "Inactive";
                    }
                    SharedPreferences sp = getContext().getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                    String email = sp.getString("UserEmail","");
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/CustomerReservation/getReservations/"+email+"/"+status);
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();

                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String jsonString) {
                try {
                    ListView listView = (ListView) view.findViewById(R.id.reservationlist);
                    listView.setAdapter(null);
                    ArrayList<CustomerReservation> customerReservationArrayList = new ArrayList<CustomerReservation>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray custReservationjsonArray = jsonObject.getJSONArray("custReservation");
                    if (custReservationjsonArray.length() > 0) {
                        JSONArray restaurantnamejsonArray = jsonObject.getJSONArray("restNames");
                        for (int i = 0; i < custReservationjsonArray.length(); i++) {
                            JSONObject reservation = custReservationjsonArray.getJSONObject(i);
                            int paxseated = reservation.getInt("noOfPaxSeated");
                            int pax = reservation.getInt("pax");
                            String jsondate = reservation.getString("reservationDate");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date reservationdate = (df).parse(jsondate);
                            SimpleDateFormat myDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm");
                            String datereservation = myDateFormat.format(reservationdate);
                            String timereservation = myTimeFormat.format(reservationdate);
                            JSONObject restseating = reservation.getJSONObject("restSeating");
                            String tableNo = restseating.getString("tableNo");
                            int seatCapacity = restseating.getInt("seatCapacity");
                            String status = reservation.getString("status");
                            String restname = restaurantnamejsonArray.getString(i);
                            CustomerReservation customerReservation = new CustomerReservation(paxseated, pax, datereservation, tableNo, seatCapacity
                                    , status, restname,timereservation);
                            customerReservationArrayList.add(customerReservation);
                        }
                        CustomerReservationAdapter adapter = new CustomerReservationAdapter(getContext(), customerReservationArrayList);
                        listView.setAdapter(adapter);
                    }
                    else {
                        listView.setEmptyView(view.findViewById(R.id.empty));
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }.execute();
    }

}
