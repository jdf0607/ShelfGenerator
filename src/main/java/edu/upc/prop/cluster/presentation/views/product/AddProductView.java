package edu.upc.prop.cluster.presentation.views.product;

import edu.upc.prop.cluster.common.Pair;
import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para añadir productos, incluyendo la adición de tags y sus pesos.
 * Esta vista permite al usuario ingresar un nombre para el producto, añadir tags con sus respectivos pesos
 * y ver una lista de los tags sugeridos. También permite eliminar tags de la lista de tags añadidos.
 * @author Jorge Vico Lora
 */
public class AddProductView extends JPanel {
    private final JTextField nameField;
    private final JTextField tagField;
    private final JTextField weightField;

    private final JList<String> tagSuggestions;
    private final DefaultListModel<String> tagListModel;
    private final DefaultListModel<String> addedTagsModel;
    private final JList<String> addedTagsList;

    private final JButton backwardsButton;
    private final JButton addProductButton;
    private final JButton addTagButton;
    private final JButton removeTagButton;

    /**
     * Vista para añadir productos, incluyendo la adición de tags y sus pesos.
     * Esta vista permite al usuario ingresar un nombre para el producto, añadir tags con sus respectivos pesos
     * y ver una lista de los tags sugeridos. También permite eliminar tags de la lista de tags añadidos.
     * @param controller Controlador de la capa de presentación
     */
    public AddProductView(PresentationController controller) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etiqueta para el nombre
        JLabel nameLabel = new JLabel("Nombre del producto:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);

        // Campo de texto para el nombre
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(nameField, gbc);

        // Etiqueta para los tags
        JLabel tagLabel = new JLabel("Añadir tags:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(tagLabel, gbc);

        // Campo de texto para tags
        tagField = new JTextField(10);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(tagField, gbc);

        // Campo de texto para pesos
        JLabel weightLabel = new JLabel("Peso del tag:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(weightLabel, gbc);

        weightField = new JTextField(5);
        gbc.gridx = 1;
        add(weightField, gbc);

        // Botón para añadir tag
        addTagButton = new JButton("Añadir Tag");
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(addTagButton, gbc);

        // Botón para eliminar tag
        removeTagButton = new JButton("Eliminar Tag");
        gbc.gridx = 2;
        gbc.gridy = 4;
        add(removeTagButton, gbc);

        // Lista de sugerencias de tags
        JLabel suggestionsLabel = new JLabel("Tags sugeridos:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        add(suggestionsLabel, gbc);

        tagListModel = new DefaultListModel<>();
        tagSuggestions = new JList<>(tagListModel);
        tagSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Agregar listener al tagList
        tagSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Un solo clic
                    int index = tagSuggestions.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedSuggestion = tagListModel.getElementAt(index);
                        tagField.setText(selectedSuggestion);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tagSuggestions);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Lista de tags añadidos
        JLabel addedTagsLabel = new JLabel("Tags añadidos:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        add(addedTagsLabel, gbc);

        addedTagsModel = new DefaultListModel<>();
        addedTagsList = new JList<>(addedTagsModel);
        JScrollPane addedTagsScrollPane = new JScrollPane(addedTagsList);
        addedTagsScrollPane.setPreferredSize(new Dimension(200, 100));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(addedTagsScrollPane, gbc);

        // Botón para regresar
        backwardsButton = new JButton("<");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(backwardsButton, gbc);

        // Botón para añadir producto
        addProductButton = new JButton("Añadir producto");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        add(addProductButton, gbc);

        // Acción para el botón de retroceder
        backwardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                setVisible(false);
            }
        });

        // Acción para el botón de añadir producto
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(AddProductView.this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<Pair<String, Double>> tagsWithWeights = new ArrayList<>();
                for (int i = 0; i < addedTagsModel.size(); i++) {
                    String[] tagData = addedTagsModel.get(i).split(" - ");
                    tagsWithWeights.add(new Pair<>(tagData[0], Double.parseDouble(tagData[1])));
                }

                Either<String, ProductDTO> result = controller.handleAddProduct(name, tagsWithWeights);

                result.fold(
                        errorMessage -> { //todo: quizas podemos mover el error de aqui al presentation controller
                            JOptionPane.showMessageDialog(AddProductView.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                            },
                        productDTO -> {
                            JOptionPane.showMessageDialog(AddProductView.this, "Producto añadido con éxito: " + productDTO.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            clearAll();
                            return null;
                        }
                );
            }
        });

        // Acción para el botón de añadir tag
        addTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tag = tagField.getText().trim();
                String weightText = weightField.getText().trim();

                if (tag.isEmpty()) {
                    JOptionPane.showMessageDialog(AddProductView.this, "El tag no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(tag.length() > 15) {
                    JOptionPane.showMessageDialog(AddProductView.this, "Número máximo de caracteres alcanzado. (15)", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (int i = 0; i < addedTagsModel.size(); i++) {
                    String[] tagData = addedTagsModel.get(i).split(" - ");
                    if (tagData[0].equals(tag)) {
                        JOptionPane.showMessageDialog(AddProductView.this, "No se puede añadir dos veces el tag", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (weightText.isEmpty()) {
                    weightText = "1.0";
                }

                try {
                    double weight = Double.parseDouble(weightText);
                    addedTagsModel.addElement(tag + " - " + weight);
                    tagField.setText("");
                    weightField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AddProductView.this, "El peso debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para el botón de eliminar tag
        removeTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = addedTagsList.getSelectedIndex();

                if (selectedIndex != -1) {
                    addedTagsModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(AddProductView.this, "Seleccione un tag para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Escuchar cambios en el campo de texto de tags
        tagField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTagSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTagSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTagSuggestions();
            }
            /*
             * Actualiza las sugerencias de tags en función del texto introducido en el campo de texto.
             *
             * El método se llama cuando se produce un evento de cambio en el documento de texto,
             * insertar un carácter o eliminar un carácter.
             *
             * En este caso, se utiliza una lista de tags para simular la obtención de sugerencias
             * y se agregan a la lista de visualización.
             *
             */
            private void updateTagSuggestions() {
                String input = tagField.getText().trim().toLowerCase();
                tagListModel.clear();

                if (!input.isEmpty()) {
                    List<String> suggestions = controller.getTagsByPrefix(input);

                    for (String suggestion : suggestions) {
                        tagListModel.addElement(suggestion);
                    }
                }
            }
        });
    }

    /*
     * Limpia todos los campos y listas de la vista.
     * Este método elimina todos los tags de la lista de tags sugeridos, la lista de tags añadidos,
     * y borra el contenido de los campos de texto para el nombre del producto, el tag y el peso.
     */
    private void clearAll() {
        tagListModel.clear();
        addedTagsModel.clear();

        addedTagsList.setSelectedIndex(-1);

        nameField.setText("");
        weightField.setText("");
        tagField.setText("");

    }
}




