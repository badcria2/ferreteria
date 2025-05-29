/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.exception;

/**
 * Excepción personalizada para errores de lógica de negocio
 * Se lanza cuando se viola una regla de negocio específica del sistema
 * 
 * Ejemplos de uso:
 * - Documento ya registrado
 * - Stock insuficiente para venta
 * - Venta no puede ser modificada en su estado actual
 * - Cliente no encontrado
 * - Producto ya existe en el almacén 
 * @version 1.0
 */
public class BusinessException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    private String userMessage;
    private Object[] parameters;
    
    /**
     * Constructor básico con mensaje
     * @param message Mensaje descriptivo del error de negocio
     */
    public BusinessException(String message) {
        super(message);
        this.userMessage = message;
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Excepción que causó este error
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.userMessage = message;
    }
    
    /**
     * Constructor con código de error y mensaje
     * @param errorCode Código identificador del error
     * @param message Mensaje descriptivo del error
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = message;
    }
    
    /**
     * Constructor completo con código, mensaje, causa y parámetros
     * @param errorCode Código identificador del error
     * @param message Mensaje descriptivo del error
     * @param cause Excepción que causó este error
     * @param parameters Parámetros adicionales para el mensaje
     */
    public BusinessException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = message;
        this.parameters = parameters;
    }
    
    /**
     * Constructor con mensaje parametrizado
     * @param message Mensaje con placeholders
     * @param parameters Parámetros para reemplazar en el mensaje
     */
    public BusinessException(String message, Object... parameters) {
        super(formatMessage(message, parameters));
        this.userMessage = formatMessage(message, parameters);
        this.parameters = parameters;
    }
    
    // ============================================
    // GETTERS Y SETTERS
    // ============================================
    
    /**
     * Obtiene el código de error específico
     * @return Código de error o null si no se estableció
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Establece el código de error
     * @param errorCode Código identificador del error
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * Obtiene el mensaje amigable para el usuario
     * @return Mensaje para mostrar al usuario
     */
    public String getUserMessage() {
        return userMessage != null ? userMessage : getMessage();
    }
    
    /**
     * Establece el mensaje para el usuario
     * @param userMessage Mensaje amigable para el usuario
     */
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    /**
     * Obtiene los parámetros adicionales del error
     * @return Array de parámetros o null si no hay
     */
    public Object[] getParameters() {
        return parameters;
    }
    
    /**
     * Establece parámetros adicionales
     * @param parameters Parámetros del error
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    
    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================
    
    /**
     * Verifica si la excepción tiene un código de error específico
     * @param code Código a verificar
     * @return true si coincide con el código de error
     */
    public boolean hasErrorCode(String code) {
        return errorCode != null && errorCode.equals(code);
    }
    
    /**
     * Verifica si la excepción tiene parámetros adicionales
     * @return true si tiene parámetros
     */
    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }
    
    /**
     * Obtiene un resumen completo de la excepción
     * @return String con toda la información del error
     */
    public String getFullErrorInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("BusinessException: ");
        
        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }
        
        sb.append(getUserMessage());
        
        if (hasParameters()) {
            sb.append(" | Parámetros: ");
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameters[i]);
            }
        }
        
        if (getCause() != null) {
            sb.append(" | Causa: ").append(getCause().getMessage());
        }
        
        return sb.toString();
    }
    
    // ============================================
    // MÉTODOS ESTÁTICOS DE CONVENIENCIA
    // ============================================
    
    /**
     * Crea una excepción para cuando un registro no se encuentra
     * @param entityName Nombre de la entidad
     * @param id ID del registro buscado
     * @return BusinessException configurada
     */
    public static BusinessException notFound(String entityName, Object id) {
        return new BusinessException("ENTITY_NOT_FOUND", 
            String.format("%s no encontrado con ID: %s", entityName, id));
    }
    
    /**
     * Crea una excepción para cuando ya existe un registro duplicado
     * @param entityName Nombre de la entidad
     * @param field Campo que está duplicado
     * @param value Valor duplicado
     * @return BusinessException configurada
     */
    public static BusinessException alreadyExists(String entityName, String field, Object value) {
        return new BusinessException("DUPLICATE_ENTITY", 
            String.format("Ya existe un %s con %s: %s", entityName, field, value));
    }
    
    /**
     * Crea una excepción para operaciones no permitidas
     * @param operation Operación que se intentó realizar
     * @param reason Razón por la que no está permitida
     * @return BusinessException configurada
     */
    public static BusinessException operationNotAllowed(String operation, String reason) {
        return new BusinessException("OPERATION_NOT_ALLOWED", 
            String.format("Operación '%s' no permitida: %s", operation, reason));
    }
    
    /**
     * Crea una excepción para recursos insuficientes
     * @param resource Recurso insuficiente
     * @param available Cantidad disponible
     * @param requested Cantidad solicitada
     * @return BusinessException configurada
     */
    public static BusinessException insufficientResource(String resource, Object available, Object requested) {
        return new BusinessException("INSUFFICIENT_RESOURCE", 
            String.format("%s insuficiente. Disponible: %s, Solicitado: %s", resource, available, requested));
    }
    
    // ============================================
    // MÉTODOS PRIVADOS
    // ============================================
    
    /**
     * Formatea un mensaje con parámetros
     * @param message Mensaje con placeholders %s
     * @param parameters Parámetros para reemplazar
     * @return Mensaje formateado
     */
    private static String formatMessage(String message, Object... parameters) {
        if (message == null) return "Error de negocio";
        if (parameters == null || parameters.length == 0) return message;
        
        try {
            return String.format(message, parameters);
        } catch (Exception e) {
            return message + " (Error al formatear parámetros)";
        }
    }
    
    @Override
    public String toString() {
        return getFullErrorInfo();
    }
}
