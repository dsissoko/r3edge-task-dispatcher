# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Une librairie Spring Boot de confort permettant de définir des tâches dans un fichier YAML  
et les associer à des handlers typés exécutés automatiquement au démarrage.

---

## ✅ Fonctionnalités

- 🧾 Définition déclarative des tâches dans application.yml
- 🔁 Exécution automatique au démarrage de l’application
- Planification automatique au démarrage (si cron)
- implementation par défaut ou jobrunr possible (si dans votre classpath)
- Refresh automatique des données de configuration des tâches (si busrefresh avec config server mis en place)

---

## 🔧 Exemple de configuration YAML


```yaml
r3edge:
  tasks:
    definitions:
      - id: handler1
        type: cleanup
        enabled: true
        cron: "0 * * * * *"
        meta:
          target: "bar"
          dataset: "1,3,40"
      - id: handler2
        type: init
        enabled: true
        meta:
          target: "foo"
          other: "nice data"
```

| Champ        | Obligatoire | Description                                              |
|--------------|-------------|----------------------------------------------------------|
| id         | ✅           | Identifiant unique de la tâche                          |
| type       | ✅           | Type logique lié à un handler                           |
| enabled    | ❌           | Exécution explicite au démarrage (true par défaut)      |
| cron    | ❌           | Motif cron      |
| meta    | ❌           | liste de parametre spécifiques à la tâche      |

---

## 🧩 Handlers

Chaque type logique est lié à un bean Spring qui implémente TaskHandler.

```java
@Component
@Slf4j
public class Handler1 implements TaskHandler {

    @Override
    public String getType() {
        return "cleanup";
    }

    @Override
    public void handle(Task task) {
        log.info("Exécution Handler1");
    }
}
```

Au démarrage, le handler est exécuté ou planifié automatiquement pour chaque tâche activé (enabled = true).

> ⚠️ En environnement distribué (multi-instance), la librairie n’applique aucun verrouillage.  
> À vous de gérer la synchronisation des exécutions dans vos `TaskHandler` avec l'outil de votre choix (ex. [ShedLock](https://github.com/lukas-krecan/ShedLock) ou [Mini Lock](https://github.com/dsissoko/r3edge-mini-lock)).


---

## 📦 Compatibilité

✅ Testée avec :  
- **Spring Boot** `3.5.3`  
- **Spring Cloud** `2025.0.0`  
- **Java** `17` et `21`

🧘 Lib légère, sans dépendance transitive aux starters : fonctionne avec toute stack Spring moderne.  
Pas de `fat-jar`, pas de verrouillage.

---

## 🚀 Intégration

Cette librairie est publiée sur **GitHub Packages**. Même en open source, **GitHub impose une authentification** pour accéder aux dépendances Maven.  
Voici comment l'intégrer dans votre projet Gradle (local ou CI/CD).

---

### 1. Déclarer le dépôt (les packages github publiques doivent être téléchargés avec des credentials : utilisez les votres)

```groovy
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/dsissoko/r3edge-task-dispatcher")
        credentials {
            username = ghUser
            password = ghKey
        }
    }
}
```

---

### 2. Ajoutez la dépendance

```groovy
ext {
    set('springCloudVersion', "2025.0.0")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

dependencies {   
    implementation 'org.springframework.cloud:spring-cloud-starter'
    implementation "com.r3edge:r3edge-task-dispatcher:0.1.5"
    implementation 'org.jobrunr:jobrunr-spring-boot-3-starter:8.0.1' #pour une implementation jobrunr de la lib
}

```

### 3. Créer vos tâches en implémentant TaskHandler:

```java
package com.example.demo;

import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.Task;
import com.r3edge.tasks.dispatcher.TaskHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Handler1 implements TaskHandler {

    @Override
    public String getType() {
        return "cleanup";
    }

    @Override
    public void handle(Task task) {
        log.info("Lancement Handler1");
    }

}      
```

### 4. Déclarez vos tâches dans le fichier application.yml

```yaml
r3edge:
  tasks:
    strategy: default ## jobrunr est également disponible
    definitions:
      - id: handler1
        type: cleanup
        enabled: true
        cron: "0 * * * * *"
        meta:
          target: "bar"
          dataset: "1,3,40"
      - id: handler2
        type: init
        enabled: true
        meta:
          target: "foo"
          other: "nice data"
```

---

### 🔐 5. Lancez votre service

 - Au démarrage vos tâches sont prises en charge directement (exécution ou planification)

---

### 📚 Référence officielle

> 📖 [Authenticating to GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/working-with-a-github-packages-registry#authenticating-to-github-packages)


---

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)