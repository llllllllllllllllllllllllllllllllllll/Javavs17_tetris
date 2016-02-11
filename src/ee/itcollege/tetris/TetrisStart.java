package ee.itcollege.tetris;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ee.itcollege.tetris.parts.Block;
import ee.itcollege.tetris.parts.Figure;

public class TetrisStart extends Application {
	
	public static void main(String[] args) {
		TetrisStart.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		Pane layout = new Pane();
		
		ObservableList<Node> contents = layout.getChildren();
		
		Figure figure = new Figure();
		figure.getChildren().add(new Block(10, 10));
		figure.getChildren().add(new Block(11, 10));
		figure.getChildren().add(new Block(10, 11));
		
		
		contents.add(figure);
		
		
		Scene scene = new Scene(layout, Block.SIZE * 20, Block.SIZE * 40);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (KeyCode.UP.equals(event.getCode())) {
				figure.move(0, -1);
			}
		});
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				figure.move(0, 1);
			}
		}, 1000, 1000);
		
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.setScene(scene);
		stage.show();
	}

}
