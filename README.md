# MovieApp[MASVS-Recommandation]

Une application Android moderne pour la découverte et la gestion de films, avec des fonctionnalités de sécurité avancées.

## 🎬 Fonctionnalités

- 📱 Interface utilisateur moderne et intuitive
- 🎯 Recherche de films en temps réel
- ⭐ Système de favoris
- 📅 Planning de films
- 🌙 Mode sombre/clair
- 🔒 Sécurité renforcée
  - Protection contre le debugging
  - Détection des appareils rootés
  - Vérifications de sécurité avancées

## 🛠️ Technologies Utilisées

- Android Studio
- Kotlin/Java
- Retrofit pour les appels API
- Glide pour le chargement d'images
- Material Design Components
- RecyclerView pour les listes
- SharedPreferences pour le stockage local

## 🔒 Sécurité

L'application implémente plusieurs mesures de sécurité :

1. **Protection contre le Debugging**
   - Détection des débogueurs connectés
   - Arrêt automatique en cas de tentative de debugging
   - Conforme à la recommandation MASVS-CODE-1

2. **Protection contre le Root**
   - Détection des appareils rootés
   - Empêche l'exécution sur les appareils compromis
   - Vérification des fichiers système sensibles

3. **Sécurité des Données**
   - Stockage sécurisé des préférences
   - Gestion sécurisée des clés API
   - Protection contre les attaques courantes

## 📋 Prérequis

- Android Studio Arctic Fox ou supérieur
- Android SDK 21+
- JDK 11 ou supérieur
- Une clé API valide pour l'API de films

## 🚀 Installation

1. Clonez le repository :
```bash
git clone https://github.com/votre-username/movieapp.git
```

2. Ouvrez le projet dans Android Studio

3. Configurez votre clé API :
   - Créez un fichier `secrets.properties` à la racine du projet
   - Ajoutez votre clé API : `API_KEY=votre_clé_api`

4. Synchronisez le projet avec Gradle

5. Exécutez l'application sur un émulateur ou un appareil physique

## ⚠️ Limitations de Sécurité

- L'application ne fonctionnera pas sur les appareils rootés
- Le debugging est désactivé en production
- Certaines fonctionnalités peuvent être limitées en mode debug

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Auteurs

- BOUTALMAOUINE Mohamed - *Développement initial* - [Votre GitHub](https://github.com/MOHAMEDBOUTALMAOUINE)
- EL QASIMY Soufiane  - *Développement initial* - [Votre GitHub](https://github.com/MOHAMEDBOUTALMAOUINE)
- FATHI Hajar  - *Développement initial* - [Votre GitHub](https://github.com/MOHAMEDBOUTALMAOUINE)
## 🙏 Remerciements

- [The Movie Database API](https://www.themoviedb.org/documentation/api) pour l'API de films
- La communauté Android pour les outils et bibliothèques
- Tous les contributeurs qui ont participé au projet 
