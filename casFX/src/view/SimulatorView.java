package view;


import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


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
