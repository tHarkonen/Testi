package com.harkonen.testi;

import org.achartengine.model.XYSeries;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;


/**
 * Created by Harkonen on 5.6.2016.
 */
public class InputFunction implements UnivariateFunction {

    protected String function;

    protected DoubleEvaluator engine = new DoubleEvaluator();
    protected DoubleVariable variable = new DoubleVariable(0.0);

    double step;
    double tempX;
    double tempY;

    public InputFunction(String funcString){

        function = funcString;

        engine.defineVariable("x",variable);
        engine.evaluate(funcString);
        engine.evaluate();

    }

    @Override
    public double value(double inputVar) {

        variable.setValue(inputVar);
        try{
            Double temp = engine.evaluate();

            if(temp==Double.NEGATIVE_INFINITY | temp==Double.POSITIVE_INFINITY | temp==Double.NaN){
                temp = 0.0;
            }

            return temp;
        }catch(Exception e){
            return 0.0;
        }
    }

    public void setFunction(String newFunction){

        function = newFunction;
        engine.evaluate(function);
    }

    public String getFunction(){

        return function;
    }

    void dataXYlessPoints(Double startX, Double endX, XYSeries series){

        step = (endX - startX)/100;
        tempX = startX;

        step = 0.9999999999*step;

        for(int i=0; i<=100; i++){

            tempY = this.value(tempX);

            series.add(tempX, tempY);

            tempX = tempX + step;
        }
    }
}
