package edu.upc.prop.cluster.presentation.views;

import edu.upc.prop.cluster.presentation.MainFrame;
import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Hashtable;
import java.util.Objects;
import java.util.prefs.Preferences;


/**
 * Esta clase representa la vista de configuración del sistema. Permite al usuario cambiar
 * la configuración de la aplicación, como el número máximo de tags, el modo de visualización
 * (claro/oscuro), y ofrece botones para guardar o borrar datos. También muestra un mensaje de
 * copyright.
 *
 * @author Jorge Vico Lora
 */
public class SettingsView extends JPanel {

    /**
     * Constructor de la vista de configuración.
     * Configura los componentes de la interfaz de usuario, incluyendo los sliders, botones
     * y etiquetas necesarias para gestionar las configuraciones.
     *
     * @param controller El controlador de presentación que maneja las acciones del usuario
     */
    public SettingsView(PresentationController controller, MainFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margen entre los componentes

        // Etiqueta de título
        JLabel titleLabel = new JLabel("Configuración", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Abarca dos columnas
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // 1. Slider para establecer maxTag (3 a 20)
        JLabel maxTagLabel = new JLabel("Número máximo de tags:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Solo una columna
        gbc.anchor = GridBagConstraints.WEST;
        add(maxTagLabel, gbc);

        Preferences prefs = Preferences.userRoot().node("com.shelfgenerator");
        int tagLimitPref = prefs.getInt("tagLimit", 10);

        JSlider maxTagSlider = new JSlider(3, 20, tagLimitPref); // Valor inicial 10
        maxTagSlider.setPaintTicks(true);
        maxTagSlider.setPaintLabels(true);
        maxTagSlider.setMinorTickSpacing(1);

        // Personalizar las etiquetas en el slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(3, new JLabel("3"));
        labelTable.put(5, new JLabel("5"));
        labelTable.put(10, new JLabel("10"));
        labelTable.put(15, new JLabel("15"));
        labelTable.put(20, new JLabel("20"));
        maxTagSlider.setLabelTable(labelTable);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expandir horizontalmente
        add(maxTagSlider, gbc);

        maxTagSlider.addChangeListener(e -> {
            if (!maxTagSlider.getValueIsAdjusting()) {
                int valorSeleccionado = maxTagSlider.getValue();
                controller.editMaxTag(valorSeleccionado);
                prefs.putInt("tagLimit", valorSeleccionado);
                JOptionPane.showMessageDialog(
                        this,
                        "El número máximo de tags se ha cambiado a: " + valorSeleccionado,
                        "Cambio de Configuración",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // 2. Botón para cambiar modo claro/oscuro
        JButton themeButton = new JButton("Cambiar a Modo Claro");
        String mode = prefs.get("mode", "light");
        if (mode.equals("light")) {
            themeButton.setText("Cambiar a Modo Oscuro");
        }
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Abarca dos columnas
        gbc.anchor = GridBagConstraints.CENTER;
        add(themeButton, gbc);

        themeButton.addActionListener(new ActionListener() {
            private boolean isDarkMode = frame.getTheme();

            @Override
            public void actionPerformed(ActionEvent e) {
                isDarkMode = !isDarkMode;
                themeButton.setText(isDarkMode ? "Cambiar a Modo Claro" : "Cambiar a Modo Oscuro");
                frame.toggleTheme("SettingsView");
            }
        });


        // 3. Botón de guardado
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton selectFileButton = new JButton("Cargar Archivo");
        add(selectFileButton, gbc);
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear un JFileChooser
                String appPath = new File("").getAbsolutePath(); // Ruta del ejecutable
                JFileChooser fileChooser = new JFileChooser(new File(appPath));
                int result = fileChooser.showOpenDialog(null);

                // Verificar si se seleccionó un archivo
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getName();
                    if (fileName.endsWith(".shelfgenerator") || fileName.endsWith(".backup")) {
                        System.out.println("Ruta del archivo: " + selectedFile.getAbsolutePath());
                        try {
                            controller.createSave();
                            File destination = new File(appPath, "main.shelfgenerator");
                            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Archivo copiado exitosamente a: " + destination.getAbsolutePath());
                            controller.loadFromJSON();
                        } catch (IOException ex) {
                            System.err.println("Error al copiar el archivo: " + ex.getMessage());
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Archivo no válido. Seleccione un archivo con extensión .shelfgenerator o .backup.",
                                "Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        // 4. Botón de guardado
        JButton saveButton = new JButton("Guardar Todo");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Se guardará todo. \u00bfDeseas continuar?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.save();
                JOptionPane.showMessageDialog(null, "Se ha guardado todo.");
            }
        });

        // 5. Botón de borrar todo con advertencia y copia de seguridad
        JButton deleteButton = new JButton("Borrar Todo");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(deleteButton, gbc);

        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Se realizará una copia de seguridad antes de borrar todo. \u00bfDeseas continuar?",
                    "Confirmar Borrado",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.createSave();
                controller.clearAll();
                JOptionPane.showMessageDialog(null, "Se ha creado una copia de seguridad y se han borrado los datos.");
            }
        });

        // 6. Copyright
        JLabel copyrightLabel = new JLabel("\u00a9 2024 Proyecto ShelfGenerator. Todos los derechos reservados.", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2; // Abarca dos columnas
        gbc.anchor = GridBagConstraints.CENTER;
        add(copyrightLabel, gbc);
    }
}
