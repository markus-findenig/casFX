package view;

import java.awt.Button;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;

import javax.swing.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.*;

public class SimulatorView {
	
	// Video Input Informations
	private static Label videoTypeL;
	private static TextField videoTypeTF;
	
	public static GridPane addGridPane() {
	    GridPane grid = new GridPane();
	    grid.setAlignment(Pos.CENTER);
	    grid.setHgap(10);
	    grid.setVgap(10);
	    
	    
	    
	    return grid;
	}
}
