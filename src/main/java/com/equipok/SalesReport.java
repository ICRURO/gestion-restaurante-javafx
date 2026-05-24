package com.equipok;

import com.equipok.model.Sales;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
            reportBox.getChildren().add(new Label("            REPORTE ESTADÍSTICO DE VENTAS - " + filterName));
            reportBox.getChildren().add(new Label("===========================================================")); 
            double totalAcumulado = 0;
            Map<String, Integer> popularityMap = new HashMap<>();
            Map<Integer, Integer> hoursMap = new HashMap<>();
            Map<String, Double> monthlyEarningsMap = new HashMap<>();
            for (Sales s : salesList) {
                if (s.getSaleDate() == null) continue;
                totalAcumulado += s.getTotalFinal();
                if (s.getItems() != null && !s.getItems().trim().isEmpty()) {
                    String[] itemsArray = s.getItems().split(",\\s*");
                    for (String item : itemsArray) {
                        popularityMap.put(item, popularityMap.getOrDefault(item, 0) + 1);
                    }
                }
                int hour = s.getSaleDate().getHour();
                hoursMap.put(hour, hoursMap.getOrDefault(hour, 0) + 1);
                String monthKey = s.getSaleDate().getYear() + " - " + 
                        s.getSaleDate().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-MX")).toUpperCase();
                monthlyEarningsMap.put(monthKey, monthlyEarningsMap.getOrDefault(monthKey, 0.0) + s.getTotalFinal());
            }
            reportBox.getChildren().add(new Label(String.format("GANANCIA TOTAL EN ESTE PERIODO: $%.2f", totalAcumulado)));
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            reportBox.getChildren().add(new Label("PLATILLOS MÁS VENDIDOS:"));
            List<Map.Entry<String, Integer>> sortedPopularity = new ArrayList<>(popularityMap.entrySet());
            sortedPopularity.sort((a, b) -> b.getValue().compareTo(a.getValue())); 
            for (Map.Entry<String, Integer> entry : sortedPopularity) {
                reportBox.getChildren().add(new Label(String.format(" * %-30s : %d uds.", entry.getKey(), entry.getValue())));
            }
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            reportBox.getChildren().add(new Label("ANÁLISIS DE HORAS PICO:"));
            List<Map.Entry<Integer, Integer>> sortedHours = new ArrayList<>(hoursMap.entrySet());
            sortedHours.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            int topHoursCount = 0;
            for (Map.Entry<Integer, Integer> entry : sortedHours) {
                if (topHoursCount >= 3) break; 
                reportBox.getChildren().add(new Label(String.format(" *%02d:00 hrs a %02d:00 hrs -> %d pedidos", 
                        entry.getKey(), entry.getKey() + 1, entry.getValue())));
                topHoursCount++;
            }
            if(sortedHours.isEmpty()) {
                reportBox.getChildren().add(new Label(" No hay suficientes datos de horarios."));
            }
            reportBox.getChildren().add(new Label("-----------------------------------------------------------"));
            reportBox.getChildren().add(new Label("GANANCIAS MENSUALES:"));
            for (Map.Entry<String, Double> entry : monthlyEarningsMap.entrySet()) {
                reportBox.getChildren().add(new Label(String.format(" * %-25s: $%.2f", entry.getKey(), entry.getValue())));
            }
            if (job.printPage(reportBox)) { 
                job.endJob(); 
                return true; 
            }
        }
        return false;
    }

    //Esto solo imprime en consola, desactivar para la entrega final
    public static void debugToConsole(String filterName, List<Sales> salesList) { 
        System.out.println("\n=================== DEBUG REPORT ===================");
        System.out.println("REPORTE ESTADÍSTICO DE VENTAS - " + filterName);
        System.out.println("====================================================");
        double totalAcumulado = 0;
        Map<String, Integer> popularityMap = new HashMap<>();
        Map<Integer, Integer> hoursMap = new HashMap<>();
        Map<String, Double> monthlyEarningsMap = new HashMap<>();
        for (Sales s : salesList) {
            if (s.getSaleDate() == null) continue;
            totalAcumulado += s.getTotalFinal();
            if (s.getItems() != null && !s.getItems().trim().isEmpty()) {
                String[] itemsArray = s.getItems().split(",\\s*");
                for (String item : itemsArray) {
                    popularityMap.put(item, popularityMap.getOrDefault(item, 0) + 1);
                }
            }
            int hour = s.getSaleDate().getHour();
            hoursMap.put(hour, hoursMap.getOrDefault(hour, 0) + 1);
            String monthKey = s.getSaleDate().getYear() + " - " + 
                    s.getSaleDate().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-MX")).toUpperCase();
            monthlyEarningsMap.put(monthKey, monthlyEarningsMap.getOrDefault(monthKey, 0.0) + s.getTotalFinal());
        }
        System.out.printf("GANANCIA TOTAL EN ESTE PERIODO: $%.2f\n", totalAcumulado);
        System.out.println("----------------------------------------------------");
        System.out.println(" PLATILLOS MÁS VENDIDOS:");
        List<Map.Entry<String, Integer>> sortedPopularity = new ArrayList<>(popularityMap.entrySet());
        sortedPopularity.sort((a, b) -> b.getValue().compareTo(a.getValue())); 
        for (Map.Entry<String, Integer> entry : sortedPopularity) {
            System.out.printf(" * %-30s : %d uds.\n", entry.getKey(), entry.getValue());
        }
        System.out.println("----------------------------------------------------");
        System.out.println(" ANÁLISIS DE HORAS PICO:");
        List<Map.Entry<Integer, Integer>> sortedHours = new ArrayList<>(hoursMap.entrySet());
        sortedHours.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        int topHoursCount = 0;
        for (Map.Entry<Integer, Integer> entry : sortedHours) {
            if (topHoursCount >= 3) break;
            System.out.printf(" *%02d:00 a %02d:00 hrs -> %d Pedidos\n", 
                    entry.getKey(), entry.getKey() + 1, entry.getValue());
            topHoursCount++;
        }
        System.out.println("----------------------------------------------------");
        System.out.println("GANANCIAS MENSUALES:");
        for (Map.Entry<String, Double> entry : monthlyEarningsMap.entrySet()) {
            System.out.printf(" * %-25s: $%.2f\n", entry.getKey(), entry.getValue());
        }
        System.out.println("====================================================\n");
    }
}