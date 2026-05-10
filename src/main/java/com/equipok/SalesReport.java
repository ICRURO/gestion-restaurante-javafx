package com.equipok;

import com.equipok.model.Sales;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SalesReport {
    
    public static boolean print(String filterName, List<Sales> salesList) {
        PrinterJob job = PrinterJob.createPrinterJob();
        
        if (job != null && job.showPrintDialog(null)) {
            VBox reportBox = new VBox(5);
            reportBox.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-font-family: monospace;");
            reportBox.getChildren().add(new Label("REPORTE DE VENTAS - " + filterName));
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            
            double totalAcumulado = 0;
            Map<String, Integer> popularityMap = new HashMap<>();

            for (Sales s : salesList) {
                String line = String.format("Ticket #%-5d | Fecha: %s | Total: $%7.2f", s.getId(), s.getSaleDate().toLocalDate().toString(), s.getTotalFinal());
                reportBox.getChildren().add(new Label(line));
                reportBox.getChildren().add(new Label("  -> Artículos: " + (s.getItems() != null ? s.getItems() : "Ninguno")));
                totalAcumulado += s.getTotalFinal();
                
                if (s.getItems() != null && !s.getItems().trim().isEmpty()) {
                    String[] itemsArray = s.getItems().split(",\\s*");
                    for (String item : itemsArray) {
                        popularityMap.put(item, popularityMap.getOrDefault(item, 0) + 1);
                    }
                }
            }
            
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            reportBox.getChildren().add(new Label(String.format("TOTAL ACUMULADO: $%7.2f", totalAcumulado)));
            reportBox.getChildren().add(new Label("\n-----------------------------------------------------------"));
            reportBox.getChildren().add(new Label("POPULARIDAD DE PRODUCTOS (MÁS VENDIDOS)"));
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            
            List<Map.Entry<String, Integer>> sortedPopularity = new ArrayList<>(popularityMap.entrySet());
            sortedPopularity.sort((a, b) -> b.getValue().compareTo(a.getValue())); 
            for (Map.Entry<String, Integer> entry : sortedPopularity) {
                reportBox.getChildren().add(new Label(String.format(" %-30s : %d vendidos", entry.getKey(), entry.getValue())));
            }

            if (job.printPage(reportBox)) { job.endJob(); return true; }
        }
        return false;
    }
}