package ru.wedro22;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Пропорциональный график
 * создается диапазон минут,
 * добавляются значения в определенных минутах,
 * задается сглаживание,
 * получается конкретное значение от общего числа из каждой минуты
 */
public class GraphTime {
    private float[] val, valCalc=null;
    public final int START, END;
    private Type type;
    private boolean calculate=false;
    private float amount=0;
    private float total=100;
    private int maxValCalcIndex;
    private Text text=new Text();

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
     * добавить новое значение пропорции
     * @param minute минута, должна быть больше начала действия и меньше конца действия
     * @param value значение пропорции в этой минуте
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
        summ();
        set_calculate();
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
        summ();
        set_calculate();
        return this;
    }

    /**
     * получение величины в конкретный момент времени из распределяемой величины
     * @param minute время, минута
     * @return величина
     */
    public float getValue(int minute){
        if (minute<START | minute>END)
            return 0;
        if (!calculate) calculate();
        float value = valCalc[minute-START]*total/amount;
        return value;
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

    private void summ(){
        amount=0;
        for (int i = 0; i < valCalc.length; i++) {
            amount+=valCalc[i];
        }
    }

    public GraphTime setTotal(float total){
        if (total>0) {
            this.total = total;
            calculate=false;
        }
        else
            System.out.println("GraphTime setTotal: total<0");
        return this;
    }

    public float getTotal(){
        return total;
    }

    public void print(){
        if (!calculate) calculate();
        System.out.printf("%2s%4s%10s%16s%16s%5s%16f%n", " ","i:","min","val","valCalc","=",total);
        String s=" ";
        for (int i = 0; i < val.length; i++) {
            s=(valCalc[i]==val[i] & val[i]>=0)?"*":" ";
            System.out.printf("%2s%4d%10d%16f%16f%5s%16f%n", "#",i,i+START,val[i],valCalc[i],s,
                    getValue(i+START));
        }
    }

    /**
     * @param g ГРАФИКА
     * @param x стартовая позиция X графика в окне
     * @param y стартовая позиция Y графика в окне
     * @param width длина графика
     * @param height    высота графика
     */
    public void draw(Graphics g, float x, float y, float width, float height){
        if (!calculate) calculate();

        g.drawRect(x,y,width,height);

        float d_x=width/(END-START+1);    //графическая еденица длины (минута)
        float hei=valCalc[maxValCalcIndex];   //неграфическая высота
        float d_y=height/hei; //графическая еденица высоты (доля)

        float l_x1=x;           //линия начало
        float l_y1=y+height;    //линия начало
        float l_x2, l_y2;       //линия конец
        for (int i=START; i<=END; i++){   //i = минута
            l_x2=l_x1+d_x;
            l_y2=y+height-valCalc[i-START]*d_y;
            g.setColor(Color.red);
            g.drawLine(l_x1,l_y1,l_x2,l_y2);
            g.setColor(Color.white);
            l_x1=l_x2;
            l_y1=l_y2;
        }

        if (text.active){
            g.drawString(text.string, x, y-g.getFont().getLineHeight()*1.2f);
        }
    }

    public void mouseOver(float x, float y, float width, float height, float mouseX, float mouseY){
        if (mouseX<x || mouseX>(x+width) || mouseY<y || mouseY>(y+height)) {
            text.active=false;
            return;
        }
        text.active=true;
        text.str_X=mouseX;
        text.str_Y=mouseY;

        int min= (int) ((mouseX-x)*valCalc.length/width);  //определение минуты по графике

        text.string= "of "+total+" : "+String.valueOf(getValue(min));
    }

    private class Text{
        boolean active=false;
        float str_X=0;
        float str_Y=0;
        String string;
    }

    private void set_calculate(){
        maxValCalcIndex=0;
        for (int i = 0; i < valCalc.length; i++) {
            if (valCalc[maxValCalcIndex]<valCalc[i])
                maxValCalcIndex=i;
        }
        calculate=true;
    }

    public static void main(String[] args){
        GraphTime gt = new GraphTime(0,8, Type.SMOOTH)
                .add(3,3)
                .add(6,3)
                .add(8,1)
                //.smoothing()
                .zeroing()
                .add(8,2);
        gt.print();
    }
}
