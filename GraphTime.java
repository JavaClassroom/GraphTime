package ru.wedro22;

import java.util.HashMap;

public class GraphTime {
    private float[] val, valCalc=null;
    public final int START, END;
    private Type type;
    private boolean calculate=false;

    /**
     * SMOOTH - промежуток заполняется сглаженными величинами (между 1 и 3 будет 2)
     * ZERO - промежуток заполняется нулевыми значениями
     */
    public enum Type{SMOOTH, ZERO}

    /**
     * виртуальный график для распределения некой величины по минутам
     * @param minuteStart с какой минуты график
     * @param minuteEnd по какую минуту
     * @param type тип графика, влияет на промежутки между заданными значениями
     */
    public GraphTime(int minuteStart, int minuteEnd, Type type){
        this.type=type;
        if (minuteStart<=minuteEnd){
            START=minuteStart;
            END=minuteEnd;
        } else {
            START=minuteEnd;
            END=minuteStart;
        }
        val = new float[END-START+1];
        for (int i = 1; i < val.length-1; i++) {
            val[i]=-1f;
        }
        val[0]=0;
        val[val.length-1]=0;
    }

    /**
     * виртуальный график для распределения некой величины по минутам
     * @param minuteStart с какой минуты график
     * @param minuteEnd по какую минуту
     * тип графика, влияющий на промежутки между заданными значениями, по умолчанию-сглаженный
     */
    public GraphTime(int minuteStart, int minuteEnd){
        this(minuteStart,minuteEnd,Type.SMOOTH);
    }

    /**
     * добавить новое значение
     * @param minute минута, должна быть больше начала действия и меньше конца действия
     * @param value значение в этой минуте
     * @return null, если аргумент минуты задано неверно
     */
    public GraphTime add(int minute, float value){
        if (minute<START | minute>END) {
            System.out.println("GraphTime add неверное время: START<" + minute + "<END");
            return null;
        }
        val[minute-START]=value;
        calculate=false;
        return this;
    }

    /**
     * сглаживание всех значений между заданными
     */
    public GraphTime smoothing(){
        copyValToCalc();
        int first=0;
        int two;
        while ((two=getNextValId(first))>0) {
            smoothInterval(first,two);
            first=two;
        }
        calculate=true;
        return this;
    }

    /**
     * обнуление всех значений кроме заданных
     */
    public GraphTime zeroing(){
        copyValToCalc();
        for (int i = 0; i < valCalc.length; i++) {
            if (valCalc[i]<0)
                valCalc[i]=0;
        }
        calculate=true;
        return this;
    }

    private void copyValToCalc(){
        valCalc=new float[val.length];
        System.arraycopy(val,0,valCalc,0,val.length);
    }

    private int getNextValId(int start){
        if (start>=valCalc.length)
            return -1;
        for (int i=start+1; i<valCalc.length; i++){
            if (valCalc[i]>=0)
                return i;
        }
        return -1;
    }

    private void smoothInterval(int first, int two){
        int minutes=two-first;
        if (minutes<=1)
            return;
        float difference=valCalc[two]-valCalc[first];
        float step=difference/minutes;
        for (int i=(first+1); i<two; i++){
            valCalc[i]=valCalc[i-1]+step;
        }
    }

    private void calculate(){
        switch (type){
            case SMOOTH:
                smoothing();
                break;
            case ZERO:
                zeroing();
                break;
        }
    }

    private void print(){
        if (!calculate) calculate();
        System.out.printf("%2s%4s%10s%16s%16s%5s%n", " ","i:","min","val","valCalc","=");
        String s=" ";
        for (int i = 0; i < val.length; i++) {
            s=(valCalc[i]==val[i] & val[i]>=0)?"*":" ";
            System.out.printf("%2s%4d%10d%16f%16f%5s%n", "#",i,i+START,val[i],valCalc[i],s);
        }
    }

    public static void main(String[] args){
        new GraphTime(0,8, Type.SMOOTH)
                .add(3,3)
                .add(6,3)
                .add(8,1)
                //.smoothing()
                .zeroing()
                .add(8,2)
                .print();

    }
}
