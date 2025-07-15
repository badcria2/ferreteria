package sistemaventas.ui;

import com.toedter.calendar.JDateChooser;
import sistemaventas.dto.VentaDTO;
import sistemaventas.service.impl.VentaServiceImpl;
import sistemaventas.service.interfaces.IVentaService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class DialogoReporteVentasPeriodo extends JDialog {

    private IVentaService ventaService;
    private JDateChooser dateChooserInicio, dateChooserFin;
    private JTable tablaReporte;
    private DefaultTableModel modelReporte;
    private JTextArea areaResumen;

    public DialogoReporteVentasPeriodo(Frame parent) {
        super(parent, "Reporte de Ventas por Período", true);
        this.ventaService = new VentaServiceImpl();
        initUI();
    }

    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Controles (Norte) ---
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelControles.setBorder(new TitledBorder("Seleccione Período"));
        
        panelControles.add(new JLabel("Fecha Inicio:"));
        dateChooserInicio = new JDateChooser();
        dateChooserInicio.setPreferredSize(new Dimension(120, 25));
        panelControles.add(dateChooserInicio);
        
        panelControles.add(new JLabel("Fecha Fin:"));
        dateChooserFin = new JDateChooser();
        dateChooserFin.setPreferredSize(new Dimension(120, 25));
        panelControles.add(dateChooserFin);
        
        JButton btnGenerar = new JButton("Generar Reporte");
        panelControles.add(btnGenerar);

        // --- Panel de Resultados (Centro) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.7);

        String[] columnas = {"ID", "Fecha", "Cliente", "Total", "Estado"};
        modelReporte = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReporte = new JTable(modelReporte);
        splitPane.setTopComponent(new JScrollPane(tablaReporte));

        areaResumen = new JTextArea();
        areaResumen.setEditable(false);
        areaResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResumen.setBorder(new TitledBorder("Resumen del Período"));
        splitPane.setBottomComponent(new JScrollPane(areaResumen));
        
        add(panelControles, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        btnGenerar.addActionListener(e -> generarReporte());
    }

    private void generarReporte() {
        if (dateChooserInicio.getDate() == null || dateChooserFin.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha de inicio y fin.", "Datos Faltantes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaInicio = dateChooserInicio.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fechaFin = dateChooserFin.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (fechaInicio.isAfter(fechaFin)) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio no puede ser posterior a la fecha de fin.", "Fechas Inválidas", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Nota: Sería más eficiente tener un método en el servicio que acepte las fechas.
            // Por ahora, seguimos la lógica de la consola: traer todo y filtrar en Java.
            List<VentaDTO> todasLasVentas = ventaService.listarVentas();
            List<VentaDTO> ventasPeriodo = todasLasVentas.stream()
                .filter(venta -> !venta.getFecha().isBefore(fechaInicio) && !venta.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());
            
            modelReporte.setRowCount(0);
            if (ventasPeriodo.isEmpty()) {
                areaResumen.setText("No se encontraron ventas en el período seleccionado.");
                return;
            }
            
            for (VentaDTO venta : ventasPeriodo) {
                modelReporte.addRow(new Object[]{
                    venta.getIdVentas(),
                    venta.getFecha(),
                    venta.getNombreCliente(),
                    venta.getTotalFormateado(),
                    venta.getEstadoDescripcion()
                });
            }
            
            generarResumen(ventasPeriodo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarResumen(List<VentaDTO> ventas) {
        BigDecimal totalGeneral = BigDecimal.ZERO;
        BigDecimal totalCompletadas = BigDecimal.ZERO;
        long ventasCompletadas = 0;
        long ventasCanceladas = 0;
        long ventasPendientes = 0;

        for (VentaDTO venta : ventas) {
            if (venta.isCompletada()) {
                ventasCompletadas++;
                totalCompletadas = totalCompletadas.add(venta.getTotal());
            } else if (venta.isCancelada()) {
                ventasCanceladas++;
            } else if (venta.isPendiente()) {
                ventasPendientes++;
            }
        }
        totalGeneral = totalCompletadas;

        BigDecimal promedioVenta = BigDecimal.ZERO;
        if (ventasCompletadas > 0) {
            promedioVenta = totalCompletadas.divide(BigDecimal.valueOf(ventasCompletadas), 2, BigDecimal.ROUND_HALF_UP);
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("===========================================\n");
        resumen.append("              RESUMEN DEL PERÍODO\n");
        resumen.append("===========================================\n");
        resumen.append(String.format("Total de ventas:      %d\n", ventas.size()));
        resumen.append(String.format("├─ Completadas:        %d (S/ %.2f)\n", ventasCompletadas, totalCompletadas));
        resumen.append(String.format("├─ Pendientes:         %d\n", ventasPendientes));
        resumen.append(String.format("└─ Canceladas:         %d\n", ventasCanceladas));
        resumen.append("-------------------------------------------\n");
        resumen.append(String.format("INGRESO TOTAL:          S/ %.2f\n", totalGeneral));
        resumen.append(String.format("Promedio por Venta:     S/ %.2f\n", promedioVenta));
        
        areaResumen.setText(resumen.toString());
    }
}