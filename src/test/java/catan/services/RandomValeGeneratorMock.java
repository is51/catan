package catan.services;

import java.util.LinkedList;
import java.util.List;

public class RandomValeGeneratorMock extends RandomValeGenerator {

    private List<Double> valuesToGenerate = new LinkedList<Double>();

    public double randomValue() {
        if(valuesToGenerate.size() > 0){
            return valuesToGenerate.remove(0);
        } else {
            return super.randomValue();
        }
    }

    public void setNextGeneratedValue(double nextGeneratedValue) {
        valuesToGenerate.add(nextGeneratedValue);
    }
}
