package edu.upc.prop.cluster.presentation.views.product;

import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.dto.TagDTO;
import edu.upc.prop.cluster.presentation.PresentationController;
import edu.upc.prop.cluster.presentation.cards.ProductCard;
import edu.upc.prop.cluster.presentation.layouts.WrapLayout;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista principal para mostrar productos en un panel utilizando un diseño de tarjetas.
 * Permite la visualización de productos, la adición de nuevos productos y la aplicación de filtros de búsqueda.
 * Utiliza un {@link CardLayout} para gestionar las diferentes vistas.
 *
 * @author Alex Meca Moñino
 */
public class    ProductView extends JPanel {
    private static PresentationController presentationController;
    private List<ProductCard> productCards = new ArrayList<>();
    private CardLayout cardLayout;  // Para cambiar entre vistas
    private JPanel mainPanel;

    /*
     * Componente que actualiza el panel inferior cuando la vista es visible.
     */
    private ComponentListener external_bpu = new ComponentListener() { // bpu = BottomPanelUpdater
        @Override
        public void componentResized(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentMoved(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentHidden(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentShown(ComponentEvent e) {
            if(getComponentCount() == 3) {
                cardLayout.show(ProductView.this, "mainPanel");
                remove(getComponentCount()-1);
            }
            System.out.println("ProductCardPanel ahora es visible externamente.");
            mainPanel.remove(mainPanel.getComponentCount()-1);
            mainPanel.add(bottomPanel());
            mainPanel.revalidate();
        }
    };
    /*
     * Componente que actualiza el panel inferior cuando la vista es visible internamente.
     */
    private AncestorListener internal_bpu = new AncestorListener() {
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
     * Constructor principal de la clase {@link ProductView}.
     * Inicializa los componentes de la interfaz de usuario, incluyendo el panel principal y las vistas de productos.
     *
     * @param controller El controlador de presentación utilizado para interactuar con la lógica de negocio.
     */
    public ProductView(PresentationController controller) {
        presentationController = controller;

        cardLayout = new CardLayout();
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel());
        mainPanel.add(bottomPanel());

        JPanel addProductView = new AddProductView(presentationController);
        addProductView.addAncestorListener(internal_bpu);
        addComponentListener(external_bpu);

        add(mainPanel, "MainPanel");
        add(addProductView, "AddProductView");

    }


    /*
     * Crea el panel inferior de la vista, que incluye la sección de productos y los filtros.
     *
     * @return Un componente {@link JPanel} que contiene los paneles izquierdo y derecho de la parte inferior.
     */
    private JPanel bottomPanel() {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));

        productPanel.add(bottomLeftPanel());
        productPanel.add(bottomRightPanel());

        return productPanel;
    }


    /*
     * Crea el panel derecho en la parte inferior, que contiene las tarjetas de productos.
     *
     * @return Un componente {@link JScrollPane} que permite el desplazamiento de las tarjetas de productos.
     */
    private Component bottomRightPanel() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));

        for (ProductDTO productDTO : presentationController.getAllProducts()) {
            ProductCard card = new ProductCard(productDTO, true);
            ActionListener editButtonAction = e -> {
                EditProductView p = new EditProductView(productDTO, presentationController);
                add(p, "EditProduct");
                p.addAncestorListener(internal_bpu);
                cardLayout.show(ProductView.this, "EditProduct");
            };
            card.setEditButton(editButtonAction);
            productCards.add(card);
            cardPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }


    /*
     * Crea el panel izquierdo en la parte inferior, que incluye los filtros y botones para añadir productos.
     *a
     * @return Un componente {@link JPanel} que contiene los filtros y el botón para añadir un producto.
     */
    private JPanel bottomLeftPanel() {


        JButton filterResetButton = new JButton("Reiniciar filtros");
        filterResetButton.addActionListener(e -> {for (ProductCard card : productCards) {card.setVisible(true);}});

        JButton addProductButton = new JButton("Añadir Producto");
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(ProductView.this, "AddProductView");
            }
        });


        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 5, 10));
        for (TagDTO tag : presentationController.getAllTags()) {
            JButton button = new JButton(tag.getName());
            button.addActionListener(e -> {for(ProductCard p: productCards) {p.setVisible(p.hasTag(tag.getName()));}});
            filterPanel.add(button);
        }

        JScrollPane filterscrollPanel = new JScrollPane(filterPanel);
        filterscrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        filterscrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel bottomLeftPanel = new JPanel();
        bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel, BoxLayout.Y_AXIS));
        bottomLeftPanel.add(filterResetButton);
        bottomLeftPanel.add(filterscrollPanel);
        bottomLeftPanel.add(addProductButton);
        bottomLeftPanel.setPreferredSize(new Dimension(220, 620));
        bottomLeftPanel.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        return bottomLeftPanel;
    }


    /*
     * Crea el panel superior de la vista, que contiene la barra de búsqueda.
     *
     * @return Un componente {@link JPanel} que contiene el campo de búsqueda y el botón de búsqueda.
     */
    private JPanel topPanel() { //Definimos el panel superior de busqueda

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JTextField searchBar = new JTextField(20); // Tamaño del campo de texto
        searchBar.setToolTipText("Escribe aquí para buscar un producto.");
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Verificar si la tecla presionada es "Enter"
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = searchBar.getText();
                    if (!query.isEmpty()) {
                        for (ProductCard card : productCards) {
                            card.setVisible(card.name().toLowerCase().contains(query.toLowerCase()));
                        }
                    } else {
                        for (ProductCard card : productCards) {
                            card.setVisible(true); // Buscar vacío implica mostrar todos
                        }
                    }
                }
            }
        });
        searchPanel.add(searchBar);

        JButton searchButton = new JButton("Buscar");
        searchPanel.add(searchButton); // Agregarlo al JPanel
        searchButton.addActionListener(e -> {
            String query = searchBar.getText();
            if (!query.isEmpty()) {
                for (ProductCard card : productCards) {card.setVisible(card.name().toLowerCase().contains(query.toLowerCase()));}
            } else {
                for (ProductCard card : productCards) {card.setVisible(true);} // Buscar vacío implica mostrar todos
            }
        });

        return searchPanel;
    }


    /**
     * Metodo principal para ejecutar la vista del producto en una ventana.
     *
     * @param args Los argumentos de línea de comandos.
     */
    public static void main(String[] args) {

        JFrame frame = new JFrame("Vista de Producto");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el controlador
        PresentationController controller = new PresentationController();

        // Crear la vista del producto y agregarla al JFrame
        ProductView productView = new ProductView(controller);
        frame.setContentPane(productView);

        // Configuración de la ventana
        frame.setSize(700, 700);
        frame.setMinimumSize(new Dimension(1280, 720));
        frame.setLocationRelativeTo(null);  // Centrar la ventana en la pantalla
        frame.setVisible(true);  // Mostrar la ventana
    }
}
