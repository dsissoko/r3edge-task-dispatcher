# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Une librairie Spring Boot de confort permettant de définir des tâches dans un fichier YAML  
et les associer à des handlers typés exécutés automatiquement au démarrage.

---

## ✅ Fonctionnalités

- 🧾 Définition déclarative des tâches dans application.yml
- 🔁 Dispatch automatique au démarrage de l’application
- 🧩 Association de chaque type à un handler Spring (TaskHandler)

---

## 🔧 Exemple de configuration YAML


```yaml
r3edge:
  tasks:
    definitions:
      - id: handler1
        type: printHandler
        enabled: true
        meta:
            - prop1: val1
            - prop2: val2
```

| Champ        | Obligatoire | Description                                              |
|--------------|-------------|----------------------------------------------------------|
| id         | ✅           | Identifiant unique de la tâche                          |
| type       | ✅           | Type logique lié à un handler                           |
| enabled    | ❌           | Exécution explicite au démarrage (true par défaut)      |
| meta    | ❌           | liste de parametre spécifiques à la tâche      |

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
    implementation "com.r3edge:r3edge-task-dispatcher:0.1.0"
}

```

### 3. Créer vos tâches en implémentant TaskHandler:

```java

package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler de test qui affiche un message depuis les métadonnées.
 */
@Slf4j

@Component
public class PrintTaskHandler implements TaskHandler {

    @Override
    public String getType() {
        return "print";
    }

    @Override
    public void handle(Task task) {
        String message = extractMeta(task);
        log.info("📣 Exécution de PrintTaskHandler avec les meta suivantes: {}", message);
    }
    
    private String extractMeta(Task task) {
        if (task == null || task.getMeta() == null) return "n/a";
        Object m = task.getMeta().get("message");
        return m != null ? m.toString() : "n/a";
    }
}

        
```

### 4. Déclarez vos tâches dans le fichier application.yml

```yaml

r3edge:
  tasks:
    definitions:
      - id: handler1
        type: printHandler
        enabled: true
        meta:
            - prop1: val1
            - prop2: val2

```

---

### 🔐 5. Lancez votre service

 - au démarrage vos tâches sont prises en charge directement

---

### 📚 Référence officielle

> 📖 [Authenticating to GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/working-with-a-github-packages-registry#authenticating-to-github-packages)


---

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)