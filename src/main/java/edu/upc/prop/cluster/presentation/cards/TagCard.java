package edu.upc.prop.cluster.presentation.cards;

import edu.upc.prop.cluster.dto.TagDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


/**
 * Clase que representa una tarjeta de tag en la interfaz gráfica.
 * La tarjeta muestra el nombre del tag y un botón de eliminación si se configura para mostrarlo.
 * @author Jose Durán Foix
 */
public class TagCard extends JPanel {
        private TagDTO tag;
        private JButton deleteButton;

        public static Color backgroundColor = Color.LIGHT_GRAY;

    /**
     * Constructor que inicializa la tarjeta de tag con la información del tag.
     *
     * @param tagDTO El objeto TagDTO que contiene la información del tag.
     * @param showEditButton Indica si se debe mostrar el botón de eliminación en la tarjeta.
     */
    public TagCard(TagDTO tagDTO, boolean showEditButton) {
        tag = tagDTO;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 140));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Borde de la tarjeta
        add(new JLabel(tagDTO.getName())); // Etiqueta dentro de la tarjeta
        deleteButton = new JButton ("Delete");
        if (showEditButton) {
            add(deleteButton, BorderLayout.EAST);
        }
    }



    /*public boolean hasProduct(String product) {
        return tag.getAllProducts().containsKey(product);
    }*/


    /**
     * Obtiene el nombre del tag.
     *
     * @return El nombre del tag.
     */
    public String name() {return tag.getName(); }


    /**
     * Establece la acción que se ejecutará cuando se presione el botón de eliminación.
     *
     * @param actionListener El ActionListener que se asociará al botón de eliminación.
     */
    public void setDeleteButton(ActionListener actionListener) {
        deleteButton.addActionListener(actionListener);
    }

    /**
     * Cambia los colores de la tarjeta.
     *
     * @param isDarkMode Indica si el modo es oscuro
     */
    public static void toggleMode(boolean isDarkMode) {
        if (!isDarkMode) {
            backgroundColor = Color.LIGHT_GRAY;
        }
        else {
            backgroundColor = Color.DARK_GRAY;
        }
    }
}

