package edu.redwoods.cis18.scam.remotegardenproject2.be;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

public class CheckBoxListCell<T> extends ListCell<T> {
	private final CheckBox checkBox = new CheckBox();

	public CheckBoxListCell() {
		checkBox.setOnAction(e -> {
			T item = getItem();
			if (item != null) {
				checkBox.setSelected(!checkBox.isSelected());
			}
		});
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setGraphic(null);
		} else {
			checkBox.setText(item.toString());
			setGraphic(checkBox);
		}
	}
}