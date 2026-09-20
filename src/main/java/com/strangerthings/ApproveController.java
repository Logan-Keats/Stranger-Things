package com.strangerthings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ApproveController {

    @FXML
    private void onApprove(ActionEvent event) {
        System.out.println("Approve clicked");
    }

    @FXML
    private void onReject(ActionEvent event) {
        System.out.println("Reject clicked");
    }
}
