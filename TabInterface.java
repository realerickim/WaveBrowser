/**
 *  TabInterface - the interface for each Tab in WaveBrowser.
 * @author    Eric Kim
 */

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TabInterface {
	private WebView browser = new WebView();
	private WebEngine webEngine = browser.getEngine();
	private Label loading = new Label();
	private VBox display;
	
	
	public TabInterface() {
		display = new VBox();
		display.setPadding(new Insets(5));
		display.setSpacing(5);
		display.getChildren().addAll(browser, loading);
		display.getChildren().add(new Label("Tab"));
		display.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Gets the display.
	 * @return a VBox which is the display.
	 */
	public VBox getDisplay() {
		return display;
	}
	
	/**
	 * Gets the WebView.
	 * @return a WebView which is the TabInterface's WebView.
	 */
	public WebView getWebView() {
		return browser;
	}
	
	/**
	 * Gets the WebEngine.
	 * @return a WebEngine which is the TabInterface's WebEngine.
	 */
	public WebEngine getWebEngine() {
		return webEngine;
	}

}
