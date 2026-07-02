package edu.upc.prop.cluster.presentation.views.tag;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.dto.TagDTO;
import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Vista que permite agregar una nueva etiqueta (tag) al sistema.
 * La vista contiene un campo de texto para ingresar el nombre de la etiqueta y botones para agregar la etiqueta o regresar.
 * @author José Durán Foix
 */
public class AddTagView extends JPanel {
    private final JTextField nameField;

    private final JButton backwardsButton;
    private final JButton addTagButton;


    /**
     * Constructor de la vista para agregar una nueva etiqueta.
     * Configura el diseño de la interfaz de usuario, incluyendo los botones y el campo de texto.
     *
     * @param controller El controlador que gestiona la lógica de la aplicación.
     */
    public AddTagView(PresentationController controller) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etiqueta para el nombre
        JLabel nameLabel = new JLabel("Nombre de la tag:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);

        // Campo de texto para el nombre
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(nameField, gbc);

        //boton para regresar
        backwardsButton = new JButton("<");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(backwardsButton, gbc);

        //boton para añadir tags
        addTagButton = new JButton("Añadir Tag");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        add(addTagButton, gbc);

        // Acción para el botón de retroceder
        backwardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                setVisible(false);
            }
        });


        addTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(AddTagView.this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(name.length() > 15) {
                    JOptionPane.showMessageDialog(AddTagView.this, "Número máximo de caracteres alcanzado. (15)", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TagDTO tag = new TagDTO(name);
                Either<String, TagDTO> result = controller.handleAddTag(tag);

                result.fold(
                        errorMessage -> {
                            JOptionPane.showMessageDialog(AddTagView.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                        },
                        tagDTO -> {
                            JOptionPane.showMessageDialog(AddTagView.this, "Tag añadido con éxito: " + tagDTO.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            clearAll();
                            return null;
                        }
                );
            }
        });
    }

    /*
     * Limpia el campo de texto donde se ingresa el nombre de la tag.
     */
    private void clearAll() {

        nameField.setText("");

    }
}
