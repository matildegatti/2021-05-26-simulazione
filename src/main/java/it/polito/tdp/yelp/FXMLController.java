/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Business b=this.cmbLocale.getValue();
    	if(b==null) {
    		this.txtResult.setText("Selezionare prima il locale di partenza");
    		return;
    	}
    	
    	double x;
    	try {
    		x=Double.parseDouble(this.txtX.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un numero");
    		return;
    	}
    	
    	if(x<0 || x>1) {
    		this.txtResult.setText("Inserire un numero con virgola compreso tra 0 e 1");
    		return;
    	}
    	
    	List<Business> list=this.model.calcolaPercorso(b,x);
    	for(Business bus:list) {
    		this.txtResult.appendText(bus.getBusinessName()+"\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Integer anno=this.cmbAnno.getValue();
    	String citta=this.cmbCitta.getValue();
    	
    	if(anno==null || citta==null) {
    		this.txtResult.setText("Selezionare prima un anno e una città");
    		return;
    	}
    	
    	this.model.creaGrafo(anno,citta);
    	
    	this.cmbLocale.getItems().addAll(this.model.getBusiness());
    		
    	this.txtResult.setText("Grafo creato con: "+this.model.nVertici()+" vertici e "+this.model.nArchi()+" archi.");

    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	this.txtResult.clear();
    	
    	if(!this.model.getGrafo()) {
    		this.txtResult.setText("Creare prima il grafo!");
    		return;
    	}
    	
    	Business b=this.model.doLocaleMigliore();
    	this.txtResult.setText("Locale migliore: "+b.getBusinessName());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	List<Integer> anno=new LinkedList<>();
    	for(int i=2005;i<=2013;i++) {
    		anno.add(i);
    	}
    	this.cmbAnno.getItems().addAll(anno);
    	
    	List<String> citta=this.model.getCitta();
    	this.cmbCitta.getItems().addAll(citta);
    }
}
