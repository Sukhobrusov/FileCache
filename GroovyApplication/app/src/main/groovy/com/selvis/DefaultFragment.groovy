package com.selvis

import android.app.AlertDialog;
import android.content.DialogInterface
import android.support.v4.app.Fragment


public class DefaultFragment extends Fragment {


    AlertDialog alert(String message) {
        new AlertDialog.Builder(this.getActivity())
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss()
            }})
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }



}
