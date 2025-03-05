package main.java.com.ubo.tp.message.ihm.component;

import javax.swing.JComponent;

/**
 * Interface commune pour tous les composants de l'application
 */
public interface IComponent {
    
    /**
     * Initialise le composant
     */
    void init();
    
    /**
     * Retourne la vue du composant
     * 
     * @return Le composant Swing représentant la vue
     */
    JComponent getView();
    
    /**
     * Libère les ressources du composant
     */
    void destroy();
}

