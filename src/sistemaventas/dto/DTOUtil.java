/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para DTOs
 * Métodos helper para formateo y conversión
 */
public class DTOUtil {
    
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Formatea un BigDecimal como moneda peruana
     */
    public static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "S/ 0.00";
        }
        return "S/ " + MONEY_FORMAT.format(amount);
    }
    
    /**
     * Formatea un LocalDateTime
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMAT);
    }
    
    /**
     * Formatea una fecha
     */
    public static String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMAT);
    }
    
    /**
     * Convierte string a BigDecimal de forma segura
     */
    public static BigDecimal parseMoney(String moneyString) {
        if (moneyString == null || moneyString.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Remover símbolos de moneda y espacios
            String cleaned = moneyString.replaceAll("[S/\\s,]", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Valida si una cadena es un número válido
     */
    public static boolean isValidNumber(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(numberString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Convierte string a Integer de forma segura
     */
    public static Integer parseInteger(String intString) {
        if (intString == null || intString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Integer.parseInt(intString.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Formatea un porcentaje
     */
    public static String formatPercentage(Double percentage) {
        if (percentage == null) {
            return "0.0%";
        }
        return String.format("%.1f%%", percentage);
    }
    
    /**
     * Trunca un string si excede la longitud máxima
     */
    public static String truncateString(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        
        if (text.length() <= maxLength) {
            return text;
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Obtiene el estado con emoji correspondiente
     */
    public static String getEstadoConEmoji(String estado) {
        if (estado == null) {
            return "";
        }
        
        switch (estado.toUpperCase()) {
            case "PENDIENTE":
                return "⏳ Pendiente";
            case "COMPLETADA":
                return "✅ Completada";
            case "CANCELADA":
                return "❌ Cancelada";
            case "BAJO":
                return "⚠️ Bajo";
            case "ALTO":
                return "⬆️ Alto";
            case "NORMAL":
                return "✅ Normal";
            case "ACTIVO":
                return "✅ Activo";
            case "INACTIVO":
                return "❌ Inactivo";
            default:
                return estado;
        }
    }
    
    /**
     * Calcula el total de una lista de DTOs
     */
    public static BigDecimal calcularTotal(java.util.List<? extends Object> items, 
                                          java.util.function.Function<Object, BigDecimal> getter) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(getter)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Valida si un email tiene formato válido (básico)
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Capitaliza la primera letra de cada palabra
     */
    public static String capitalizeWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}