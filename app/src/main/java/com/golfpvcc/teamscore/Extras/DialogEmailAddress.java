package com.golfpvcc.teamscore.Extras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.golfpvcc.teamscore.R;

import static android.content.Context.MODE_PRIVATE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.EMAIL_ADDRESS;

/*
 This Class will configure the email address to use when sending player's scores
  */
public class DialogEmailAddress extends AppCompatDialogFragment {
    private EditText m_EmailAddress;
    private EmailDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_email_address, null);
        builder.setView(view)
                .setTitle("Configure Email Address")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });

        m_EmailAddress = view.findViewById(R.id.Email_Address);
        final SharedPreferences pref = getActivity().getSharedPreferences(EMAIL_ADDRESS, MODE_PRIVATE);
        EmailLoadValues(pref, m_EmailAddress, EMAIL_ADDRESS, "Vgamble@golfpvcc.com");     // load the dialog window with save shared preference values

        return builder.create();


    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String EmailAddress = m_EmailAddress.getText().toString();
                    Boolean wantToCloseDialog = isEmailValid(EmailAddress);

                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (wantToCloseDialog) {
                        listener.SendEmailAddress(EmailAddress);        // send the information to the class that call this class.
                        d.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Invalid Email Address", Toast.LENGTH_LONG).show();
                    }
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EmailDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException((context.toString() + "VPG Must implement dialog class listener"));
        }
    }

    /*
    Load the store email address
     */
    private void EmailLoadValues(SharedPreferences pref, EditText EditTextValue, String quotaKeyString, String defaultValue) {
        int len;

        String Value_str = pref.getString(quotaKeyString, defaultValue);
        EditTextValue.setText(Value_str);
        len = Value_str.length();
        EditTextValue.setSelection(len);       // set the cursor at the end of the field
    }

    /*
This function validates the email address
 */
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /*
        Use to comminacate back to the main class that call this dialog
         */
    public interface EmailDialogListener {
        void SendEmailAddress(String EmailAddressForUser);
    }
}
