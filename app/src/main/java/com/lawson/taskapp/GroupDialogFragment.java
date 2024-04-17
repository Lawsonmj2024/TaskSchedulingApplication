/*
Task Scheduling Application
GroupDialogFragment.java
Michael Lawson
2024, March 9
 */
package com.lawson.taskapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class GroupDialogFragment extends DialogFragment {

    private OnGroupEnteredListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText groupEditText = new EditText(requireActivity());
        groupEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        groupEditText.setMaxLines(1);

        return new AlertDialog.Builder(requireActivity())
                .setTitle("New Group")
                .setView(groupEditText)
                .setPositiveButton("Create", (dialog, whichButton) -> {
                    // Notify Listener
                    String group = groupEditText.getText().toString();
                    mListener.onGroupEntered(group.trim());
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnGroupEnteredListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnGroupEnteredListener {
        void onGroupEntered(String groupName);
    }
}
