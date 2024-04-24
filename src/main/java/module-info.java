module edu.redwoods.cis18.scam.remotegardenproject2 {
	requires javafx.controls;
	requires javafx.fxml;
	requires jssc;
    requires java.sql;  // Ensure this is added if jssc is used in any of these packages

	exports edu.redwoods.cis18.scam.remotegardenproject2.be;
	exports edu.redwoods.cis18.scam.remotegardenproject2.db;
	exports edu.redwoods.cis18.scam.remotegardenproject2.jfx;

	opens edu.redwoods.cis18.scam.remotegardenproject2.jfx to javafx.fxml;
}
