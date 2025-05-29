/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.util;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern DOCUMENT_PATTERN = 
        Pattern.compile("^[0-9]{8,12}$");
    
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\+]?[0-9]{9,15}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isValidDocument(String document) {
        return document != null && DOCUMENT_PATTERN.matcher(document.trim()).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }
    
    public static boolean isValidQuantity(Integer quantity) {
        return quantity != null && quantity > 0;
    }
    
    public static String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("\\s+", " ");
    }
}