package com.harkonen.testi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

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

public class MainActivity2 extends AppCompatActivity {

    XYSeriesRenderer testR;
    XYSeriesRenderer pointsRenderer;
    XYMultipleSeriesRenderer myR;

    CombinedXYChart.XYCombinedChartDef[] types = { new CombinedXYChart.XYCombinedChartDef(LineChart.TYPE, 0),
                                                    new CombinedXYChart.XYCombinedChartDef(ScatterChart.TYPE, 1)};

    XYSeries series;
    XYValueSeries seriesPoints;
    XYMultipleSeriesDataset dataset;
    CombinedXYChart chart;
    GraphicalView view;
    double [] chartPoint = new double[2];

    ViewPager pager;

    InputFunction func = new InputFunction("x");
    InputFunction plotFunc = new InputFunction("x");

    ArrayList<String> listFuncStr = new ArrayList();
    ArrayList<InputFunction> listFunc = new ArrayList();

    double x;
    double y;

    double lowerX = 0.0;
    double upperX = 2.0*Math.PI;

    double lowerY = 0.0;
    double upperY = 5.0;

    double tempValue;

    int parAmount;
    int index;
    int indexEnd;

    String funcStrPrime = "a0*Sin[x]+ a1*x^3 + a2*x^2 +a3*x + a4";
    String funcStr;
    String funcWithPars;
    String tempStr;

    Array2DRowRealMatrix funcMatrix = new Array2DRowRealMatrix();
    Array2DRowRealMatrix funcVector = new Array2DRowRealMatrix();
    RealMatrix paramVector;

    DoubleEvaluator mathEval = new DoubleEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        series = new XYSeries("Least Squares");
        seriesPoints = new XYValueSeries("Points");

        testR = new XYSeriesRenderer();
        testR.setLineWidth(5.0f);

        pointsRenderer = new XYSeriesRenderer();
        pointsRenderer.setPointStyle(PointStyle.CIRCLE);
        pointsRenderer.setFillPoints(true);


        myR = new XYMultipleSeriesRenderer();

        myR.setXAxisMin(lowerX);
        myR.setXAxisMax(upperX);
        myR.setYAxisMin(lowerY);
        myR.setYAxisMax(upperY);

        dataset = new XYMultipleSeriesDataset();

        testR.setColor(Color.RED);
        pointsRenderer.setColor(Color.BLUE);

        myR.addSeriesRenderer(testR);
        myR.addSeriesRenderer(pointsRenderer);

        myR.setPointSize(12.0f);

        dataset.addSeries(series);
        dataset.addSeries(seriesPoints);

        chart = new CombinedXYChart(dataset,myR, types);
        view = new GraphicalView(this, chart);
        myR.setShowLegend(false);

        pager.addView(view,0);

        setFunctionData();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if ( event.getAction() == MotionEvent.ACTION_DOWN ){

                    chartPoint = chart.toRealPoint(event.getX(),event.getY());

                    x = chartPoint[0];
                    y = chartPoint[1];

                    seriesPoints.add(x,y);

                    for(int i = 0; i < parAmount; i++){

                        for(int j = 0; j < parAmount; j++){

                            tempValue = listFunc.get(i).value(x)*listFunc.get(j).value(x);
                            funcMatrix.setEntry(i, j, funcMatrix.getEntry(i, j) + tempValue);
                        }

                        tempValue = y*listFunc.get(i).value(x);

                        funcVector.setEntry(i, 0, funcVector.getEntry(i, 0) + tempValue);
                    }

                    System.out.println(seriesPoints.getItemCount());
                    System.out.println(parAmount);
                    System.out.println(series.getItemCount());

                    if( seriesPoints.getItemCount() >= parAmount ){

                        plotWithNewData();
                    }
                    view.repaint();
                }
                return true;
            }
        });
    }

    private void plotWithNewData(){

        //funcStr = strFunction.getText();
        funcStr = funcStrPrime;

        paramVector = MatrixUtils.inverse(funcMatrix).multiply(funcVector);

        for(int i = 0; i < parAmount; i++){

            funcStr = funcStr.replaceAll("a"+String.valueOf(i), String.valueOf(Math.round(paramVector.getEntry(i, 0)*1000.0)/1000.0));
        }

        plotFunc.setFunction(funcStr);
        series.clear();
        plotFunc.dataXYlessPoints(lowerX, upperX, series);

        for(int i = 0;i<series.getItemCount();i++){

            System.out.println(String.valueOf(series.getX(i)));
            System.out.println(String.valueOf(series.getY(i)));
        }
    }

    private void setFunctionData(){

        //funcStr = strFunction.getText().replaceAll(" ", "");

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
    }
}
