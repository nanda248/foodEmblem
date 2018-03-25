package com.example.jiongyi.foodemblem.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jiongyi.foodemblem.HomeActivity;
import com.example.jiongyi.foodemblem.R;


public class ReservationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Reservation Completed! Please be seated within 5 minutes or your reservation will be voided!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ReservationDialogFragment.this.getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }



}
