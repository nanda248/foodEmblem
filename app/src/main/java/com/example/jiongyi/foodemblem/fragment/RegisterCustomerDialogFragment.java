package com.example.jiongyi.foodemblem.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.jiongyi.foodemblem.HomeActivity;
import com.example.jiongyi.foodemblem.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by JiongYi on 12/4/2018.
 */

public class RegisterCustomerDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your account has been successfully created!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(RegisterCustomerDialogFragment.this.getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}
