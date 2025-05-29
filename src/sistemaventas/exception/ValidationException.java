/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excepción personalizada para errores de validación de datos
 * Se lanza cuando los datos de entrada no cumplen con las reglas de validación
 * 
 * Ejemplos de uso:
 * - Formato de email inválido
 * - Número de documento inválido
 * - Campos requeridos faltantes
 * - Valores fuera de rango permitido
 * - Longitud de texto excedida
 *  
 * @version 1.0
 */
public class ValidationException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String field;
    private Object invalidValue;
    private String validationRule;
    private Map<String, List<String>> fieldErrors;
    private List<String> globalErrors;
    
    /**
     * Constructor básico con mensaje
     * @param message Mensaje descriptivo del error de validación
     */
    public ValidationException(String message) {
        super(message);
        this.globalErrors = new ArrayList<>();
        this.globalErrors.add(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Excepción que causó este error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.globalErrors = new ArrayList<>();
        this.globalErrors.add(message);
    }
    
    /**
     * Constructor con campo específico y mensaje
     * @param field Campo que falló la validación
     * @param message Mensaje descriptivo del error
     */
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.fieldErrors = new HashMap<>();
        addFieldError(field, message);
    }
    
    /**
     * Constructor completo con campo, valor inválido y regla
     * @param field Campo que falló la validación
     * @param invalidValue Valor que causó el error
     * @param validationRule Regla de validación violada
     * @param message Mensaje descriptivo del error
     */
    public ValidationException(String field, Object invalidValue, String validationRule, String message) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.validationRule = validationRule;
        this.fieldErrors = new HashMap<>();
        addFieldError(field, message);
    }
    
    /**
     * Constructor con múltiples errores de campo
     * @param fieldErrors Mapa de errores por campo
     */
    public ValidationException(Map<String, List<String>> fieldErrors) {
        super("Errores de validación en múltiples campos");
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
    }
    
    // ============================================
    // GETTERS Y SETTERS
    // ============================================
    
    /**
     * Obtiene el campo que falló la validación
     * @return Nombre del campo o null si es un error global
     */
    public String getField() {
        return field;
    }
    
    /**
     * Establece el campo que falló la validación
     * @param field Nombre del campo
     */
    public void setField(String field) {
        this.field = field;
    }
    
    /**
     * Obtiene el valor que causó la validación fallida
     * @return Valor inválido o null si no se estableció
     */
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    /**
     * Establece el valor inválido
     * @param invalidValue Valor que causó el error
     */
    public void setInvalidValue(Object invalidValue) {
        this.invalidValue = invalidValue;
    }
    
    /**
     * Obtiene la regla de validación que se violó
     * @return Nombre de la regla o null si no se estableció
     */
    public String getValidationRule() {
        return validationRule;
    }
    
    /**
     * Establece la regla de validación violada
     * @param validationRule Nombre de la regla
     */
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }
    
    /**
     * Obtiene todos los errores por campo
     * @return Mapa de errores por campo
     */
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors != null ? fieldErrors : new HashMap<>();
    }
    
    /**
     * Obtiene errores globales (no asociados a un campo específico)
     * @return Lista de errores globales
     */
    public List<String> getGlobalErrors() {
        return globalErrors != null ? globalErrors : new ArrayList<>();
    }
    
    // ============================================
    // MÉTODOS DE GESTIÓN DE ERRORES
    // ============================================
    
    /**
     * Agrega un error a un campo específico
     * @param fieldName Nombre del campo
     * @param errorMessage Mensaje de error
     */
    public void addFieldError(String fieldName, String errorMessage) {
        if (fieldErrors == null) {
            fieldErrors = new HashMap<>();
        }
        
        fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
    }
    
    /**
     * Agrega un error global
     * @param errorMessage Mensaje de error global
     */
    public void addGlobalError(String errorMessage) {
        if (globalErrors == null) {
            globalErrors = new ArrayList<>();
        }
        globalErrors.add(errorMessage);
    }
    
    /**
     * Obtiene errores de un campo específico
     * @param fieldName Nombre del campo
     * @return Lista de errores del campo o lista vacía si no hay errores
     */
    public List<String> getErrorsForField(String fieldName) {
        if (fieldErrors == null || fieldName == null) {
            return new ArrayList<>();
        }
        return fieldErrors.getOrDefault(fieldName, new ArrayList<>());
    }
    
    /**
     * Verifica si un campo específico tiene errores
     * @param fieldName Nombre del campo
     * @return true si el campo tiene errores
     */
    public boolean hasErrorsForField(String fieldName) {
        return !getErrorsForField(fieldName).isEmpty();
    }
    
    /**
     * Verifica si hay errores globales
     * @return true si hay errores globales
     */
    public boolean hasGlobalErrors() {
        return globalErrors != null && !globalErrors.isEmpty();
    }
    
    /**
     * Verifica si hay errores de campo
     * @return true si hay errores de campo
     */
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }
    
    /**
     * Obtiene el número total de errores
     * @return Cantidad total de errores
     */
    public int getTotalErrorCount() {
        int count = 0;
        
        if (hasGlobalErrors()) {
            count += globalErrors.size();
        }
        
        if (hasFieldErrors()) {
            count += fieldErrors.values().stream().mapToInt(List::size).sum();
        }
        
        return count;
    }
    
    /**
     * Obtiene todos los mensajes de error en una lista
     * @return Lista con todos los mensajes de error
     */
    public List<String> getAllErrorMessages() {
        List<String> allErrors = new ArrayList<>();
        
        if (hasGlobalErrors()) {
            allErrors.addAll(globalErrors);
        }
        
        if (hasFieldErrors()) {
            for (Map.Entry<String, List<String>> entry : fieldErrors.entrySet()) {
                for (String error : entry.getValue()) {
                    allErrors.add(entry.getKey() + ": " + error);
                }
            }
        }
        
        return allErrors;
    }
    
    /**
     * Obtiene un resumen de todos los errores
     * @return String con resumen de errores
     */
    public String getErrorSummary() {
        if (getTotalErrorCount() == 0) {
            return "Sin errores de validación";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Errores de validación (").append(getTotalErrorCount()).append("):\n");
        
        List<String> allErrors = getAllErrorMessages();
        for (int i = 0; i < allErrors.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(allErrors.get(i));
            if (i < allErrors.size() - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    // ============================================
    // MÉTODOS ESTÁTICOS DE CONVENIENCIA
    // ============================================
    
    /**
     * Crea una excepción para campo requerido faltante
     * @param fieldName Nombre del campo requerido
     * @return ValidationException configurada
     */
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(fieldName, "El campo " + fieldName + " es requerido");
    }
    
    /**
     * Crea una excepción para formato inválido
     * @param fieldName Nombre del campo
     * @param expectedFormat Formato esperado
     * @return ValidationException configurada
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException(fieldName, 
            String.format("El campo %s tiene formato inválido. Formato esperado: %s", fieldName, expectedFormat));
    }
    
    /**
     * Crea una excepción para valor fuera de rango
     * @param fieldName Nombre del campo
     * @param value Valor proporcionado
     * @param min Valor mínimo permitido
     * @param max Valor máximo permitido
     * @return ValidationException configurada
     */
    public static ValidationException outOfRange(String fieldName, Object value, Object min, Object max) {
        return new ValidationException(fieldName, value, "RANGE_VALIDATION",
            String.format("El campo %s debe estar entre %s y %s. Valor proporcionado: %s", 
                fieldName, min, max, value));
    }
    
    /**
     * Crea una excepción para longitud inválida
     * @param fieldName Nombre del campo
     * @param actualLength Longitud actual
     * @param maxLength Longitud máxima permitida
     * @return ValidationException configurada
     */
    public static ValidationException invalidLength(String fieldName, int actualLength, int maxLength) {
        return new ValidationException(fieldName, actualLength, "LENGTH_VALIDATION",
            String.format("El campo %s excede la longitud máxima. Actual: %d, Máximo: %d", 
                fieldName, actualLength, maxLength));
    }
    
    /**
     * Crea una excepción para email inválido
     * @param email Email que falló la validación
     * @return ValidationException configurada
     */
    public static ValidationException invalidEmail(String email) {
        return new ValidationException("correo", email, "EMAIL_VALIDATION",
            "El formato del correo electrónico es inválido: " + email);
    }
    
    @Override
    public String toString() {
        return getErrorSummary();
    }
}