package edu.upc.prop.cluster.presentation;

import edu.upc.prop.cluster.presentation.cards.ProductCard;

import java.util.Objects;
import java.util.prefs.Preferences;
import edu.upc.prop.cluster.presentation.cards.ShelfCard;
import edu.upc.prop.cluster.presentation.cards.TagCard;
import edu.upc.prop.cluster.presentation.views.HomeView;
import edu.upc.prop.cluster.presentation.views.SettingsView;
import edu.upc.prop.cluster.presentation.views.product.ProductView;
import edu.upc.prop.cluster.presentation.views.similarities.SimilarityView;
import edu.upc.prop.cluster.presentation.views.tag.TagView;

import javax.swing.*;
import java.awt.*;


/**
 * Esta clase representa la ventana principal de la aplicación. Contiene un {@link JFrame} con un
 * {@link CardLayout} que permite cambiar entre diferentes vistas, como la vista de inicio, la vista
 * de productos, la vista de etiquetas, la vista de similitud y la vista de configuración.
 * Además, tiene un panel de botones en la parte inferior que permite al usuario navegar entre las vistas.
 *
 * @author Jorge Vico Lora
 */
public class MainFrame {
    /* El controlador de presentación de la aplicación */
    private PresentationController controller;

    /* El JFrame principal de la aplicación */
    private final JFrame frame;

    /* El layout utilizado para gestionar las diferentes vistas */
    private final CardLayout cardLayout;

    /* El panel que contiene los botones de navegación */
    private final JPanel buttonPanel;

    /* El panel que contiene las diferentes vistas que se pueden mostrar */
    private final JPanel cardPanel;

    /* Indicador del tema actual */
    private boolean isDarkMode = false;




    /**
     * Constructor de la clase {@code MainFrame}.
     * Configura la ventana principal de la aplicación, incluyendo el layout, las vistas y los botones de navegación.
     *
     * @param controller El controlador de presentación que maneja las interacciones del usuario
     */
    public MainFrame(PresentationController controller) {
        this.controller = controller;

        // Configuración del JFrame
        frame = new JFrame("Shelf Generator");
        frame.setSize(1310, 720);
        frame.setMinimumSize(new Dimension(1310, 720));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Usamos un CardLayout para cambiar entre vistas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Crear las vistas y pasar el controlador
        JPanel homeView = new HomeView(controller);
        JPanel productView = new ProductView(controller);
        JPanel settingsView = new SettingsView(controller, this);
        JPanel tagView = new TagView(controller);
        JPanel similarityView = new SimilarityView(controller);

        // Añadir las vistas al CardLayout
        cardPanel.add(homeView, "HomeView");
        cardPanel.add(productView, "ProductView");
        cardPanel.add(settingsView, "SettingsView");
        cardPanel.add(tagView, "TagView");
        cardPanel.add(similarityView, "SimilView");

        // Botones inferiores con FlowLayout para centrado
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));  // Centrado y con espaciado entre botones

        JButton btnView1 = new JButton("Home");
        JButton btnView2 = new JButton("Product");
        JButton btnView3 = new JButton("Tag");
        JButton btnView4 = new JButton("Similarity");
        JButton btnView5 = new JButton("Settings");

        buttonPanel.add(btnView4);
        buttonPanel.add(btnView2);
        buttonPanel.add(btnView1);
        buttonPanel.add(btnView3);
        buttonPanel.add(btnView5);

        // Acción para cambiar entre vistas cuando se hace clic en un botón
        btnView1.addActionListener(e -> cardLayout.show(cardPanel, "HomeView"));
        btnView2.addActionListener(e -> cardLayout.show(cardPanel, "ProductView"));
        btnView3.addActionListener(e -> cardLayout.show(cardPanel, "TagView"));
        btnView4.addActionListener(e -> cardLayout.show(cardPanel, "SimilView"));
        btnView5.addActionListener(e -> cardLayout.show(cardPanel, "SettingsView"));

        // Añadir el panel con el CardLayout al frame
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        Preferences prefs = Preferences.userRoot().node("com.shelfgenerator");
        String mode = prefs.get("mode", "light");
        if(Objects.equals(mode, "dark")) toggleTheme("HomeView");

    }




    /**
     * Muestra la ventana principal. Este método debe ser llamado para hacer visible la ventana.
     */
    public void show() {
        frame.setVisible(true);
    }


    /**
     * Alterna entre modo claro y oscuro
     */
    public void toggleTheme(String currentView) {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            Preferences prefs = Preferences.userRoot().node("com.shelfgenerator");
            prefs.put("mode", "dark");
            applyDarkMode();
        } else {
            Preferences prefs = Preferences.userRoot().node("com.shelfgenerator");
            prefs.put("mode", "light");
            applyLightMode();
        }

        SwingUtilities.updateComponentTreeUI(frame); // Forzar la actualización de la UI
        SwingUtilities.updateComponentTreeUI(buttonPanel); // Forzar la actualización de la UI
        SwingUtilities.updateComponentTreeUI(cardPanel);
        // Otra forma de asegurarse de que la UI se actualice completamente
        cardPanel.revalidate();
        cardPanel.repaint();
        buttonPanel.revalidate();
        buttonPanel.repaint();
        frame.revalidate();
        frame.repaint();

        // Crear las vistas y pasar el controlador
        JPanel homeView = new HomeView(this.controller);
        JPanel productView = new ProductView(this.controller);
        JPanel settingsView = new SettingsView(controller, this);
        JPanel tagView = new TagView(this.controller);
        JPanel similarityView = new SimilarityView(this.controller);


        cardPanel.removeAll();

        // Añadir las vistas al CardLayout
        cardPanel.add(homeView, "HomeView");
        cardPanel.add(productView, "ProductView");
        cardPanel.add(settingsView, "SettingsView");
        cardPanel.add(tagView, "TagView");
        cardPanel.add(similarityView, "SimilView");

        cardLayout.show(cardPanel, currentView);
    }

    /**
     * Pide el tema
     */
    public boolean getTheme() {
        return isDarkMode;
    }

    /**
     * Pone el modo oscuro
     */
    private void applyDarkMode() {
        try {
            // Restaurar el LookAndFeel predeterminado del sistema
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", new Color(53, 53, 53));
            UIManager.put("info", new Color(53, 53, 53));
            UIManager.put("nimbusBase", new Color(18, 18, 18));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusLightBackground", new Color(18, 18, 18));
            UIManager.put("text", new Color(255, 255, 255));

            // Actualizar los colores de cada card
            ShelfCard.toggleMode(true);
            TagCard.toggleMode(true);
            ProductCard.toggleMode(true);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pone el modo claro
     */
    private void applyLightMode() {
        try {
            // Restaurar el LookAndFeel predeterminado de swing
            UIManager.put("control", new Color(255, 255, 255));
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            ShelfCard.toggleMode(false);
            TagCard.toggleMode(false);
            ProductCard.toggleMode(false);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
