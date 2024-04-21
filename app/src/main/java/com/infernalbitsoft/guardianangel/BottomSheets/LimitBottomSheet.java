package com.infernalbitsoft.guardianangel.BottomSheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infernalbitsoft.guardianangel.R;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class LimitBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {


    public static final String TAG = "LimitBottomSheet";
    private ItemClickListener mListener;

    private Spinner s;
    private Spinner m;

    private String app;
    private String pack;

    public static LimitBottomSheet newInstance(String app, String pack) {
        return new LimitBottomSheet(app, pack);
    }

    LimitBottomSheet(String app, String pack) {
        this.app = app;
        this.pack = pack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_usage_limit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView setLimitHeader = view.findViewById(R.id.set_limit_header);
        setLimitHeader.setText("Set Daily Limit for ".concat(app));

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++)
            hours[i] = String.valueOf(i);

        String[] minutes = new String[13];
        for (int i = 0; i < 13; i++) {
            if (i == 12)
                minutes[i] = String.valueOf((i * 5) - 1);
            else
                minutes[i] = String.valueOf(i * 5);
        }

        s = view.findViewById(R.id.limit_hours);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        m = view.findViewById(R.id.limit_minutes);
        ArrayAdapter<String> adapter_m = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, minutes);
        adapter_m.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m.setAdapter(adapter_m);

        s.setSelection(1);
        m.setSelection(0);

        Button setLimit = view.findViewById(R.id.submit_limit);
        setLimit.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

        String hour = s.getSelectedItem().toString();
        String minute = m.getSelectedItem().toString();

        if (hour.equals("0") && minute.equals("0"))
            RealToast(view.getContext(), "Limit cannot be 0");
        else {
            mListener.onItemClick(pack, s.getSelectedItem().toString(), m.getSelectedItem().toString());
            dismiss();
        }
    }

    public interface ItemClickListener {
        void onItemClick(String pack, String hour, String minute);
    }
}