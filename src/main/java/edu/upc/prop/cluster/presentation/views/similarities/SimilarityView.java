package edu.upc.prop.cluster.presentation.views.similarities;

import edu.upc.prop.cluster.common.Either;
import edu.upc.prop.cluster.presentation.PresentationController;
import edu.upc.prop.cluster.presentation.cards.ProductCard;
import edu.upc.prop.cluster.dto.ProductDTO;
import edu.upc.prop.cluster.presentation.cards.ShelfCard;
import edu.upc.prop.cluster.presentation.layouts.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.List;


/**
 * Vista que permite visualizar y gestionar similitudes entre productos.
 * El usuario puede seleccionar dos productos y asignarles una similitud.
 * Además, tiene la opción de ver similitudes guardadas previamente.
 * Utiliza un {@link CardLayout} para cambiar entre la vista principal y la vista de similitudes guardadas.
 * @author Jorge Vico Lora
 */
public class SimilarityView extends JPanel {
    private final PresentationController controller;
    private ProductCard firstSelectedProduct;
    private ProductCard secondSelectedProduct;
    private JTextField similarityField;
    private JButton confirmButton;

    private JPanel firstProductContainer;
    private JPanel secondProductContainer;
    private JPanel bottomPanel;

    private JButton viewSimilaritiesButton;
    private CardLayout cardLayout;  // Para cambiar entre vistas
    private JPanel cardPanel;  // Contenedor para las vistas
    private JScrollPane firstScrollPane;

    private SavedSimilaritiesView savedSimilaritiesView;

    /*
     * Componente que actualiza los productos
     */
    private ComponentListener external_pu = new ComponentListener() { // bpu = BottomPanelUpdater
        @Override
        public void componentResized(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentMoved(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentHidden(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentShown(ComponentEvent e) {
            System.out.println("InnerCardPanel ahora es visible externamente.");
            loadFirstProductPanel();
            resetSelection();
            firstScrollPane.revalidate();
            firstScrollPane.repaint();

        }
    };

    /**
     * Constructor de la vista de similitudes entre productos.
     * Inicializa el controlador y establece el layout de la vista.
     *
     * @param controller El controlador que gestiona la lógica de la aplicación.
     */
    public SimilarityView(PresentationController controller) {
        this.controller = controller;
        this.addComponentListener(external_pu);
        setLayout(new BorderLayout());

        // Inicializar CardLayout y el contenedor
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Crear la vista principal (Similarities)
        JPanel similaritiesView = new JPanel();
        similaritiesView.setLayout(new BorderLayout());
        similaritiesView.add(createMainView(), BorderLayout.CENTER);

        // Crear la vista de similitudes guardadas (SavedSimilaritiesView)
        savedSimilaritiesView = new SavedSimilaritiesView(controller);

        // Añadir ambas vistas al cardPanel
        cardPanel.add(similaritiesView, "SimilarityView");
        cardPanel.add(savedSimilaritiesView, "SavedSimilaritiesView");

        // Añadir el cardPanel a la vista principal
        add(cardPanel, BorderLayout.CENTER);

        // Cargar los productos
        loadFirstProductPanel();
    }


    /*
     * Crea la vista principal de la interfaz de similitudes.
     * Contiene los botones, paneles de productos y la configuración de eventos.
     *
     * @return El panel principal con la configuración de los productos y los botones.
     */
    private JPanel createMainView() {
        // Crear el panel principal con GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Botón superior: Ver similitudes
        viewSimilaritiesButton = new JButton("Ver similitudes");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // No ocupa mucho espacio vertical
        gbc.fill = GridBagConstraints.HORIZONTAL; // Se expande horizontalmente
        mainPanel.add(viewSimilaritiesButton, gbc);

        // Panel para productos (ocupa el 80% del espacio vertical)
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));

        // Configuración del Panel Superior (Primer producto)
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel firstLabel = new JLabel("Selecciona el primer producto:");
        firstLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 15)); // Label pequeño

        firstProductContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        firstScrollPane = new JScrollPane(firstProductContainer);
        firstScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        topPanel.add(firstLabel, BorderLayout.NORTH);
        topPanel.add(firstScrollPane, BorderLayout.CENTER);

        // Configuración del Panel Medio (Segundo producto)
        JPanel middlePanel = new JPanel(new BorderLayout());
        JLabel secondLabel = new JLabel("Selecciona el segundo producto:");
        secondLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 15)); // Label pequeño

        secondProductContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane secondScrollPane = new JScrollPane(secondProductContainer);
        secondScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        middlePanel.add(secondLabel, BorderLayout.NORTH);
        middlePanel.add(secondScrollPane, BorderLayout.CENTER);

        // Agregar los dos paneles al productsPanel
        gbc.gridy = 1;
        gbc.weighty = 0.5; // 80% del espacio vertical
        gbc.fill = GridBagConstraints.BOTH;
        productsPanel.add(topPanel, gbc);
        productsPanel.add(middlePanel, gbc);

        // Agregar el productsPanel al GridBagLayout
        gbc.gridy = 1;
        gbc.weighty = 0.8; // 80% del espacio vertical
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(productsPanel, gbc);

        // Panel inferior
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        gbc.gridy = 2;
        gbc.weighty = 0.05; // 5% del espacio vertical
        mainPanel.add(bottomPanel, gbc);

        // Configurar el ActionListener para cambiar de vista
        viewSimilaritiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savedSimilaritiesView.loadSimilarities();
                cardLayout.show(cardPanel, "SavedSimilaritiesView");  // Cambiar a la vista SavedSimilaritiesView
            }
        });

        return mainPanel;
    }


    /*
     * Carga y muestra los productos en el panel para el primer producto seleccionado.
     * Los productos se ordenan alfabéticamente.
     */
    private void loadFirstProductPanel() {
        List<ProductDTO> products = controller.getAllProducts();

        products.sort(Comparator.comparing(ProductDTO::getName));

        firstProductContainer.removeAll();
        secondProductContainer.removeAll();

        for (ProductDTO productDTO : products) {
            ProductCard card = new ProductCard(productDTO, false);
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleFirstProductSelection(card);
                }
            });

            card.addMouseListener (new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (firstSelectedProduct != card)
                        card.setBackground(ShelfCard.hoverColor);
                }
            });

            card.addMouseListener (new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if (firstSelectedProduct != card)
                        card.setBackground(ShelfCard.defaultColor);
                }
            });
            firstProductContainer.add(card);
        }
        firstProductContainer.revalidate();
        firstProductContainer.repaint();
    }


    /*
     * Maneja la selección del primer producto.
     * Actualiza la vista para reflejar la selección del primer producto y carga el segundo producto.
     *
     * @param card El {@link ProductCard} del primer producto seleccionado.
     */
    private void handleFirstProductSelection(ProductCard card) {
        firstSelectedProduct = card;
        bottomPanel.removeAll();
        JLabel label = new JLabel("Primer producto seleccionado: " + firstSelectedProduct.name());

        for (Component c : firstProductContainer.getComponents()) {
            c.setBackground(ShelfCard.defaultColor);
        }
        card.setBackground(ShelfCard.selectedColor);

        bottomPanel.add(label);
        bottomPanel.revalidate();
        bottomPanel.repaint();
        loadSecondProductPanel();
    }


    /*
     * Carga y muestra los productos en el panel para el segundo producto seleccionado.
     * Los productos se ordenan alfabéticamente, excluyendo el primer producto seleccionado.
     */
    private void loadSecondProductPanel() {
        List<ProductDTO> products = controller.getAllProducts();

        products.sort(Comparator.comparing(ProductDTO::getName)); // Facilitar la busqueda

        secondProductContainer.removeAll();

        for (ProductDTO productDTO : products) {
            if (!productDTO.getName().equals(firstSelectedProduct.name())) {
                ProductCard card = new ProductCard(productDTO, false);
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSecondProductSelection(card);
                    }
                });

                card.addMouseListener (new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (secondSelectedProduct != card)
                            card.setBackground(ShelfCard.hoverColor);
                    }
                });

                card.addMouseListener (new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (secondSelectedProduct != card)
                            card.setBackground(ShelfCard.defaultColor);
                    }
                });

                secondProductContainer.add(card);
            }
        }
        secondProductContainer.revalidate();
        secondProductContainer.repaint();
    }


    /*
     * Maneja la selección del segundo producto.
     * Muestra el campo para ingresar la similitud entre los productos seleccionados.
     *
     * @param card El {@link ProductCard} del segundo producto seleccionado.
     */
    private void handleSecondProductSelection(ProductCard card) {
        secondSelectedProduct = card;

        for (Component c : secondProductContainer.getComponents()) {
            c.setBackground(ShelfCard.defaultColor);
        }
        card.setBackground(ShelfCard.selectedColor);
        showSimilarityInput();
    }


    /*
     * Muestra el campo de entrada para la similitud y el botón de confirmación.
     * Permite al usuario ingresar un valor numérico para la similitud entre los productos seleccionados.
     */
    private void showSimilarityInput() {
        bottomPanel.removeAll();
        JLabel label1 = new JLabel("Primer producto: " + firstSelectedProduct.name());
        JLabel label2 = new JLabel("Segundo producto: " + secondSelectedProduct.name());
        JLabel instruction = new JLabel("Establece la similitud:");

        similarityField = new JTextField(10);
        confirmButton = new JButton("Confirmar");
        confirmButton.addActionListener(this::handleSimilarityConfirm);

        bottomPanel.add(label1);
        bottomPanel.add(label2);
        bottomPanel.add(instruction);
        bottomPanel.add(similarityField);
        bottomPanel.add(confirmButton);

        bottomPanel.revalidate();
        bottomPanel.repaint();
    }



    /*
     * Confirma la similitud ingresada entre los dos productos seleccionados.
     * Almacenará la similitud en el controlador y actualizará la vista.
     *
     * @param e El evento de clic del botón de confirmación.
     */
    private void handleSimilarityConfirm(ActionEvent e) {
        try {
            double similarity = Double.parseDouble(similarityField.getText()); //todo mover esto al controller
            Either<String, Boolean> result = controller.addSimilarity(firstSelectedProduct.name(), secondSelectedProduct.name(), similarity);
            result.fold(
                    errorMessage -> {
                        JOptionPane.showMessageDialog(SimilarityView.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    },
                    productDTO -> {
                        JOptionPane.showMessageDialog(this, "Similitud establecida correctamente", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
                        resetSelection();
                        return null;
                    }
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un número válido para la similitud.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        }
    }



    /*
     * Elimina la seleccion de productos.
     */
    private void resetSelection() {
        firstSelectedProduct = null;
        secondSelectedProduct = null;
        similarityField = null;
        confirmButton = null;
        bottomPanel.removeAll();
        firstProductContainer.removeAll();
        secondProductContainer.removeAll();
        loadFirstProductPanel();
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
