# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Une librairie Spring Boot simple pour définir des tâches dans un fichier YAML  
et les associer à des handlers typés exécutés automatiquement au démarrage ou via un hot-reload.

---

## ✅ Fonctionnalités

- 🧾 Définition déclarative des tâches dans application.yml
- 🔁 Dispatch automatique au démarrage de l’application
- 🧩 Association de chaque type à un handler Spring (TaskHandler)
- ♻️ Reload dynamique des tâches via /actuator/busrefresh

---

## 🔧 Exemple de configuration YAML


```yaml
r3edge:
  tasks:
    definitions:
      - id: cleanup-temp
        type: cleanup
        enabled: true
        hotReload: true
```

| Champ        | Obligatoire | Description                                              |
|--------------|-------------|----------------------------------------------------------|
| id         | ✅           | Identifiant unique de la tâche                          |
| type       | ✅           | Type logique lié à un handler                           |
| enabled    | ❌           | Activation explicite (true par défaut)                |
| hotReload  | ❌           | Autorise la mise à jour dynamique (false par défaut)  |

---

## 🧩 Handlers

Chaque type logique est lié à un bean Spring qui implémente TaskHandler.

```java
@Component
public class CleanupTaskHandler implements TaskHandler {
    @Override
    public void handle(Task task) {
        // logique métier ici
    }
}
```

Le handler est exécuté automatiquement pour chaque tâche activée.

> ⚠️ En environnement distribué (multi-instance), la librairie n’applique aucun verrouillage.  
> À vous de gérer la synchronisation des exécutions dans vos `TaskHandler` avec l'outil de votre choix (ex. [ShedLock](https://github.com/lukas-krecan/ShedLock)).


---

## 🔁 Reload dynamique

Lorsqu’un événement EnvironmentChangeEvent est déclenché (via Spring Cloud Bus ou autre),  
les tâches peuvent être mises à jour à chaud.

| Cas de modification        | Comportement                                  |
|----------------------------|-----------------------------------------------|
| Nouvelle tâche             | Dispatch immédiat                             |
| Suppression d’une tâche    | Marquée comme désactivée                      |
| Modification de enabled  | Activée ou désactivée dynamiquement           |
| Tâche identique            | Ignorée                                       |

⚠️ Le champ type (le handler) ne peut pas être modifié dynamiquement.

---

## 🚀 Intégration

Cette librairie est publiée sur **GitHub Packages**. Même en open source, **GitHub impose une authentification** pour accéder aux dépendances Maven.  
Voici comment l'intégrer dans votre projet Gradle (local ou CI/CD).

---

### 🔧 1. Ajoutez le repository GitHub Packages dans build.gradle

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/dsissoko/r3edge-task-dispatcher")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GPR_USER")
            password = project.findProperty("gpr.key") ?: System.getenv("GPR_KEY")
        }
    }
    mavenCentral()
}
```

---

### 📦 2. Ajoutez la dépendance

```groovy
dependencies {
    implementation "com.r3edge:r3edge-task-dispatcher:0.0.1"
}
```

---

### 🔐 3. Authentification requise

GitHub Packages **nécessite une authentification**, même pour les projets publics.

Utilisez les mêmes variables gpr.user / gpr.key en local ou les équivalents GPR_USER / GPR_KEY dans les environnements CI/CD.

---

#### ✅ En local (poste de développeur)

1. Créez un [GitHub Personal Access Token (PAT)](https://github.com/settings/tokens) avec le scope read:packages.
2. Ajoutez dans ~/.gradle/gradle.properties :

```properties
gpr.user=ton_username_github
gpr.key=ton_token_github
```

> 💡 Ne jamais commiter ce fichier !

---

#### ✅ En CI/CD (ex : GitHub Actions)

Ajoutez dans votre pipeline :

```yaml
env:
  GPR_USER: ${{ github.actor }}
  GPR_KEY: ${{ secrets.GITHUB_TOKEN }}
```

Cela permet d’utiliser les **mêmes noms de variables** que pour le développement local, sans toucher au build.gradle.

---

### 📚 Référence officielle

> 📖 [Authenticating to GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/working-with-a-github-packages-registry#authenticating-to-github-packages)


---

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)