package my.mtg.throneofeldraine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.AppCompatSpinner;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    SpannableStringBuilder[] items = null;
    boolean[] selection = null;
    AlertDialog.Builder builder;

    public MultiSelectionSpinner(Context context) {
        super(context);
        builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    }

    @Override
    public void onClick(DialogInterface dialog, int idx, boolean isChecked) {
        if (selection != null && idx < selection.length) {
            selection[idx] = isChecked;
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        builder.setMultiChoiceItems(items, selection, this);
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(SpannableStringBuilder[] item) {

        items = item;
        selection = new boolean[items.length];
        Arrays.fill(selection, true);
    }

    public void setSelection(int index) {
        Arrays.fill(selection, false);
        if (index >= 0 && index < selection.length) {
            selection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
    }

    public List<String> getSelectedStrings() {
        List<String> select = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            if (selection[i]) {
                select.add(items[i].toString().trim());
            }
        }
        return select;
    }
}
