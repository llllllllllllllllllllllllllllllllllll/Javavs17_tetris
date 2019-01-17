package ee.itcollege.tetris.parts;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ee.itcollege.tetris.lib.CollisionDetector;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

@SuppressWarnings("serial")
public class Figure extends ArrayList<Block> {

	public void move(int deltaX, int deltaY) {

	    for (Block block : this) {
			if (block.getX() + block.getX() + deltaX * Block.SIZE >= 0 &&  block.getX() + deltaX * Block.SIZE <= Block.SIZE * 15) {
				block.setX(block.getX() + deltaX * Block.SIZE);
			}
		}
		for (Block block : this) {
			block.setY(block.getY() + deltaY * Block.SIZE);
		}

	}
    public void rotateR(){

	    int blockNumber = 0;
        for (Block block : this) {
            int state = block.getState();
            System.out.println(state + " " + blockNumber);
            switch (state) {
                case 0:
                    block.setState(1);
                    switch (blockNumber){
                        case 2:
                            block.minX();
                            block.minY();
                            break;
                    }
                    break;
                case 1:
                    block.setState(2);
                    switch (blockNumber){
                        case 2:
                            block.plusX();
                            block.plusY();
                            break;
                        case 3:
                            block.minY();
                            block.minY();
                            break;
                    }
                    break;
                case 2:
                    block.setState(3);
                    switch (blockNumber){
                        case 0:
                            block.plusX();
                            block.minY();
                            break;
                        case 3:
                            block.plusY();
                            block.plusY();
                            break;
                    }
                    break;
                case 3:
                    block.setState(0);
                    switch (blockNumber){
                        case 0:
                            block.plusY();
                            block.minX();
                            break;
                    }
                    break;
            }

            blockNumber++;
        }
    }

}






