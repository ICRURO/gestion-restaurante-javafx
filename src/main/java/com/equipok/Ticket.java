package com.equipok;

import com.equipok.model.Bill;

import com.equipok.model.Product;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Ticket {
    public static boolean print(Bill bill, List<Product> items, double tip, double discount, String method) {
        VBox ticket = new VBox(5);
        ticket.setPadding(new Insets(20));
        ticket.setStyle("-fx-background-color: white; -fx-font-family: monospace;");
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String now = dtf.format(LocalDateTime.now());

        ticket.getChildren().addAll(
            new Label("       RESTAURANTE       "),
            new Label("---------------------------"),
            new Label("Fecha: " + now),
            new Label("Ticket No.: " + bill.getId()),
            new Label("Mesa No.: " + bill.getTableId()),
            new Label("---------------------------"),
            new Label("PRODUCTOS:")
        );

        double subtotal = 0;
        for (Product p : items) {
            ticket.getChildren().add(new Label(String.format("%-15s $%7.2f", p.getName(), p.getPrice())));
            subtotal += p.getPrice();
        }

        double total = Math.max(0, subtotal - discount) + tip;

        ticket.getChildren().addAll(
            new Label("---------------------------"),
            new Label(String.format("Subtotal:       $%7.2f", subtotal)),
            new Label(String.format("Descuento:      $%7.2f", discount)),
            new Label(String.format("Propina:        $%7.2f", tip)),
            new Label("---------------------------"),
            new Label(String.format("TOTAL:          $%7.2f", total)),
            new Label("---------------------------"),
            new Label("Método de Pago: " + method),
            new Label("---------------------------"),
            new Label("  ¡Gracias por su compra!  ")
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
