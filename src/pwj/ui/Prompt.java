package pwj.ui;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class Prompt {
    public static void alert (String msg, StackPane stackPane, AnchorPane rootAnchorPane)
    {
        BoxBlur blur = new BoxBlur(3, 3, 3);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Label(msg));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);
        dialog.setMinSize(300, 150);
        dialog.setOnDialogClosed((JFXDialogEvent e) -> {rootAnchorPane.setEffect(null);});
        dialog.show();
        rootAnchorPane.setEffect(blur);
    }
}
