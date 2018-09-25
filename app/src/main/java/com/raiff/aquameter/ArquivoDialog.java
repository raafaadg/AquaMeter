package com.raiff.aquameter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ArquivoDialog extends AppCompatDialogFragment {

    EditText et_amostragem;
    ArquivoDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedinstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog,null);

        builder.setView(view)
                .setTitle("Amostragem")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Selecionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int amostragem = Integer.parseInt(et_amostragem.getText().toString());
                        listener.applyTexts(amostragem);
                    }
                });
        et_amostragem = view.findViewById(R.id.et_amostragem);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener =(ArquivoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
            "deve implementar ArquivoDialogListner");
        }
    }
}
