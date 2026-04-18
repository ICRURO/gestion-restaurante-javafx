package com.equipok;

import com.equipok.model.Bill;

import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Ticket {
    public static boolean print(Bill bill, String method) {
        VBox ticket = new VBox(10);
        ticket.setPadding(new Insets(20));
        ticket.setStyle("-fx-background-color: white;");
        ticket.getChildren().addAll(
            new Label("RESTAURANTE"),
            new Label("---------------------------"),
            new Label("Mesa No.: " + bill.getTableId()),
            new Label("Cuenta: " + bill.getItems()),
            new Label("Método de Pago: " + method),
            new Label("---------------------------"),
            new Label("TOTAL: $" + bill.getTotal()),
            new Label("---------------------------"),
            new Label("¡Gracias por su compra!")
        );
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(ticket);
            if (success) {
                job.endJob();
                return true;
            }
        }
        return false;
    }
}
