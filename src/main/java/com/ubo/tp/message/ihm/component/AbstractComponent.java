package main.java.com.ubo.tp.message.ihm.component;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * Classe abstraite fournissant une implémentation de base pour les composants
 */
public abstract class AbstractComponent implements IViewWithMessages {

    /**
     * Affiche un message d'erreur
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    @Override
    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                getComponent(),
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Affiche un message d'information
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    @Override
    public void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(
                getComponent(),
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Méthode par défaut pour obtenir la vue
     */
    public JComponent getView() {
        return getComponent();
    }

    public void destroy() {
        // Implémentation par défaut (vide)
    }
}