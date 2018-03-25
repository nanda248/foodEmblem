package com.example.jiongyi.foodemblem.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.custom_adapters.RestaurantAdapter;
import com.example.jiongyi.foodemblem.room.Restaurant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected View mview;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.mview = view;
        TextView txtview = ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar_title);
        txtview.setText("Home");
        SearchView searchView = (SearchView)view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search e.g Manhattan Fish Market");
        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();

        final ProgressDialog dialog = new ProgressDialog(getContext());
        loadRestaurants(dialog,view);
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

    public void loadRestaurants(final ProgressDialog dialog, final View view){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Loading Restaurants");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://10.0.2.2:8080/FoodEmblemV1-war/Resources/Restaurant");
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
                    GridView gridview = (GridView)view.findViewById(R.id.restaurantGrid);
                    gridview.setAdapter(null);
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int restid = view.getId();
                            Bundle bundle = new Bundle();
                            bundle.putInt("RestaurantId" , restid);
                            RestaurantMenuFragment restaurantMenu = new RestaurantMenuFragment();
                            restaurantMenu.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_frame, restaurantMenu);
                            fragmentTransaction.commit();
                        }
                    });
                    ArrayList<Restaurant> restaurantArrayList = new ArrayList<Restaurant>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray restaurantsjsonArray = jsonObject.getJSONArray("restaurants");
                    if (restaurantsjsonArray.length() > 0) {
                        for (int i = 0; i < restaurantsjsonArray.length(); i++) {
                            JSONObject restaurant = restaurantsjsonArray.getJSONObject(i);
                            String restaddress = restaurant.getString("address");
                            String restcontactno = restaurant.getString("contactNo");
                            String restname = restaurant.getString("name");
                            int restid = restaurant.getInt("id");
                            Restaurant restobj = new Restaurant(restid,restname,restaddress,restcontactno,"");
                            restaurantArrayList.add(restobj);
                        }
                        RestaurantAdapter adapter = new RestaurantAdapter(getContext(), restaurantArrayList);
                        gridview.setAdapter(adapter);
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
