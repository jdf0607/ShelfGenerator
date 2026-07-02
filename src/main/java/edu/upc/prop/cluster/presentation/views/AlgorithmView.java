package edu.upc.prop.cluster.presentation.views;

import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;


/**
 * Vista que permite al usuario seleccionar un tipo de algoritmo y generar una estantería.
 * La vista ofrece dos opciones de algoritmos (aproximación o fuerza bruta) y dos botones para navegar y generar la estantería.
 * @author Jorge Vico Lora
 */
public class AlgorithmView extends JPanel {
    private JButton backwardsButton;
    private JButton generateButton;


    /**
     * Constructor de la vista de algoritmos.
     * Inicializa los componentes de la vista, incluyendo los botones, los checkboxes y su disposición en la interfaz.
     *
     * @param controller El controlador que gestiona la lógica de la aplicación.
     */
    public AlgorithmView(PresentationController controller) {
        setLayout(new GridBagLayout());  // Usamos GridBagLayout

        // Crear un GridBagConstraints para controlar la disposición de los componentes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Añadir margen entre los componentes

        // Crear una etiqueta
        JLabel label = new JLabel("Selecciona el tipo de algoritmo");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // Hacer que la etiqueta ocupe dos columnas
        add(label, gbc);

        // Crear dos checkboxes
        JCheckBox checkBox1 = new JCheckBox("Algoritmo de aproximación (rápido)");
        checkBox1.setSelected(true);
        JCheckBox checkBox2 = new JCheckBox("Algoritmo de fuerza bruta (muy lento)");

        // Configurar la disposición de los checkboxes
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;  // Los checkboxes ocupan dos columnas
        add(checkBox1, gbc);

        gbc.gridy = 2;
        add(checkBox2, gbc);

        // Crear un ButtonGroup para asegurar que solo uno de los checkboxes pueda ser seleccionado
        ButtonGroup group = new ButtonGroup();
        group.add(checkBox1);
        group.add(checkBox2);

        // Botón para regresar
        backwardsButton = new JButton("<");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;  // Hacer que el botón ocupe solo una columna
        add(backwardsButton, gbc);

        // Botón para generar la estantería
        generateButton = new JButton("Generar estantería");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(generateButton, gbc);

        // Acción para el botón de retroceder
        backwardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);  // Ocultar la vista de algoritmos
            }
        });

        // Acción para el botón de generar
        generateButton.addActionListener(e -> {
            if (!checkBox1.isSelected()) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que " +
                        "deseas usar el algoritmo de fuerza bruta? El tiempo estimado para 15 productos " +
                        "es de 2 horas.", "Confirmación", JOptionPane.YES_NO_OPTION);
                if(!(confirm == JOptionPane.YES_OPTION)) {
                    return;
                }
            }
            // Mostrar ventana de carga
            JDialog loadingDialog = new JDialog((Frame) null, "Cargando...", true);
            JLabel loadingLabel = new JLabel("Generando estantería, por favor espere...");
            loadingDialog.add(BorderLayout.CENTER, loadingLabel);
            loadingDialog.setSize(300, 100);
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            loadingDialog.add(new JLabel("   Generando estanteria, espera por favor."));

            new Thread(() -> {
                if (checkBox1.isSelected()) {
                    controller.executeProAlgorithm();
                } else {
                    controller.executeBacktracking();
                }
                SwingUtilities.invokeLater(() -> loadingDialog.dispose());
                setVisible(false);
            }).start();
            loadingDialog.setVisible(true);

        });
    }
}


