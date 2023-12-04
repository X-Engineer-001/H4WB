package h4wb;

import java.io.IOException;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ModuleController {
	@FXML
	Pane module;
	@FXML
	Circle body;
	@FXML
	TextField lambdaText;
	@FXML
	TextField muText;
	static double dragX, dragY;
	public static Node picked = null;
	public static HashMap<String, Module> modules = new HashMap<String, Module>();
	public static double lastLambda = 0, lastMu = 0;
	public static int[] pickedExperimentErrorHistory = null, pickedExperimentFailHistory = null;
	public void onPressed(MouseEvent e) {
		MouseButton mouseButton = e.getButton();
		if(picked == null) {
			picked = e.getPickResult().getIntersectedNode();
			dragX = e.getSceneX();
			dragY = e.getSceneY();
			while(picked.getId() == null || !picked.getId().contains(module.getId())) {
				picked = picked.getParent();
			}
			pickedExperimentErrorHistory = modules.get(picked.getId()).experimentErrorHistory;
			pickedExperimentFailHistory = modules.get(picked.getId()).experimentFailHistory;
//			Module test = modules.get(picked.getId());
//			for(Module module: test.inModules.values()) {
//				System.out.println(module);
//			}
//			System.out.println();
//			for(Module module: test.outModules.values()) {
//				System.out.println(module);
//			}
//			System.out.println();
//			System.out.println();
		}
		if(!MainController.running && mouseButton == MouseButton.SECONDARY) {
			MainController.showArrow.setStartX(picked.getLayoutX()+((Region) picked).getPrefWidth()/2);
			MainController.showArrow.setStartY(picked.getLayoutY()+((Region) picked).getPrefHeight()/2);
			MainController.showArrow.setEndX(MainController.showArrow.getStartX());
			MainController.showArrow.setEndY(MainController.showArrow.getStartY());
			MainController.showArrow.setVisible(true);
		}
	}
	public void onDragged(MouseEvent e) {
		MouseButton mouseButton = e.getButton();
		if(mouseButton == MouseButton.PRIMARY) {
			if(picked != null) {
				picked.setLayoutX(Math.min(Math.max(picked.getLayoutX()+e.getSceneX()-dragX, 0), Main.stage.getWidth()-((Region) picked).getPrefWidth()));
				picked.setLayoutY(Math.min(Math.max(picked.getLayoutY()+e.getSceneY()-dragY, 0), Main.stage.getHeight()-((Region) picked).getPrefHeight()));
				dragX = e.getSceneX();
				dragY = e.getSceneY();
				Module moving = modules.get(picked.getId());
				for(Arrow arrow: moving.inArrows.values()) {
					arrow.setEndX(moving.getLayoutX()+((Region) moving).getPrefWidth()/2);
					arrow.setEndY(moving.getLayoutY()+((Region) moving).getPrefHeight()/2);
				}
				for(Arrow arrow: moving.outArrows.values()) {
					arrow.setStartX(moving.getLayoutX()+((Region) moving).getPrefWidth()/2);
					arrow.setStartY(moving.getLayoutY()+((Region) moving).getPrefHeight()/2);
				}
			}
		}else if(!MainController.running && mouseButton == MouseButton.SECONDARY) {
			MainController.showArrow.setEndX(e.getSceneX());
			MainController.showArrow.setEndY(e.getSceneY());
		}
	}
	public void onReleased(MouseEvent e) {
		MouseButton mouseButton = e.getButton();
		if(!MainController.running && mouseButton == MouseButton.SECONDARY) {
			Node target = e.getPickResult().getIntersectedNode();
			MainController.showArrow.setVisible(false);
			try {
				while(target.getId() == null || !target.getId().contains(module.getId())) {
					target = target.getParent();
				}
				if(target.getId() != picked.getId()) {
					Module from = modules.get(picked.getId()), to = modules.get(target.getId());
					if(from.outModules.get(to.getId()) == null && from.inModules.get(to.getId()) == null) {
						Arrow newArrow = new Arrow();
						newArrow.setStartX(MainController.showArrow.getStartX());
						newArrow.setStartY(MainController.showArrow.getStartY());
						newArrow.setEndX(target.getLayoutX()+((Region) target).getPrefWidth()/2);
						newArrow.setEndY(target.getLayoutY()+((Region) target).getPrefHeight()/2);
						from.outModules.put(to.getId(), to);
						from.outArrows.put(newArrow.getId(), newArrow);
						to.inModules.put(from.getId(), from);
						to.inArrows.put(newArrow.getId(), newArrow);
						MainController.field.add(newArrow);
						newArrow.toBack();
					}
				}
			}catch(Exception ex) {}
		}
		if(!e.isPrimaryButtonDown() && !e.isSecondaryButtonDown()) {
			picked = null;
		}
	}
	public void onInput(ActionEvent e) {
//		TextField target = (TextField) e.getTarget();
//		Module module = modules.get(target.getParent().getId());
//		if("lambdaText".equals(target.getId())) {
//			try {
//				module.lambda = Double.valueOf(target.getText());
//			}catch(Exception ex) {
//				module.lambda = 0;
//			}
//		}else if("muText".equals(target.getId())) {
//			try {
//				module.mu = Double.valueOf(target.getText());
//			}catch(Exception ex) {
//				module.mu = 0;
//			}
//		}
	}
}
