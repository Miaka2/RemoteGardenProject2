module edu.redwoods.cis18.scam.remotegardenproject2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.redwoods.cis18.scam.remotegardenproject2 to javafx.fxml;
    exports edu.redwoods.cis18.scam.remotegardenproject2;
}