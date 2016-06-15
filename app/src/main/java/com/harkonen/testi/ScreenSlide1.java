package com.harkonen.testi;

/**
 * Created by Harkonen on 7.6.2016.
 */

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.achartengine.GraphicalView;
import org.achartengine.chart.CombinedXYChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.parser.client.eval.DoubleEvaluator;

import java.util.ArrayList;

public class ScreenSlide1 extends Fragment {

    XYSeriesRenderer testR;
    XYSeriesRenderer pointsRenderer;
    XYMultipleSeriesRenderer myR;

    CombinedXYChart.XYCombinedChartDef[] types = { new CombinedXYChart.XYCombinedChartDef(LineChart.TYPE, 0),
            new CombinedXYChart.XYCombinedChartDef(ScatterChart.TYPE, 1)};

    public XYSeries series = new XYSeries("");
    XYValueSeries seriesPoints = new XYValueSeries("");
    XYMultipleSeriesDataset dataset;
    CombinedXYChart chart;
    GraphicalView chartView;
    private double [] chartPoint = new double[2];

    InputFunction func = new InputFunction("x");
    InputFunction plotFunc = new InputFunction("x");

    ArrayList<String> listFuncStr = new ArrayList();
    ArrayList<InputFunction> listFunc = new ArrayList();

    private double x;
    private double y;

    double lowerX = 0.0;
    double upperX = 2.0*Math.PI;

    double lowerY = 0.0;
    double upperY = 5.0;

    private double tempValue;

    int parAmount;
    int index;
    int indexEnd;

    public String funcStrPrime = "a0 + a1*x";
    String funcStr;
    String funcWithPars;
    String tempStr;

    Array2DRowRealMatrix funcMatrix = new Array2DRowRealMatrix();
    Array2DRowRealMatrix funcVector = new Array2DRowRealMatrix();
    RealMatrix paramVector;

    DoubleEvaluator mathEval = new DoubleEvaluator();

    SharedPreferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.testi_screen_swipe, container, false);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton dataResetButton = (ImageButton) getActivity().findViewById(R.id.dataResetButton);
        ImageButton addPointButton = (ImageButton) getActivity().findViewById(R.id.addPointButton);

        dataResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                series.clear();
                seriesPoints.clear();
                setFunctionData(funcStrPrime);
                chartView.repaint();
            }
        });


        if ( savedInstanceState == null) {

            RelativeLayout lay = (RelativeLayout) getActivity().findViewById(R.id.chartContainer);

            series = new XYSeries("Least Squares");
            seriesPoints = new XYValueSeries("Points");

            testR = new XYSeriesRenderer();
            testR.setLineWidth(10.0f);

            pointsRenderer = new XYSeriesRenderer();
            pointsRenderer.setPointStyle(PointStyle.CIRCLE);
            pointsRenderer.setFillPoints(true);


            myR = new XYMultipleSeriesRenderer();



            dataset = new XYMultipleSeriesDataset();

            testR.setColor(Color.RED);
            pointsRenderer.setColor(Color.BLUE);

            myR.addSeriesRenderer(testR);
            myR.addSeriesRenderer(pointsRenderer);

            myR.setPointSize(14.0f);

            dataset.addSeries(series);
            dataset.addSeries(seriesPoints);

            chart = new CombinedXYChart(dataset,myR, types);
            chartView = new GraphicalView(getActivity(), chart);

            myR.setShowLegend(false);


            myR.setGridLineWidth(5.0f);
            myR.setLabelsTextSize(25.0f);
            myR.setYLabelsPadding(10.0f);
            myR.setShowGrid(true);

            myR.setDisplayValues(false);
            myR.setXLabels(6);

            myR.setXAxisMin(lowerX);
            myR.setXAxisMax(upperX);
            myR.setYAxisMin(lowerY);
            myR.setYAxisMax(upperY);

            myR.setGridLineWidth(5.0f);
            myR.setLabelsTextSize(25.0f);
            myR.setYLabelsPadding(10.0f);
            myR.setShowGrid(true);

            lay.addView(chartView);

            setFunctionData(funcStrPrime);

            chartView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    if ( event.getAction() == MotionEvent.ACTION_DOWN ){

                        chartPoint = chart.toRealPoint(event.getX(),event.getY());

                        x = chartPoint[0];
                        y = chartPoint[1];


                    }else if( event.getAction() == MotionEvent.ACTION_UP ){

                        seriesPoints.add(x,y);

                        for(int i = 0; i < parAmount; i++){

                            for(int j = 0; j < parAmount; j++){

                                tempValue = listFunc.get(i).value(x)*listFunc.get(j).value(x);
                                funcMatrix.setEntry(i, j, funcMatrix.getEntry(i, j) + tempValue);
                            }

                            tempValue = y*listFunc.get(i).value(x);

                            funcVector.setEntry(i, 0, funcVector.getEntry(i, 0) + tempValue);
                        }

                        if( seriesPoints.getItemCount() >= parAmount ){

                            plotWithNewData();
                        }
                        chartView.repaint();

                    }
                    return true;
                }
            });
        }
    }


    private void plotWithNewData(){

        //funcStr = strFunction.getText();
        funcStr = funcStrPrime;

        paramVector = MatrixUtils.inverse(funcMatrix).multiply(funcVector);

        for(int i = 0; i < parAmount; i++){

            funcStr = funcStr.replaceAll("a"+String.valueOf(i), String.valueOf(paramVector.getEntry(i,0)));
        }

        plotFunc.setFunction(funcStr);
        series.clear();
        plotFunc.dataXYlessPoints(lowerX, upperX, series);
    }

    public void setFunctionData(String inputStr){

        funcStrPrime = inputStr;

        funcStr = funcStrPrime.replaceAll(" ", "");

        listFuncStr.clear();
        listFunc.clear();

        tempStr = "";
        int i = 0;
        index = 0;
        indexEnd = 0;

        parAmount = 0;

        while(funcStr.contains("a"+i)){

            parAmount = parAmount + 1;

            index = funcStr.indexOf("a"+Integer.toString(i));
            indexEnd = funcStr.indexOf("a"+Integer.toString(i+1)) - 1;

            if(indexEnd==-2){

                indexEnd = funcStr.length();
            }

            tempStr = funcStr.substring(index, indexEnd);

            if(tempStr.equals("")){
                tempStr = "1";
            }

            tempStr = tempStr.replace("a"+i, "1");

            listFuncStr.add(tempStr);
            listFunc.add(new InputFunction(tempStr));

            i = i + 1;
        }

        funcMatrix = new Array2DRowRealMatrix(parAmount,parAmount);
        funcVector = new Array2DRowRealMatrix(parAmount,1);

        series.clear();
        seriesPoints.clear();
        chartView.repaint();
    }
}
