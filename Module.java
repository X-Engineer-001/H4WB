package h4wb;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
public class Module extends Pane{
	public HashMap<String, Arrow> inArrows = new HashMap<String, Arrow>(), outArrows = new HashMap<String, Arrow>();
	public HashMap<String, Module> inModules = new HashMap<String, Module>(), outModules = new HashMap<String, Module>();
	public int[] experimentErrorHistory = new int[MainController.experimentTime], experimentFailHistory = new int[MainController.experimentTime];;
	public double lambda = 0, mu = 0;
	public states state = states.IDLE;
	private static int nextID = 0;
	public Module() throws IOException {
		Pane moduleT = new FXMLLoader(getClass().getResource("moduleT.fxml")).load();
		this.getChildren().addAll(moduleT.getChildren());
		this.setPrefSize(moduleT.getPrefWidth(), moduleT.getPrefHeight());
		this.setId(moduleT.getId()+String.valueOf(nextID++));
		ContextMenu empty = new ContextMenu();
		TextField lambdaText = (TextField) this.lookup("#lambdaText"), muText = (TextField) this.lookup("#muText");
		lambdaText.setContextMenu(empty);
		muText.setContextMenu(empty);
		lambdaText.setOnKeyReleased((e) -> {
			try {
				this.lambda = Double.valueOf(lambdaText.getText());
				ModuleController.lastLambda = this.lambda;
			}catch(Exception ex) {
				this.lambda = 0;
			}
		});
		muText.setOnKeyReleased((e) -> {
			try {
				this.mu = Double.valueOf(muText.getText());
				ModuleController.lastMu = this.mu;
			}catch(Exception ex) {
				this.mu = 0;
			}
		});
		if(ModuleController.lastLambda > 0) {
			this.lambda = ModuleController.lastLambda;
			lambdaText.setText(String.valueOf(this.lambda));
		}
		if(ModuleController.lastMu > 0) {
			this.mu = ModuleController.lastMu;
			muText.setText(String.valueOf(this.mu));
		}
		ModuleController.modules.put(this.getId(), this);
		ModuleController.pickedExperimentErrorHistory = this.experimentErrorHistory;
		ModuleController.pickedExperimentFailHistory = this.experimentFailHistory;
	}
}
