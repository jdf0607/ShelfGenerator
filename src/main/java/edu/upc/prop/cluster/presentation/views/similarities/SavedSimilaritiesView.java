package edu.upc.prop.cluster.presentation.views.similarities;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;


/**
 * Vista para mostrar las similitudes guardadas entre productos.
 * Permite visualizar, eliminar y gestionar las similitudes entre productos ya existentes.
 * Utiliza una {@link JList} para mostrar las similitudes y un {@link JButton} para retroceder a la vista anterior.
 *
 * @author Jorge Vico Lora
 */
public class SavedSimilaritiesView extends JPanel {
    private JButton backwardsButton;
    private JList<String> similaritiesList;
    private DefaultListModel<String> listModel;
    private PresentationController controller;
    private JLabel instructionLabel;


    /**
     * Constructor de la clase {@link SavedSimilaritiesView}.
     * Inicializa la vista con los componentes necesarios para mostrar las similitudes guardadas entre productos.
     *
     * @param controller El controlador de presentación utilizado para interactuar con la lógica de negocio.
     */
    public SavedSimilaritiesView(PresentationController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        instructionLabel = new JLabel("Haz click para eliminar la similitud");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(instructionLabel, gbc);

        backwardsButton = new JButton("<");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(backwardsButton, gbc);

        // Acción para el botón de retroceder
        backwardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        listModel = new DefaultListModel<>();
        similaritiesList = new JList<>(listModel);
        similaritiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        similaritiesList.setVisibleRowCount(10);  // Mostrar hasta 10 filas

        JScrollPane listScroller = new JScrollPane(similaritiesList);
        listScroller.setPreferredSize(new Dimension(300, 150));  // Ajustar tamaño de la lista
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(listScroller, gbc);

        similaritiesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedValue = similaritiesList.getSelectedValue();
                    if (selectedValue != null) {
                        String[] parts = selectedValue.split(":")[0].split(" - ");
                        String productName1 = parts[0];
                        String productName2 = parts[1];
                        removeSimilarity(productName1, productName2);
                    }
                }
            }
        });

        loadSimilarities();
    }


    /**
     * Carga las similitudes guardadas entre productos desde el controlador y las muestra en la lista.
     * Si ocurre un error, muestra un mensaje en un cuadro de diálogo.
     */
    public void loadSimilarities() {
        listModel.clear();
        // Obtener todos los productos
        Either<String, Map<Pair<String, String>, Double>> result = controller.getSavedSimilarities();
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(SavedSimilaritiesView.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                similarities -> {
                    for (Pair<String, String> pair : similarities.keySet()) {
                        listModel.addElement(pair.first() + " - " + pair.second() + ": " + similarities.get(pair));
                    }
                    return null;
                }
        );
    }


    /*
     * Elimina una similitud entre dos productos específicos.
     * Muestra un mensaje de éxito o error según el resultado de la operación.
     *
     * @param productName1 El nombre del primer producto de la similitud a eliminar.
     * @param productName2 El nombre del segundo producto de la similitud a eliminar.
     */
    private void removeSimilarity(String productName1, String productName2) {
        controller.removeSimilarity(productName1, productName2).fold(
                error -> {
                    JOptionPane.showMessageDialog(this, error);
                    return null;
                },
                success -> {
                    JOptionPane.showMessageDialog(SavedSimilaritiesView.this, "Similitiud elimianda ", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadSimilarities();
                    return null;
                }
        );
    }
}
