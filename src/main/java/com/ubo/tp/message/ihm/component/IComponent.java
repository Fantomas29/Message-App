package main.java.com.ubo.tp.message.ihm.component;

import javax.swing.JComponent;

public interface IComponent {
    /**
     * Retourne le composant Swing associé
     * @return Le composant Swing
     */
    JComponent getComponent();

    /**
     * Initialise le composant
     */
    void init();

    /**
     * Nettoie les ressources lors de la destruction
     */
    void destroy();
}