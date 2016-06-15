package com.harkonen.testi;

/**
 * Created by Harkonen on 7.6.2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlide2 extends Fragment {

    String functionStr;
    EditText editText;
    EditText editTextPrime;

    List<String> spinnerArray = new ArrayList<String>();

    int numberOfParams = 2;
    int powerPoly = 2;
    int freqSin = 1;
    int freqCos = 1;

    String newParamStr = "a2";

    ArrayAdapter<String> adapter;
    Spinner spinnerList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        spinnerArray.add("Add a term");
        spinnerArray.add("x^2");
        spinnerArray.add("Sin[x]");
        spinnerArray.add("Cos[x]");

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.testi_screen_swipe2, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = (EditText) getActivity().findViewById(R.id.modelStrInput);

        Button applyButton = (Button) getActivity().findViewById(R.id.applyButton);
        Button clearButton = (Button) getActivity().findViewById(R.id.clearButton);
        spinnerList  = (Spinner) getActivity().findViewById(R.id.spinner);


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                functionStr = editText.getText().toString();

                try {
                    ((MainActivity)getActivity()).setFuncStr(functionStr);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.setText("");
                resetSpinnerArray();
            }
        });

        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if( position == 0){
                    //System.out.println("ok");
                }else if( position == 1){

                    if(editText.getText().toString().equals("")){
                        editText.setText("a"+String.valueOf(numberOfParams) + "*x");
                    }else{

                        if( powerPoly == 1){
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams)
                                    + "*x");
                        }else {
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams)
                                             + "*x^" + String.valueOf(powerPoly));
                        }
                    }

                    powerPoly = powerPoly + 1;
                    numberOfParams = numberOfParams + 1;

                    spinnerArray.set(1,"x^" + String.valueOf(powerPoly));
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerList.setAdapter(adapter);

                }else if( position == 2){
                    if(editText.getText().toString().equals("")) {
                        editText.setText("a" + String.valueOf(numberOfParams) + "*Sin[x]");
                    }else {

                        if (freqSin == 1) {
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams) + "*Sin[x]");
                        } else {
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams)
                                    + "*Sin[" + String.valueOf(freqSin) + "*x]");
                        }
                    }

                    freqSin = freqSin + 1;
                    numberOfParams = numberOfParams + 1;

                    spinnerArray.set(2,"Sin[" + String.valueOf(freqSin) + "*x]");
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerList.setAdapter(adapter);

                }else if( position == 3){

                    if(editText.getText().toString().equals("")) {
                        editText.setText("a" + String.valueOf(numberOfParams) + "*Cos[x]");
                    }else {

                        if (freqCos == 1) {
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams) + "*Cos[x]");
                        } else {
                            editText.setText(editText.getText() + " + " + "a" + String.valueOf(numberOfParams)
                                    + "*Cos[" + String.valueOf(freqCos) + "*x]");
                        }
                    }

                    freqCos = freqCos + 1;
                    numberOfParams = numberOfParams + 1;

                    spinnerArray.set(3,"Cos[" + String.valueOf(freqCos) + "*x]");
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerList.setAdapter(adapter);

                } else if( position == 4){

                    if(editText.getText().toString().equals("")){
                        editText.setText("a"+String.valueOf(numberOfParams));
                    }else{
                        editText.setText(editText.getText() + " + " + "a"+String.valueOf(numberOfParams));
                    }

                    numberOfParams = numberOfParams + 1;

                    spinnerArray.remove(4);
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerList.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void resetSpinnerArray(){

        powerPoly = 1;
        freqSin = 1;
        freqCos = 1;
        numberOfParams = 0;

        if( spinnerArray.size() == 5 ){

            spinnerArray.set(0,"Add a term");
            spinnerArray.set(1,"x");
            spinnerArray.set(2,"Sin[x]");
            spinnerArray.set(3,"Cos[x]");
            spinnerArray.set(4,"Constant");
        }else{

            spinnerArray.set(0,"Add a term");
            spinnerArray.set(1,"x");
            spinnerArray.set(2,"Sin[x]");
            spinnerArray.set(3,"Cos[x]");
            spinnerArray.add(4,"Constant");
        }



        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerList.setAdapter(adapter);
    }


}
