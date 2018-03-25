package com.example.jiongyi.foodemblem.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.ReserveSeat;
import com.example.jiongyi.foodemblem.custom_adapters.RestuarantDishAdapter;
import com.example.jiongyi.foodemblem.room.RestaurantDish;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected View mview;

    private OnFragmentInteractionListener mListener;

    public RestaurantMenuFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RestaurantMenuFragment newInstance(String param1, String param2) {
        RestaurantMenuFragment fragment = new RestaurantMenuFragment();
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
        final View view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        this.mview = view;
        TextView txtview = ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar_title);
        txtview.setText("Dishes");
        final int restid = getArguments().getInt("RestaurantId");
        loadRestaurantName(restid, view);
        Button reserveBtn = (Button)view.findViewById(R.id.reserveBtn);
        reserveBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(),ReserveSeat.class);
                TextView textView = (TextView)view.findViewById(R.id.restaurantNameLbl);
                String restname = textView.getText().toString();
                intent.putExtra("RestaurantName",restname);
                intent.putExtra("RestaurantId",restid);
                startActivity(intent);
            }
        });
        final ProgressDialog dialog = new ProgressDialog(getContext());
        loadRestaurantDishes(dialog,view,restid);
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

    public void loadRestaurantDishes(final ProgressDialog dialog, final View view , final int restaurantId){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Loading Dishes");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://10.0.2.2:8080/FoodEmblemV1-war/Resources/RestaurantDish/" + restaurantId);
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
                    ListView listView = (ListView)view.findViewById(R.id.menuList);
                    listView.setAdapter(null);
                    ArrayList<RestaurantDish> restaurantDishesArrayList = new ArrayList<RestaurantDish>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray restaurantsdishjsonArray = jsonObject.getJSONArray("dishes");
                    if (restaurantsdishjsonArray.length() > 0) {
                        for (int i = 0; i < restaurantsdishjsonArray.length(); i++) {
                            JSONObject dish = restaurantsdishjsonArray.getJSONObject(i);
                            String dishname = dish.getString("name");
                            String dishdesc = dish.getString("category");
                            Double dishprice = dish.getDouble("price");
                            int imgpath = 0;
                            if (dishdesc.equals("Seafood")){
                                dishdesc = "Delicious salmon pasta made from fresh eggs long with herb grilled salmon freshly catched daily";
                                imgpath = R.drawable.salmon;

                            }
                            else if (dishdesc.equals("Meat")){
                                dishdesc = "Our chicken are grilled crispy and juicy to allow you to enjoy the best chicken chop experience." +
                                        "Served with crispy fries and wedges along with fresh coslow";
                                imgpath = R.drawable.chickenchop;

                            }
                            else
                            {
                                dishdesc = "Vegetarians are boring people however with our amazingly juicy fresh and healthy sandwich, you will become " +
                                        "lively once again";
                                imgpath = R.drawable.veggie;

                            }
                            RestaurantDish dishobj = new RestaurantDish("",imgpath,dishname,dishprice,dishdesc);
                            restaurantDishesArrayList.add(dishobj);
                        }
                        RestuarantDishAdapter adapter = new RestuarantDishAdapter(getContext(), restaurantDishesArrayList);
                        listView.setAdapter(adapter);
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

    public void loadRestaurantName(final int restaurantId, final View view){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {

            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://10.0.2.2:8080/FoodEmblemV1-war/Resources/Restaurant/getRestaurantById/" + restaurantId);
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
                        TextView textView = (TextView)view.findViewById(R.id.restaurantNameLbl);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        textView.setText(jsonObject.getJSONObject("restaurant").getString("name"));
                    }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }.execute();
    }
}
