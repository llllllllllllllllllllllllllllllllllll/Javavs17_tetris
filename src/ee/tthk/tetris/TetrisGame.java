package ee.tthk.tetris;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ee.tthk.tetris.entity.Player;
import ee.tthk.tetris.lib.CollisionDetector;
import ee.tthk.tetris.lib.FigureGenerator;
import ee.tthk.tetris.parts.Block;
import ee.tthk.tetris.parts.Figure;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TetrisGame extends Application {
	// entity
	public static final int FIELD_HEIGHT = 30;
	public static final int FIELD_WIDHT = 15;
	
	public static void main(String[] args) {
		TetrisGame.launch(args);
	}
	
	public static void exit(WindowEvent e) {
		System.exit(0);
	}
	
	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");
	FigureGenerator figureGenerator = new FigureGenerator();
	Figure figure;
	ArrayList<Shape> gameField = new ArrayList<>();
	ArrayList<Block> fallenBlocks = new ArrayList<>();
	Pane layout = new Pane();
	Stage window;
	
	public void createFigure() {
		Platform.runLater(() -> {
			figure = figureGenerator.createFigure();
			figure.move(FIELD_WIDHT / 2, 0);
			layout.getChildren().addAll(figure);
			if (CollisionDetector.collide(figure, fallenBlocks)) {
				// game over
				gameOver();
			}
		});
	}

	@Override
	public void start(Stage window) throws Exception {
		this.window = window;
		createFigure();
		
		Rectangle rectangle = new Rectangle(0, 0, Block.SIZE - 1, Block.SIZE * FIELD_HEIGHT);
		rectangle.setFill(Color.RED);
		gameField.add(rectangle);
		gameField.add(new Rectangle(Block.SIZE * (FIELD_WIDHT - 1), 0, Block.SIZE, Block.SIZE * FIELD_HEIGHT));
		gameField.add(new Rectangle(0, Block.SIZE * (FIELD_HEIGHT - 1), Block.SIZE * FIELD_WIDHT, Block.SIZE));
		layout.getChildren().addAll(gameField);
		
		Platform.runLater(() -> {
			try {
				Document document = Jsoup.connect("http://www.postimees.ee").get();
				String title = document.select(".article-content").first().text();
				window.setTitle(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		
		Scene scene = new Scene(layout, Block.SIZE * FIELD_WIDHT, Block.SIZE * FIELD_HEIGHT);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			
			switch (event.getCode()) {
			case UP:
				figure.rotate();
				// if there is a collision after the rotation,
				// rotate 270deg more clockwise
				if (CollisionDetector.collide(figure, gameField)) {
					for (int i = 0; i < 3; i++) {
						figure.rotate();
					}
				}
				break;
			case DOWN:
				figure.moveIfPossible(0, 1, gameField);
				break;
			case LEFT:
				figure.moveIfPossible(-1, 0, gameField);
				break;
			case RIGHT:
				figure.moveIfPossible(1, 0, gameField);
				break;
			case SPACE:
				while (figure.moveIfPossible(0, 1, gameField))
					;
				break;
			case ESCAPE:
				gameOver();
				break;
			default:
				break;
			}
			
		});
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				boolean moved = figure.moveIfPossible(0, 1, gameField);
				if (!moved) {
					fallenBlocks.addAll(figure);
					Collections.sort(fallenBlocks);
					gameField.addAll(figure);
					createFigure();
					removeFilledLines();
				}
			}
		}, 400, 400);
		
		window.setOnCloseRequest(TetrisGame::exit);
		window.setScene(scene);
		window.show();
	}
	
	private void removeFromY(double y) {
		Platform.runLater(() -> {
			for (int i = 0; i < fallenBlocks.size(); i++) {
				if (y == fallenBlocks.get(i).getY()) {
					Block removedBlock = fallenBlocks.remove(i);
					i--;
					gameField.remove(removedBlock);
					layout.getChildren().remove(removedBlock);
				}
			}
			for (Block block : fallenBlocks) {
				if (block.getY() < y) {
					block.setY(block.getY() + Block.SIZE);
				}
			}
		}); 
	}
	
	
	private void removeFilledLines() {
		Block lastBlock = null;
		int countInRow = 0;
		for (Block block : fallenBlocks) {
			if (lastBlock == null || block.getY() == lastBlock.getY()) {
				countInRow++;
			}
			else {
				countInRow = 1;
			}
			if (countInRow >= FIELD_WIDHT - 2) {
				removeFromY(lastBlock.getY());
				countInRow = 0;
			}
			lastBlock = block;
		}
	}
	
	private void gameOver() {
		AnchorPane prompt = new AnchorPane();
		Label nameLabel = new Label("Insert your name:");
		TextField textField = new TextField();
		textField.setOnAction(e -> {
			String name = textField.getText();
			System.out.format("User inserted their name: %s", name);
			
			EntityManager em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Player player = new Player();
			player.setName(name);
			em.persist(player);
			em.getTransaction().commit();
			em.close();
			
			System.exit(0);
		});
		AnchorPane.setTopAnchor(nameLabel, 20.);
		AnchorPane.setLeftAnchor(nameLabel, 20.);
		AnchorPane.setTopAnchor(textField, 50.);
		AnchorPane.setLeftAnchor(textField, 20.);
		AnchorPane.setRightAnchor(textField, 20.);
		
		EntityManager em = entityManagerFactory.createEntityManager();
		List<Player> allPlayers = em.createQuery("from Player p order by p.points desc").getResultList();
		VBox list = new VBox();
		for (Player player : allPlayers) {
			list.getChildren().add(new Label(player.getName() + " " + player.getPoints()));
		}
		em.close();
		AnchorPane.setBottomAnchor(list, 20.);
		AnchorPane.setLeftAnchor(list, 20.);
		
		prompt.getChildren().addAll(nameLabel, textField, list);
		
		Scene scene = new Scene(prompt, Block.SIZE * 10, Block.SIZE * 20);
		window.setScene(scene);
	}

}








