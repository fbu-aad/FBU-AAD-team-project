package com.example.una;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

public class CurrencyTextWatcher implements TextWatcher {

    EditText editText;
    String currentAmount;

    public CurrencyTextWatcher(EditText editText, String currentAmount) {
        this.editText = editText;
        this.currentAmount = currentAmount;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(currentAmount)) {
            // remove the change listener
            editText.removeTextChangedListener(this);

            // format the string to be in $#,##0.00 format
            String cleanAmount = s.toString().replaceAll("[,$.]", "");
            double parsed = Double.parseDouble(cleanAmount);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed/100);

            currentAmount = formatted;
            editText.setText(formatted);
            editText.setSelection(formatted.length());

            // replace the change listener
            editText.addTextChangedListener(this);
        }

    }

    @Override
    public void afterTextChanged(Editable s) { }
}
