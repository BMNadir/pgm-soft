package pwj.ui;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Prompt {
    private static JFXDialog waitDialog;
    private static boolean dialogOpen = false;
    
    public static void alert (String msg, StackPane stackPane, AnchorPane rootAnchorPane)
    {
        BoxBlur blur = new BoxBlur(3, 3, 3);
        
        VBox vbox = new VBox();
        Label text = new Label(msg);
        text.setStyle("-fx-font-weight: bold; -fx-font-size : 10pt");
        vbox.getChildren().addAll(text);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);
        
        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(vbox);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);
        dialog.setMinSize(300, 150);
        dialog.setOnDialogClosed((JFXDialogEvent e) -> {rootAnchorPane.setEffect(null);});
        dialog.show();
        rootAnchorPane.setEffect(blur);
    }
    
     public static void wait (String msg, StackPane stackPane, AnchorPane rootAnchorPane)
    {
        BoxBlur blur = new BoxBlur(3, 3, 3);
        JFXSpinner spinner = new JFXSpinner();
        
        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Label(msg), spinner);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);
        
        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(vbox);
        content.setAlignment(Pos.CENTER);
        
        waitDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP, false);
        waitDialog.setPrefSize(300, 150);
        waitDialog.setOnDialogClosed((JFXDialogEvent e) -> {rootAnchorPane.setEffect(null);});
        waitDialog.show();
        dialogOpen = true;
        rootAnchorPane.setEffect(blur);
    }
 
     public static void closeWait()
     {
         if (dialogOpen)
         {
             dialogOpen = false;
             waitDialog.close();
         }
     }
     
}
