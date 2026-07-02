package edu.upc.prop.cluster.presentation.views.tag;

import edu.upc.prop.cluster.dto.TagDTO;
import edu.upc.prop.cluster.presentation.PresentationController;
import edu.upc.prop.cluster.presentation.cards.TagCard;
import edu.upc.prop.cluster.presentation.layouts.WrapLayout;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Vista que muestra una lista de etiquetas (tags) y permite al usuario agregar o eliminar etiquetas.
 * También contiene una barra de búsqueda para filtrar las etiquetas mostradas.
 * @author José Durán Foix
 */
public class TagView extends JPanel {
    private static PresentationController presentationController;
    private List<TagCard> tagCards = new ArrayList<>();
    private CardLayout cardLayout;
    private JPanel mainPanel;

    /*
     * Componente que actualiza el panel inferior cuando la vista es visible.
     */
    private ComponentListener external_tu = new ComponentListener() { // bpu = BottomPanelUpdater
        @Override
        public void componentResized(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentMoved(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentHidden(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentShown(ComponentEvent e) {
            System.out.println("InnerCardPanel ahora es visible externamente.");
            mainPanel.remove(mainPanel.getComponentCount()-1);
            mainPanel.add(bottomPanel());
            mainPanel.revalidate();
        }
    };
    /*

     */
    private AncestorListener internal_tu = new AncestorListener() {
        @Override
        public void ancestorAdded(AncestorEvent event) {/*Ignorado*/}
        @Override
        public void ancestorRemoved(AncestorEvent event) {System.out.println("ProductCardPanel ahora es visible internamente.");
            mainPanel.remove(mainPanel.getComponentCount()-1);
            mainPanel.add(bottomPanel());
            mainPanel.revalidate();}
        @Override
        public void ancestorMoved(AncestorEvent event) {/*Ignorado*/}
    };


    /**
     * Constructor de la vista de etiquetas.
     * Inicializa el diseño y los componentes de la vista, incluyendo la barra de búsqueda, los paneles y los botones.
     *
     * @param controller El controlador que gestiona la lógica de la aplicación.
     */
    public TagView(PresentationController controller) {
        presentationController = controller;
        addComponentListener(external_tu);
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel());
        mainPanel.add(bottomPanel());

        JPanel addTagView = new AddTagView(presentationController);
        addTagView.addAncestorListener(internal_tu);
        add(mainPanel, "MainPanel");
        add(addTagView, "AddTagView");
    }


    /*
     * Crea el panel inferior de la vista que contiene dos paneles (izquierdo y derecho) con las tarjetas de etiquetas.
     *
     * @return El panel inferior que contiene las tarjetas de etiquetas.
     */
    private JPanel bottomPanel() {
        JPanel tagPanel = new JPanel();
        tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.X_AXIS));

        tagPanel.add(bottomLeftPanel());
        tagPanel.add(bottomRightPanel());

        return tagPanel;
    }


    /*
     * Crea el panel derecho inferior de la vista que contiene las tarjetas de etiquetas.
     * Cada tarjeta tiene un botón para eliminar la etiqueta correspondiente.
     *
     * @return Un JScrollPane que contiene el panel de tarjetas de etiquetas.
     */
    private Component bottomRightPanel() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));

        for (TagDTO tagDTO : presentationController.getAllTags()) {
            TagCard card = new TagCard(tagDTO, true);
            ActionListener deleteButtonAction = e -> {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar esta etiqueta?", "Eliminar Tag", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Eliminar la tarjeta
                    presentationController.removeTag(tagDTO.getName());  // Aquí debes tener un método en el controlador para eliminar la etiqueta
                    tagCards.remove(card);  // Eliminar la tarjeta de la lista de tarjetas
                    cardPanel.remove(card);  // Remover la tarjeta del panel
                    cardPanel.revalidate();
                    cardPanel.repaint();
                    JOptionPane.showMessageDialog(null, "La etiqueta ha sido eliminada.");
                }
            };

            // Botón para eliminar la etiqueta

            card.setDeleteButton(deleteButtonAction);
            tagCards.add(card);
            cardPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }


    /*
     * Crea el panel izquierdo inferior de la vista que contiene el botón para agregar una nueva etiqueta.
     *
     * @return El panel izquierdo con el botón para agregar una nueva etiqueta.
     */
    private JPanel bottomLeftPanel() {

        JButton filterResetButton = new JButton("Ver todas las tags");
        filterResetButton.addActionListener(e -> {for (TagCard card : tagCards) {card.setVisible(true);}});

        JButton addTagButton = new JButton("Añadir Tag");
        addTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(TagView.this, "AddTagView");
            }
        });

        JPanel bottomLeftPanel = new JPanel();
        bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel, BoxLayout.Y_AXIS));
        bottomLeftPanel.add(addTagButton);
        bottomLeftPanel.add(filterResetButton);
        bottomLeftPanel.setPreferredSize(new Dimension(220, 620));
        bottomLeftPanel.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        return bottomLeftPanel;
    }


    /*
     * Crea el panel superior de la vista que contiene la barra de búsqueda y el botón de búsqueda.
     *
     * @return El panel superior con la barra de búsqueda y el botón.
     */
    private JPanel topPanel() {

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JTextField searchBar = new JTextField(20); // Tamaño del campo de texto
        searchBar.setToolTipText("Escribe aquí para buscar una Tag.");
        searchPanel.add(searchBar);
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Verificar si la tecla presionada es "Enter"
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = searchBar.getText();
                    if (!query.isEmpty()) {
                        for (TagCard card : tagCards) {card.setVisible(card.name().toLowerCase().contains(query.toLowerCase()));}
                    } else {
                        for (TagCard card : tagCards) {card.setVisible(true);}
                    }
                }
            }
        });

        JButton searchButton = new JButton("Buscar");
        searchPanel.add(searchButton); // Agregarlo al JPanel
        searchButton.addActionListener(e -> {
            String query = searchBar.getText();
            if (!query.isEmpty()) {
                for (TagCard card : tagCards) {card.setVisible(card.name().toLowerCase().contains(query.toLowerCase()));}
            } else {
                for (TagCard card : tagCards) {card.setVisible(true);}
            }
        });

        return searchPanel;
    }


    /**
     * Método principal que crea y muestra la ventana de la vista de etiquetas.
     *
     * @param args Los argumentos de la línea de comandos (no se utilizan en este caso).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Vista de Tag");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el controlador
        PresentationController controller = new PresentationController();

        // Crear la vista de la Tag y agregarla al JFrame
        TagView tagView = new TagView(controller);
        frame.setContentPane(tagView);

        // Configuración de la ventana
        frame.setSize(700, 700);  // Ajusta el tamaño a lo que necesites
        frame.setMinimumSize(new Dimension(1280, 720));
        frame.setLocationRelativeTo(null);  // Centrar la ventana en la pantalla
        frame.setVisible(true);  // Mostrar la ventana
        }

    }