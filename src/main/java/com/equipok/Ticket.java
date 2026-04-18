package com.equipok;

import com.equipok.model.Bill;

import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Ticket {
    public static void print(Bill bill, String method) {
        // 1. Creamos un contenedor visual (como una mini-ventana)
        VBox ticket = new VBox(10);
        ticket.setPadding(new Insets(20));
        ticket.setStyle("-fx-background-color: white;");

        // 2. Añadimos el contenido (Todo en inglés como acordamos)
        ticket.getChildren().addAll(
            new Label("RESTAURANTE EQUIPO K"),
            new Label("---------------------------"),
            new Label("Table ID: " + bill.getTableId()),
            new Label("Items: " + bill.getItems()),
            new Label("Method: " + method),
            new Label("---------------------------"),
            new Label("TOTAL: $" + bill.getTotal()),
            new Label("---------------------------"),
            new Label("Thank you for your visit!")
        );

        // 3. Usamos la API de impresión nativa de JavaFX
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(null)) {
            // El usuario elige "Print to PDF" en la ventana que sale
            boolean success = job.printPage(ticket);
            if (success) {
                job.endJob();
                System.out.println("Printing finished.");
            }
        }
    }
}
