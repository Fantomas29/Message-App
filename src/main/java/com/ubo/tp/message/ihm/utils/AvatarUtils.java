package main.java.com.ubo.tp.message.ihm.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Classe utilitaire pour la gestion des avatars.
 */
public class AvatarUtils {

    /**
     * Configure un JLabel pour afficher un avatar
     *
     * @param avatarLabel Le JLabel à configurer
     * @param avatarPath Chemin vers l'avatar
     * @param size Taille de l'avatar
     * @param placeholder Texte à afficher en l'absence d'avatar
     */
    public static void displayAvatar(JLabel avatarLabel, String avatarPath, int size, String placeholder) {
        // Configuration initiale du label
        avatarLabel.setPreferredSize(new Dimension(size, size));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                try {
                    // Chargement et redimensionnement de l'image
                    ImageIcon originalIcon = new ImageIcon(avatarPath);
                    Image img = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                    avatarLabel.setText("");
                } catch (Exception e) {
                    // En cas d'erreur, afficher le placeholder
                    configureDefaultAvatar(avatarLabel, placeholder);
                }
            } else {
                // Si le fichier n'existe pas, afficher le placeholder
                configureDefaultAvatar(avatarLabel, placeholder);
            }
        } else {
            // Si pas de chemin d'avatar, afficher le placeholder
            configureDefaultAvatar(avatarLabel, placeholder);
        }
    }

    /**
     * Configure un avatar par défaut
     *
     * @param avatarLabel Le JLabel à configurer
     * @param placeholder Texte à afficher
     */
    private static void configureDefaultAvatar(JLabel avatarLabel, String placeholder) {
        avatarLabel.setIcon(null);
        avatarLabel.setText(placeholder);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 24));
    }
}