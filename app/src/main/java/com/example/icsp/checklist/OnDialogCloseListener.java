package com.example.icsp.checklist;

import android.content.DialogInterface;

/**
 * OnDialogCloseListener Interface
 * <p>
 * This interface is used for closing the bottom activity form in the checklist fragment and allowing further actions based on the dialog being closed.
 */
public interface OnDialogCloseListener {
    void onDialogClose(DialogInterface dialogInterface);
}
