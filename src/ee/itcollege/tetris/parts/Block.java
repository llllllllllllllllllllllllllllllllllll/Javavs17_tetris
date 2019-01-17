package ee.itcollege.tetris.parts;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block extends Rectangle {

	public static final int SIZE = 20;

        int state = 0;

        public int getState(){
            return state;
        }

        public void setState(int newstate){
            this.state = newstate;
        }

    public void plusX(){
        this.setX(this.getX()+Block.SIZE);
    }
    public void minX(){
        this.setX(this.getX()-Block.SIZE);
    }
    public void plusY(){
        this.setY(this.getY()+Block.SIZE);
    }
    public void minY(){
        this.setY(this.getY()-Block.SIZE);
    }

	public Block(double x, double y) {

		//        x             y        laius  kõrgus
		super(x * SIZE, y * SIZE, SIZE, SIZE);


		setFill(Color.BLACK);
	}

}
