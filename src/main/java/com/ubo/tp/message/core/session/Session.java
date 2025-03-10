package main.java.com.ubo.tp.message.core.session;

import java.util.ArrayList;
import java.util.List;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class Session implements ISession {

	/**
	 * Utilisateur connecté
	 */
	protected User mConnectedUser;

	/**
	 * Liste des observateurs de la session.
	 */
	protected List<ISessionObserver> mObservers = new ArrayList<>();

	@Override
	public void addObserver(ISessionObserver observer) {
		this.mObservers.add(observer);
	}

	@Override
	public void removeObserver(ISessionObserver observer) {
		this.mObservers.remove(observer);
	}

	@Override
	public User getConnectedUser() {
		return mConnectedUser;
	}

	@Override
	public void connect(User connectedUser) {
		this.mConnectedUser = connectedUser;

		for (ISessionObserver observer : mObservers) {
			observer.notifyLogin(connectedUser);
		}
	}

	@Override
	public void disconnect() {
		System.out.println("Méthode disconnect() appelée dans Session");
		if (this.mConnectedUser != null) {
			System.out.println("Déconnexion de l'utilisateur: @" + this.mConnectedUser.getUserTag());
		} else {
			System.out.println("Aucun utilisateur connecté à déconnecter");
		}

		this.mConnectedUser = null;
		System.out.println("mConnectedUser mis à null");

		for (ISessionObserver observer : mObservers) {
			System.out.println("Notification de l'observateur: " + observer.getClass().getSimpleName());
			observer.notifyLogout();
		}

		System.out.println("Tous les observateurs ont été notifiés");
	}
}
