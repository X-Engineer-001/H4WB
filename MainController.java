package h4wb;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import h4wb.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.util.Duration;

enum states {WORK, FAIL, ERROR, IDLE;}
public class MainController implements Initializable {
	@FXML
	Pane _field;
	static Robot robot;
	static int screenWidth, screenHeight;
	static double pressX, pressY;
	static final double clickRange = 10, dt = 0.01;
	static boolean click, running = false, experimenting = false;
	public static Arrow showArrow;
	public static ObservableList<Node> field;
	public static final Color Work = Color.rgb(150, 255, 150), Fail = Color.rgb(255, 100, 100), Error = Color.rgb(255, 230, 0), Idle = Color.rgb(150, 150, 150);
	private static Timeline run, show, experiment;
	public static final int experimentTime = 1000;
	private static int experimentCount = 0;
	private static Line[] outputReliability = new Line[experimentTime], moduleReliability = new Line[experimentTime];
	
	private void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {}
	}
	
	public EventHandler<KeyEvent> onKeyPressed = (e)->{
		KeyCode keyCode = e.getCode();
		if(keyCode == KeyCode.ESCAPE){
			run.stop();
			Main.stage.close();
			System.exit(0);
		}else if(e.getCode()==KeyCode.CONTROL) {
			if(running) {
				run.stop();
				show.stop();
				experiment.stop();
				experimenting = false;
				for(int i = 0; i < experimentTime; i++) {
					outputReliability[i].setVisible(false);
					moduleReliability[i].setVisible(false);
				}
				for(Module module: ModuleController.modules.values()) {
					module.state = states.IDLE;
					((Shape) module.lookup("#body")).setFill(Idle);
					for(Module outModule: module.outModules.values()) {
						if(outModule.inModules.get(module.getId()) == null) {
							outModule.inModules.put(module.getId(), module);
						}
					}
				}
				running = false;
			}else {
				running = true;
				if(!e.isAltDown()) {
					for(Module module: ModuleController.modules.values()) {
						Shape body = (Shape) module.lookup("#body");
						if(module.inModules.size() <= 0) {
							module.state = states.IDLE;
							body.setFill(Idle);
						}else {
							module.state = states.WORK;
							body.setFill(Work);
						}
					}
					run.play();
				}else {
					experimenting = true;
					for(int i = 0; i < experimentTime; i++) {
						for(Module module: ModuleController.modules.values()) {
							module.experimentErrorHistory[i] = 0;
							module.experimentFailHistory[i] = 0;
						}
						outputReliability[i].setStartX(-100);
						outputReliability[i].setStartY(-100);
						outputReliability[i].setEndX(-100);
						outputReliability[i].setEndY(-100);
						outputReliability[i].setVisible(true);
						moduleReliability[i].setStartX(-100);
						moduleReliability[i].setStartY(-100);
						moduleReliability[i].setEndX(-100);
						moduleReliability[i].setEndY(-100);
						moduleReliability[i].setVisible(true);
					}
					show.play();
					experimentCount = 0;
					experiment.play();
				}
			}
		}
	};
	
	public void onClicked(MouseEvent e) throws IOException {
		MouseButton mouseButton = e.getButton();
		String targetID = e.getPickResult().getIntersectedNode().getId();
		if(!running && mouseButton == MouseButton.PRIMARY && click) {
			if("_field".equals(targetID)) {
		    	Module newModule = new Module();
		    	newModule.setLayoutX(e.getX()-((Region)newModule).getPrefWidth()/2);
		    	newModule.setLayoutY(e.getY()-((Region)newModule).getPrefHeight()/2);
		    	field.add(newModule);
			}
		}
	}
	public void onPressed(MouseEvent e) {
		MouseButton mouseButton = e.getButton();
		if(mouseButton == MouseButton.PRIMARY) {
			pressX = e.getSceneX();
			pressY = e.getSceneY();
			click = true;
		}
	}
	public void onDragged(MouseEvent e) {
		MouseButton mouseButton = e.getButton();
		if(mouseButton == MouseButton.PRIMARY) {
			if(Math.hypot(e.getSceneX()-pressX, e.getSceneY()-pressY) > clickRange) {
				click = false;
			}
		}
	}
	
	private static void timeStep(double dt, boolean draw) {
		for(Module module: ModuleController.modules.values()) {
			if(module.state == states.WORK) {
				if(Math.random() < module.lambda*dt) {
					module.state = states.FAIL;
					for(Module outModule: module.outModules.values()) {
						outModule.inModules.remove(module.getId());
					}
				}else if(module.inModules.size() <= 0){
					module.state = states.ERROR;
					for(Module outModule: module.outModules.values()) {
						outModule.inModules.remove(module.getId());
					}
				}
			}else if(module.state == states.ERROR) {
				if(Math.random() < module.lambda*dt) {
					module.state = states.FAIL;
				}else if(module.inModules.size() > 0){
					module.state = states.WORK;
					for(Module outModule: module.outModules.values()) {
						outModule.inModules.put(module.getId(), module);
					}
				}
			}else if(module.state == states.FAIL) {
				if(Math.random() < module.mu*dt) {
					if(module.inModules.size() > 0){
						module.state = states.WORK;
						for(Module outModule: module.outModules.values()) {
							outModule.inModules.put(module.getId(), module);
						}
					}else {
						module.state = states.ERROR;
					}
				}
			}
			if(draw) {
				Shape body = (Shape) module.lookup("#body");
				if(module.state == states.WORK) {
					body.setFill(Work);
				}else if(module.state == states.ERROR) {
					body.setFill(Error);
				}else if(module.state == states.FAIL) {
					body.setFill(Fail);
				}
			}
		}
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		robot = new Robot();
		javafx.geometry.Rectangle2D screenBounds  = Screen.getPrimary().getBounds();
		screenWidth = (int) screenBounds.getWidth();
		screenHeight = (int) screenBounds.getHeight();
		Main.stage.setWidth(screenWidth);
		Main.stage.setHeight(screenHeight);
//		sleep(1000);
//		_in.setLayoutX(0);
//		_in.setLayoutY(screenHeight/2 - _in.getHeight()/2);
//		_out.setLayoutX(screenWidth - _in.getWidth());
//		_out.setLayoutY(screenHeight/2 - _out.getHeight()/2);
		showArrow = new Arrow();
		showArrow.setVisible(false);
		showArrow.setArrowPosition(0);
		field = _field.getChildren();
		field.add(showArrow);
		run = new Timeline(new KeyFrame(Duration.millis(1000*dt),(e)->{
			timeStep(dt, true);
		}));
		run.setCycleCount(Timeline.INDEFINITE);
		for(int i = 0; i < experimentTime; i++) {
			moduleReliability[i] = new Line();
			moduleReliability[i].setVisible(false);
			moduleReliability[i].setStroke(Work);
			field.add(moduleReliability[i]);
			outputReliability[i] = new Line();
			outputReliability[i].setVisible(false);
			outputReliability[i].setStrokeWidth(2);
			field.add(outputReliability[i]);
		}
		show = new Timeline(new KeyFrame(Duration.millis(10),(e)->{
			if(ModuleController.pickedExperimentErrorHistory != null) {
				outputReliability[0].setStartX(0);
				outputReliability[0].setStartY(0);
				outputReliability[0].setEndX(((double)screenWidth)/((double)experimentTime));
				outputReliability[0].setEndY(((double)ModuleController.pickedExperimentErrorHistory[0])/((double)experimentCount)*screenHeight);
				moduleReliability[0].setStartX(0);
				moduleReliability[0].setStartY(0);
				moduleReliability[0].setEndX(((double)screenWidth)/((double)experimentTime));
				moduleReliability[0].setEndY(((double)ModuleController.pickedExperimentFailHistory[0])/((double)experimentCount)*screenHeight);
				for(int i = 1; i < experimentTime; i++) {
					outputReliability[i].setStartX(outputReliability[i-1].getEndX());
					outputReliability[i].setStartY(outputReliability[i-1].getEndY());
					outputReliability[i].setEndX(((double)screenWidth)/((double)experimentTime)*(i+1));
					outputReliability[i].setEndY(((double)ModuleController.pickedExperimentErrorHistory[i])/((double)experimentCount)*screenHeight);
					moduleReliability[i].setStartX(moduleReliability[i-1].getEndX());
					moduleReliability[i].setStartY(moduleReliability[i-1].getEndY());
					moduleReliability[i].setEndX(((double)screenWidth)/((double)experimentTime)*(i+1));
					moduleReliability[i].setEndY(((double)ModuleController.pickedExperimentFailHistory[i])/((double)experimentCount)*screenHeight);
				}
			}
		}));
		show.setCycleCount(Timeline.INDEFINITE);
		experiment = new Timeline(new KeyFrame(Duration.millis(1),(e)->{
			experimentCount++;
			for(Module module: ModuleController.modules.values()) {
				for(Module outModule: module.outModules.values()) {
					if(outModule.inModules.get(module.getId()) == null) {
						outModule.inModules.put(module.getId(), module);
					}
				}
			}
			for(Module module: ModuleController.modules.values()) {
				if(module.inModules.size() <= 0) {
					module.state = states.IDLE;
				}else {
					module.state = states.WORK;
				}
			}
			for(var i = 0; i < experimentTime; i++) {
				timeStep(1, false);
				for(Module module: ModuleController.modules.values()) {
					if(module.state != states.WORK) {
						module.experimentErrorHistory[i]++;
					}
					if(module.state == states.FAIL) {
						module.experimentFailHistory[i]++;
					}
				}
			}
		}));
		experiment.setCycleCount(Timeline.INDEFINITE);
	}
}
