package edu.upc.prop.cluster.presentation.cards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;


/**
 * Clase que representa una tarjeta de estante en la interfaz gráfica.
 * La tarjeta muestra el nombre del producto y cambia de color dependiendo de su estado
 * (por ejemplo, cuando el ratón pasa por encima o cuando está seleccionada).
 * @author Alex Meca Moñino
 */
public class ShelfCard extends JPanel {
    private String name;
    private JButton eliminar;
    public static Color defaultColor = Color.LIGHT_GRAY;
    public static Color hoverColor = Color.ORANGE;
    public static Color selectedColor = Color.GREEN;

    /**
     * Constructor que inicializa la tarjeta de estante con el nombre del producto.
     *
     * @param productName El nombre del producto que se mostrará en la tarjeta.
     */
    public ShelfCard(String productName) {
        name = productName;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setBackground(defaultColor);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(new JLabel(name));
        eliminar = new JButton("Eliminar");
        eliminar.setVisible(false);
        add(eliminar);
    }

    public static void toggleMode(boolean isDarkMode) {
        if (!isDarkMode) {
            defaultColor = Color.LIGHT_GRAY;
            hoverColor = Color.ORANGE;
            selectedColor = Color.GREEN;
        }
        else {
            defaultColor = Color.DARK_GRAY;
            hoverColor = new Color(177, 113, 4);
            selectedColor = new Color(30, 94, 1);
        }
    }

    public void toggleDeleteButton() {
        eliminar.setVisible(!eliminar.isVisible());
    }

    public void setDeleteButtonAction(MouseAdapter action) {
        eliminar.addMouseListener(action);
    }
}
