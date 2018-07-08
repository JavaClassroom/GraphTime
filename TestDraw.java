package ru.wedro22;

import org.newdawn.slick.*;

public class TestDraw extends BasicGame {
    private GameContainer container;    //НЕ УДАЛЯТЬ

    GraphTime graph;

    /**
     * Create a new basic game
     *
     * @param title The title for the game
     */
    public TestDraw(String title) {
        super(title);
    }


    @Override
    public void init(GameContainer container) throws SlickException {
        container.setShowFPS(false);//НЕ УДАЛЯТЬ
        this.container=container;   //НЕ УДАЛЯТЬ

        graph = new GraphTime(0, 30, GraphTime.Type.SMOOTH)
                .add(5,20).add(15,50).add(20,80)
                .setTotal(100);
        graph.print();
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {

    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        graph.draw(g,100,100,200,100);
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        graph.mouseOver(100,100,200,100,newx,newy);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key==Input.KEY_F)       //НЕ УДАЛЯТЬ
            container.setShowFPS(!container.isShowingFPS());    //НЕ УДАЛЯТЬ
    }

    public static void main(String[] args){
        try {
            new AppGameContainer(new TestDraw("TestDraw"),800,600,false).start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
