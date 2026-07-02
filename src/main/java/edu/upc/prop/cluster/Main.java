package edu.upc.prop.cluster;

import edu.upc.prop.cluster.presentation.PresentationController;


/**
 * Clase principal de la aplicación. Su función es iniciar la ejecución de la aplicación
 * creando una instancia del {@link PresentationController} y llamando al método {@link PresentationController#start()}
 * para iniciar la interfaz de usuario.
 *
 * @author Jorge Vico Lora
 */
public class    Main {

    /**
     * Metodo principal que inicia la aplicación.
     * Crea una instancia de {@link PresentationController} y llama al método {@link PresentationController#start()}
     * para mostrar la ventana principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos, que no son utilizados en esta aplicación.
     */
    public static void main(String[] args) {
        PresentationController presentationController = new PresentationController();
        presentationController.start();
    }
}
