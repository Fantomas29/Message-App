package main.java.com.ubo.tp.message.ihm.component;

import javax.swing.*; /**
 * Interface commune pour toutes les vues de composants
 */
public interface IView {
    
    /**
     * Retourne le composant Swing représentant la vue
     * 
     * @return Le composant Swing
     */
    JComponent getComponent();
}
