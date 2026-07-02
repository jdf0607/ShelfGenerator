package edu.upc.prop.cluster.presentation.views.product;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.presentation.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Vista para editar un producto. Permite cambiar el nombre del producto, editar o eliminar tags
 * asociados al producto, y eliminar el producto en sí.
 * Esta vista también permite agregar nuevos tags al producto.
 *
 * @author Alex Meca Moñino
 */
public class EditProductView extends JPanel {
    String name;
    Map<String,Double> tags;
    PresentationController controller;


    /**
     * Constructor de la vista para editar el producto.
     *
     * @param p El objeto ProductDTO que contiene los detalles del producto a editar.
     * @param controller El controlador de presentación que maneja las interacciones.
     */
    public EditProductView(ProductDTO p, PresentationController controller) {
        name = p.getName();
        tags = p.getTags();
        this.controller = controller;
        setLayout(new GridBagLayout());
        generate();


    }


    /*
     * Crea el botón para regresar a la vista anterior.
     *
     * @return El botón de regresar.
     */
    private JButton backButton() {
        JButton backButton = new JButton("<");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        return backButton;
    }


    /*
     * Actualiza el nombre del producto.
     * Muestra un mensaje de error si la operación falla.
     *
     * @param newname El nuevo nombre del producto.
     */
    private void updateName(String newname) {
        Either<String, ProductDTO> result = controller.editProductName(this.name, newname);
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                _ -> {
                    name = newname;
                    generate();
                    return null;
                }
        );
    }


    /*
     * Elimina un tag del producto.
     * Muestra un mensaje de error si la operación falla.
     *
     * @param tag El nombre del tag a eliminar.
     */
    private void removeTag(String tag){
        Either<String, Boolean> result = controller.removeTagFromProduct(this.name, tag);
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                _ -> {
                    tags.remove(tag);
                    generate();
                    return null;
                }
        );
    }


    /*
     * Añade un nuevo tag al producto.
     * Muestra un mensaje de error si la operación falla.
     *
     * @param tag El nombre del nuevo tag.
     * @param weight El peso asociado al tag.
     */
    private void addTag(String tag, Double weight){
        Either<String, ProductDTO> result = controller.addTagToProduct(this.name, tag, weight);
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                _ -> {
                    tags.put(tag,weight);
                    generate();
                    return null;
                }
        );
    }


    /*
     * Edita el peso de un tag asociado al producto.
     * Muestra un mensaje de error si la operación falla.
     *
     * @param tag El nombre del tag a editar.
     * @param newweight El nuevo peso del tag.
     */
    private void editTag(String tag, Double newweight) {
        Either<String, ProductDTO> result = controller.editTagFromProduct(this.name, tag, newweight);
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                _ -> {
                    tags.put(tag,newweight);
                    generate();
                    return null;
                }
        );
    }


    /*
     * Elimina el producto.
     * Muestra un mensaje de error si la operación falla.
     */
    private void deleteProduct() {

        Either<String, Boolean> result = controller.removeProduct(this.name);
        result.fold(
                errorMessage -> {
                    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                },
                _ -> {
                    setVisible(false);
                    return null;
                }
        );
    }


    /*
     * Genera la interfaz gráfica de la vista, incluyendo el nombre del producto, botones para editar,
     * eliminar el producto y los tags, así como un botón para regresar a la vista anterior.
     */
    private void generate() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        removeAll();

        //Name
        JLabel nameLabel = new JLabel(this.name);
        nameLabel.setFont(new Font("Serif", Font.PLAIN, 48));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(nameLabel, gbc);

        //NameButtons
        JPanel nameButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEditName = new JButton("Editar Nombre");
        btnEditName.addActionListener(e -> {
            String newname = JOptionPane.showInputDialog(this, "Ingrese el nuevo nombre:", this.name);
            if (newname != null) {
                updateName(newname);
            }
        });

        JButton btnDelete = new JButton("Eliminar producto");
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto? Esta acción es irreversible.", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteProduct();
            }
        });

        nameButtonPanel.add(btnEditName);
        nameButtonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(nameButtonPanel, gbc);

        //TagsPanel
        JPanel tagsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints tagsGbc = new GridBagConstraints();
        tagsGbc.insets = new Insets(5, 5, 5, 5);
        tagsGbc.fill = GridBagConstraints.HORIZONTAL;

        int tagRow = 0;
        int tagCol = 0;
        for (Map.Entry<String, Double> value : tags.entrySet()) {
            JLabel label = new JLabel(value.getKey() + ": " + value.getValue());
            tagsGbc.gridx = tagCol * 3;
            tagsGbc.gridy = tagRow;
            tagsGbc.gridwidth = 1;
            tagsPanel.add(label, tagsGbc);

            JButton btnEditar = new JButton("Editar");
            btnEditar.addActionListener(e -> {
                String nuevoValorStr = JOptionPane.showInputDialog(this, "Ingrese el nuevo valor:", value.getValue());
                if (nuevoValorStr != null) {
                    try {
                        double nuevoValor = Double.parseDouble(nuevoValorStr);
                        label.setText(value.getKey() + ": " + nuevoValor);
                        editTag(value.getKey(), nuevoValor);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            tagsGbc.gridx = tagCol * 3 + 1;
            tagsPanel.add(btnEditar, tagsGbc);

            JButton btnEliminar = new JButton("Eliminar");
            btnEliminar.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    removeTag(value.getKey());
                }
            });

            tagsGbc.gridx = tagCol * 3 + 2;
            tagsPanel.add(btnEliminar, tagsGbc);

            tagCol++;
            if (tagCol == 5) {
                tagCol = 0;
                tagRow++;
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(tagsPanel, gbc);

        //newTag
        JButton btnAdd = new JButton("Añadir nueva tag");
        btnAdd.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Ingrese el nombre del tag:");
            if (name != null && !name.isEmpty()) {
                String weightStr = JOptionPane.showInputDialog(this, "Ingrese el peso:");
                if (weightStr != null) {
                    try {
                        double weight = Double.parseDouble(weightStr);
                        addTag(name, weight);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Ocupa dos columnas
        add(btnAdd, gbc);

        // Botón para regresar (colocado en la parte inferior)
        JButton backButton = backButton();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Ocupa dos columnas
        add(backButton, gbc);

        revalidate();
        repaint();
    }
}
