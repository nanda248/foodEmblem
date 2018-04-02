package com.example.jiongyi.foodemblem.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.jiongyi.foodemblem.HomeActivity;
import com.example.jiongyi.foodemblem.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by JiongYi on 29/3/2018.
 */

public class CustomDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your Order has been successfully sent to the kitchen!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sp = getActivity().getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                        //Finish ordering
                        sp.edit().remove("IsOrdering").apply();
                        sp.edit().remove("IsOrderingRestId").apply();
                        Intent intent = new Intent(CustomDialogFragment.this.getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}
