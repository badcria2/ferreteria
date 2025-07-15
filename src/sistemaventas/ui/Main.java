package sistemaventas.ui;

 

import javax.swing.*; 

import javax.swing.SwingUtilities; 

public class Main {
    public static void main(String[] args) {
        // Inicia la interfaz gráfica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Crea y muestra la ventana principal
                MainDashboard mainFrame = new MainDashboard();
                mainFrame.setVisible(true);
            }
        });
    }
}