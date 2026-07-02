package edu.upc.prop.cluster.presentation.views;

import edu.upc.prop.cluster.presentation.PresentationController;
import edu.upc.prop.cluster.presentation.cards.ShelfCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.event.*;
import java.awt.*;
import java.util.List;


/**
 * Vista principal que muestra la estantería y permite al usuario generar una nueva estantería,
 * guardar la estantería actual, y ajustar el tamaño de la estantería.
 * También permite mover y reorganizar los productos en la estantería.
 *
 * @author Alex Meca Moñino
 */
public class HomeView extends JPanel {
    private CardLayout cardLayout;  // Para cambiar entre vistas
    private List<String> shelf;
    private JPanel homePanel;
    private PresentationController controller;
    private int shelfSize;
    private boolean clicked = false;
    private int lastClicked = -1;

    /*
     * Componente que actualiza el panel inferior cuando la vista es visible internamente.
     */
    private AncestorListener internal_shelfupdater = new AncestorListener() {
        @Override
        public void ancestorAdded(AncestorEvent event) {/*Ignorado*/}
        @Override
        public void ancestorRemoved(AncestorEvent event) {
            update(true);
            clearSelection();
        }
        @Override
        public void ancestorMoved(AncestorEvent event) {/*Ignorado*/}
    };

    private ComponentListener external_shelfupdater = new ComponentListener() { // bpu = BottomPanelUpdater
        @Override
        public void componentResized(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentMoved(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentHidden(ComponentEvent e) {/*Ignorado*/}

        @Override
        public void componentShown(ComponentEvent e) {
            update(true);
            clearSelection();
        }
    };


    /*
     * Añade la estantería a la vista con su disposición.
     *
     * @return Un JScrollPane que contiene la estantería.
     */
    private JScrollPane addShelf() {
        JPanel shelfPanel = new JPanel();
        shelfPanel.setLayout(new BoxLayout(shelfPanel, BoxLayout.Y_AXIS));
        boolean sentido = true;
        int i = 0;
        JPanel shelfAux;

        while(i < shelf.size()) {
            int limit = Math.min(i + shelfSize-1, shelf.size()-1);
            shelfAux = new JPanel();
            shelfAux.setLayout(new BoxLayout(shelfAux, BoxLayout.X_AXIS));
            if(sentido) {
                for(int j = i; j <= limit; j++) {
                    shelfAux.add(addClickableSpace(j));

                    ShelfCard card = new ShelfCard(shelf.get(j));
                    int finalJ = j;
                    card.setDeleteButtonAction(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            shelf.remove(finalJ);
                            clearSelection();
                            update(false);
                        }
                    });
                    card.addMouseListener(createCardMouseListener(card, j));

                    shelfAux.add(card);
                }
                shelfAux.add(addClickableSpace(limit+1));
            }
            else {
                for (int j = limit; j >= i; j--) {
                    shelfAux.add(addClickableSpace(j+1));
                    ShelfCard card = new ShelfCard(shelf.get(j));
                    int finalJ = j;
                    card.setDeleteButtonAction(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            shelf.remove(finalJ);
                            clearSelection();
                            update(false);
                        }
                    });
                    card.addMouseListener(createCardMouseListener(card, j));
                    shelfAux.add(card);
                }
                shelfAux.add(addClickableSpace(i));
            }
            i = limit+1;
            shelfPanel.add(shelfAux);
            sentido = !sentido;
        }

        JScrollPane scrollPane = new JScrollPane(shelfPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }


    /*
     * Constructor de la vista principal.
     * Inicializa los componentes de la vista, incluyendo botones para generar una nueva estantería, guardar la actual y cambiar el tamaño de la estantería.
     *
     * @param controller El controlador que gestiona la lógica de la aplicación.
     */
    public HomeView(PresentationController controller) {
        shelfSize = 5;
        addComponentListener(external_shelfupdater);
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        shelf = controller.getShelf();
        this.controller = controller;

        JPanel algorithmView = new AlgorithmView(controller);
        algorithmView.addAncestorListener(internal_shelfupdater);

        homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));

        JButton generateButton = new JButton("Generar nueva estanteria");
        generateButton.addActionListener(e -> cardLayout.show(HomeView.this, "Algorithm"));
        generateButton.setPreferredSize(new Dimension(50, 30));
        homePanel.add(generateButton);

        JButton saveButton = new JButton("Guardar estanteria actual");
        saveButton.addActionListener(e -> controller.saveShelf(shelf));
        saveButton.setPreferredSize(new Dimension(50, 30));

        JButton removeSizeButton = new JButton("-");
        removeSizeButton.addActionListener(e -> {shelfSize = Math.max(shelfSize-1, 2); update(false); clearSelection();});

        JButton addSizeButton = new JButton("+");
        addSizeButton.addActionListener(e -> {shelfSize =Math.min(shelfSize+1, shelf.size()); update(false); clearSelection();});


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(generateButton);
        topPanel.add(saveButton);
        JLabel text1 = new JLabel("Tamaño de la estanteria: ");
        text1.setBorder(new EmptyBorder(0, 200, 0, 0));
        topPanel.add(text1);
        topPanel.add(removeSizeButton);
        topPanel.add(addSizeButton);
        homePanel.add(topPanel);

        homePanel.add(addShelf());

        add(homePanel, "Home");
        add(algorithmView, "Algorithm");
    }


    /**
     * Actualiza la vista de la estantería. Si se indica que es necesario un actualización forzada, se recarga la estantería desde el controlador.
     *
     * @param forceUpdate Si es true, fuerza la actualización de la estantería desde el controlador.
     */
    public void update(boolean forceUpdate) {
        if(forceUpdate) shelf = controller.getShelf();
        homePanel.remove(homePanel.getComponentCount()-1);
        homePanel.add(addShelf());
        homePanel.revalidate();
        homePanel.repaint();
        revalidate();
        repaint();
    }


    /*
     * Añade un espacio clickeable a la estantería para permitir mover los elementos.
     *
     * @param Index El índice del espacio en la estantería.
     * @return Un JPanel que representa un espacio clickeable.
     */
    private JPanel addClickableSpace(int Index) {
        JPanel space = new JPanel();
        space.setBackground(null);
        space.setMaximumSize(new Dimension(20,100));
        space.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clicked) {
                    String temp = shelf.get(lastClicked);
                    shelf.add(Index, temp);
                    if(Index < lastClicked) shelf.remove(lastClicked +1);
                    else shelf.remove(lastClicked);
                    clearSelection();
                    update(false);
                }
            }

            public void mouseEntered(MouseEvent e) {
                if(clicked) space.setBackground(ShelfCard.hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                space.setBackground(null);
            }
        });

        return space ;
    }


    /*
     * Crea un MouseListener para los eventos de las tarjetas de la estantería.
     *
     * @param card       La tarjeta que representa un producto en la estantería.
     * @param cardIndex El índice de la tarjeta en la estantería.
     * @return Un MouseAdapter que maneja los clics y el paso del mouse sobre la tarjeta.
     */
    private MouseAdapter createCardMouseListener(ShelfCard card, int cardIndex) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!clicked) {
                    card.toggleDeleteButton();
                    card.setBackground(ShelfCard.selectedColor);
                    revalidate();
                    repaint();
                    clicked = true;
                    lastClicked = cardIndex;
                } else {
                    if (lastClicked == cardIndex) {
                        card.toggleDeleteButton();
                        card.setBackground(ShelfCard.defaultColor);
                        clearSelection();
                    } else {
                        String temp = shelf.get(lastClicked);
                        shelf.set(lastClicked, shelf.get(cardIndex));
                        shelf.set(cardIndex, temp);
                        clearSelection();
                        update(false);
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
                if(lastClicked != cardIndex)card.setBackground(ShelfCard.hoverColor);  // Cambiar color al pasar el mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!clicked || lastClicked != cardIndex) card.setBackground(ShelfCard.defaultColor);  // Restaurar color cuando el mouse sale
            }
        };
    }


    /*
     * Limpia la selección actual de la estantería.
     */
    private void clearSelection() {
        clicked = false;
        lastClicked = -1;
    }
}

