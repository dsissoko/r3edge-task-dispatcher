# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Une librairie Spring Boot de confort permettant de définir des tâches dans un fichier YAML  
et les associer à des handlers typés exécutés automatiquement au démarrage.

This project is documented in French 🇫🇷 by default.  
An auto-translated English version is available here:

[👉 English (auto-translated by Google)](https://translate.google.com/translate?sl=auto&tl=en&u=https://github.com/dsissoko/r3edge-task-dispatcher)

---

## ✅ Fonctionnalités

- 🧾 Définition déclarative des tâches dans application.yml
- 🔁 Exécution automatique au démarrage de l’application
- Planification automatique au démarrage (si cron)
- implémentation par défaut ou jobrunr possible (si dans votre classpath)
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
    ...  
    // version actuelle 0.2.0
    implementation "com.r3edge:r3edge-task-dispatcher:0.2.0"
    // spring boot nécessaire
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Les dépendances suivante sont optionnelles mais nécessaires pour bénéficier de l'implémentation jobrunr
    // Pour bénéficier de la configuration automatique d'une datasource pour jobrunr
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'org.jobrunr:jobrunr-spring-boot-3-starter:8.0.1'
}

```

### 3. Créer vos tâches en implémentant TaskHandler:

```java
package com.example.demo;

import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

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
    skip-late-tasks: true # si True alors les tâches dont le parametre At est dépassé ne sont pas exécutés
    definitions:
      - id: testDefaultFireAndForgetDatacollect
        strategy: default
        type: datacollect
        enabled: true
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
      - id: testJobRunrFireAndForgetDatacollect
        strategy: jobrunr
        type: datacollect
        enabled: true
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
      - id: testDefaultFireAndForgetAtDatacollect
        strategy: default
        type: datacollect
        enabled: true
        at: "2025-07-31T15:32:00Z" # ou "2025-07-31T17:32:00+02:00": Format ISO-8601 UTC
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
      - id: testJobRunrFireAndForgetAtDatacollect
        strategy: jobrunr
        type: datacollect
        enabled: true
        at: "2025-07-31T15:32:00Z" # ou "2025-07-31T17:32:00+02:00": Format ISO-8601 UTC
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
      - id: testDefaultScheduleDatacollect
        strategy: default
        type: datacollect
        enabled: true
        cron: "0 * * * * *"  # Exécute toutes les minutes
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
      - id: testJobRunrScheduleDatacollect
        strategy: jobrunr
        type: datacollect
        enabled: true
        cron: "0 * * * * *"  # Exécute toutes les minutes
        meta:
          message: "KucoinBTC1mn"
          market: "Kucoin"
          pair: "BTC"
          timeframe: "1mn"
```

---

### 🔐 5. Lancez votre service

 - Au démarrage vos tâches sont prises en charge directement (exécution ou planification)
 - Au refresh (si cloudbus et config server correctement configurés), les tâches sont rechargées.
 
### ⚙️ Comportement au redémarrage ou après refresh de configuration

#### 🟢 Tâche avec `cron` :

| Implémentation | Comportement au redémarrage           | Comportement au refresh            |
|----------------|----------------------------------------|------------------------------------|
| `Default`      | ✔ Replanifiée                         | ✔ Replanifiée                     |
| `JobRunr`      | ✔ Non dupliquée (persistée)           | ✔ Non dupliquée (persistée)       |

- Si `enabled: false`, alors les tâches ne sont pas prises en compte.
- Pour une tâche de type cron, le champ `redispatchedOnRefresh` est systématiquement **ignoré**.

#### 🔵 Tâche ponctuelle (sans `cron`) :

| Implémentation | Comportement au redémarrage                            | Comportement au refresh                       |
|----------------|--------------------------------------------------------|-----------------------------------------------|
| `Default`      | ✔ Relancée                                             | ✔ Relancée si `redispatchedOnRefresh: true`  |
| `JobRunr`      | ✘ Non relancée si déjà exécutée (persistée)           | ✔ Relancée si `redispatchedOnRefresh: true`  |

- Si `enabled: false`, alors les tâches ne sont pas prises en compte.

#### 🗑️ Tâche supprimée du YAML :

- Toute tâche précédemment dispatchée mais absente de la nouvelle config est **automatiquement annulée**.

| Implémentation | Comportement au redémarrage                | Comportement au refresh                  |
|----------------|--------------------------------------------|------------------------------------------|
| `Default`      | ✔ Annulée automatiquement                 | ✔ Annulée automatiquement               |
| `JobRunr`      | ✔ Déplanifiée si cron                     | ✔ Déplanifiée si cron                   |

---

### 📚 Référence officielle

> 📖 [Authenticating to GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/working-with-a-github-packages-registry#authenticating-to-github-packages)


---

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)