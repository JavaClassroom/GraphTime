//<applet code="HelloWorldApplet" width=200 height=40>
package ru.wedro22;

import java.applet.Applet;
import java.awt.*;


public class TestApplet extends Applet {
    GraphTime graph = new GraphTime(5, 30, GraphTime.Type.SMOOTH);

    @Override
    public void init() {
        graph.add(15, 100)
                .add(10,85)
                .add(20,80)
                .add(5,20)
                .add(25,15);
    }

    @Override
    public void paint(Graphics g) {
        graph.draw(g);
    }
}
//</applet>
