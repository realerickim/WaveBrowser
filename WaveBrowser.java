/**
 *  WaveBrowser - A browser compatible with HTML5, CSS, and Javascript.
 * @author    Eric Kim
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class WaveBrowser extends Application{
	private TabInterface currentTabInterface;
	private ArrayList<Tab> tabs = new ArrayList<Tab>();
	private ArrayList<TabInterface> tabInterfaces = new ArrayList<TabInterface>();
	private Label location = new Label();
	private TextField urlBar = new TextField();
	private TabPane tabPane = new TabPane();

	public WaveBrowser() {
		super();
		}

	public void start(final Stage stage) {
		addNewTab(); //opens the browser with one tab by default
		
		Button addTab = new Button("+");
		addTab.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addNewTab();
			}
		});
		
		urlBar = new TextField();
		urlBar.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tabs.size()>0 && urlBar.getText().length()>0) {
					connectToURL(currentTabInterface.getWebEngine(), urlBar);
					String url = currentTabInterface.getWebEngine().getLocation();
					urlBar.setText(url);
				}
			}
		});
		
		Button back = new Button("Go Back");
		back.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tabs.size()>0 && urlBar.getText().length()>0) {
					currentTabInterface.getWebEngine().executeScript("history.back()");
					String url = currentTabInterface.getWebEngine().getLocation();
					urlBar.setText(url);
				}
			}
		});
		
		Button forward = new Button("Go Forward");
		forward.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tabs.size()>0 && urlBar.getText().length()>0) {
					currentTabInterface.getWebEngine().executeScript("history.forward()");
					String url = currentTabInterface.getWebEngine().getLocation();
					urlBar.setText(url);
				}
			}
		});
		
		Button home = new Button("Home");
		home.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tabs.size()>0 && urlBar.getText().length()>0) {
					currentTabInterface.getWebEngine().load("http://www.google.com");
					String url = currentTabInterface.getWebEngine().getLocation();
					urlBar.setText(url);
				}
			}
		});
		
		Button go = new Button("GO");
		go.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tabs.size()>0 && urlBar.getText().length()>0) {
					connectToURL(currentTabInterface.getWebEngine(), urlBar);
					String url = currentTabInterface.getWebEngine().getLocation();
					urlBar.setText(url);
				}
			}
		});

		HBox controls = new HBox();
		controls.setPadding(new Insets(5));
		controls.setSpacing(5);
		controls.getChildren().addAll(back, forward, home, urlBar, go, addTab);

		StackPane rootPane = new StackPane();
		rootPane.getChildren().addAll(controls, tabPane);
		tabPane.setTranslateY(60);
		Scene scene = new Scene(rootPane);
		
		stage.setTitle("WaveBrowser");
		stage.setScene(scene);
		stage.setWidth(1366);
		stage.setHeight(700);
		
		stage.show();
	}
	
	/**
	 * Creates a new web browser tab. 
	 * @return no return value.
	 */
	private void addNewTab() {
		TabInterface tabInterface = new TabInterface();
		Tab tab = new Tab();
		tabs.add(tab);
		tabInterfaces.add(tabInterface);
		tabInterface.getWebEngine().load("http://www.google.com"); //loads homepage
		tab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
			if(isNowSelected) {
				int index = tabs.indexOf(tab);
				currentTabInterface = tabInterfaces.get(index);
				
				location.textProperty().bind(currentTabInterface.getWebEngine().locationProperty());
				tab.textProperty().bind(currentTabInterface.getWebEngine().titleProperty());
				
				currentTabInterface.getWebEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue)-> { //updates the urlBar with the current website's url. learned from https://stackoverflow.com/questions/36321786/detect-url-change-in-javafx-webview
					if(Worker.State.SUCCEEDED.equals(newValue)) {
						urlBar.setText(currentTabInterface.getWebEngine().getLocation());
					}
				});
			}
		});
		
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				int index = tabs.indexOf(tab);
				tabs.remove(index);
				tabInterfaces.remove(index);
			}
		});
		tab.setText("New Tab");
		tab.setContent(tabInterface.getDisplay());
		tabPane.getTabs().add(tab);
	}
	
	/**
	 * Loads a given URL.
	 * @return no return value.
	 */
	private void connectToURL(WebEngine webEngine, TextField urlBar) {
		boolean loadUrl = true;
		boolean addedHTTP = false;
		String url = urlBar.getText();
		if(url.length()>8 && !url.substring(0,7).equals("http://") && !url.substring(0,8).equals("https://")) {
			url = "http://"+url;
			addedHTTP = true;
		} else {
			loadUrl = false;
		} if(loadUrl && isValidURL(url))
	    	webEngine.load(url);
	    else {
	    	url = url.replaceAll(" ", "+");
	    	if(addedHTTP)
	    		url = url.substring(7, url.length());
	    	webEngine.load("http://www.google.com/search?q="+url);
	    } 
	}
	
	/**
	 * Checks if a given URL is valid.
	 * @return a boolean which indicates whether the given URL is valid.
	 */
	private boolean isValidURL(String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
