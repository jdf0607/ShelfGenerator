package edu.upc.prop.cluster.presentation.cards;

import edu.upc.prop.cluster.dto.ProductDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;


/**
 * Clase que representa una tarjeta de producto dentro de la interfaz gráfica.
 * La tarjeta muestra el nombre del producto y sus tags, además de un botón de edición
 * si se solicita.
 * @author Alex Meca Moñino
 */
public class ProductCard extends JPanel {
    private ProductDTO p;
    private JButton editButton;
    public static Color backgroundColor = Color.LIGHT_GRAY;


    /**
     * Constructor que inicializa la tarjeta de producto.
     *
     * @param product El objeto ProductDTO que representa el producto.
     * @param showEditButton Indica si debe mostrarse el botón de edición.
     */
    public ProductCard(ProductDTO product, boolean showEditButton) {
        p = product;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 140));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Borde de la tarjeta
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar en el eje X
        add(nameLabel);
        JPanel tagsPanel = new JPanel(new FlowLayout());
        tagsPanel.setPreferredSize(new Dimension(200, 100));
        tagsPanel.setMaximumSize(new Dimension(200, 100));
        add(tagsPanel);
        int characterCount = 0;

        for(Map.Entry<String,Double> tag: product.getTags().entrySet()) {
            characterCount += tag.getKey().length();
            if(characterCount >= 117) {
                tagsPanel.add(new JLabel("..."));
                break;
            }
            JLabel tagentry = new JLabel(tag.getKey());
            tagentry.setFont(new Font("Serif", Font.PLAIN, 12));
            tagsPanel.add(tagentry);
        }

        if (showEditButton) {
            editButton = new JButton("+");
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.add(editButton, BorderLayout.EAST);
            add(buttonPanel);
        }
    }


    /**
     * Verifica si el producto contiene un tag específico.
     *
     * @param tag El nombre del tag a verificar.
     * @return true si el producto contiene el tag, false en caso contrario.
     */
    public boolean hasTag(String tag) {
        return p.getTags().containsKey(tag);
    }


    /**
     * Obtiene el nombre del producto.
     *
     * @return El nombre del producto.
     */
    public String name() {
        return p.getName();
    }


    /**
     * Establece un ActionListener para el botón de edición de la tarjeta.
     *
     * @param actionListener El ActionListener a asociar con el botón de edición.
     */
    public void setEditButton(ActionListener actionListener) {editButton.addActionListener(actionListener);}

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
