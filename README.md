# r3edge-task-dispatcher | ![Logo](logo_ds.png)

**Librairie Java pour exécuter des tâches déclaratives et dynamiques dans vos microservices Spring Boot.**

> 🚀 Pourquoi adopter `r3edge-task-dispatcher` ?
>
> ✅ Définissez vos tâches en YAML ou via Spring Config Server  
> ✅ Exécutez-les immédiatement ou à un instant précis (`at`)  
> ✅ Fire-and-forget natif, sans cron ni batch  
> ✅ 100 % compatible Spring Boot 3  
> ✅ Intégration ultra simple : une dépendance à ajouter, un handler à implémenter  
> ✅ Stratégies configurables : `inmemory`, `jobrunr`, `hazelcast`

This project is documented in French 🇫🇷 by default.  
An auto-translated English version is available here:

[👉 English (auto-translated by Google)](https://translate.google.com/translate?sl=auto&tl=en&u=https://github.com/dsissoko/r3edge-task-dispatcher)

---

## 📋 Fonctionnalités clés

- ✅ Définition déclarative des tâches dans `application.yml`
- ✅ Prise en charge automatique au démarrage de l’application
- ✅ Support des tâches Fire & Forget avec lancement différé (`at`)
- ✅ Support des tâches planifiées avec motif cron
- ✅ Implémentation par défaut in-memory ou `jobrunr` (et prochainement `hazelcast`)
- ✅ Autoconfiguration avec fallback systématique vers in-memory
- ✅ Support du refresh automatique des données de configuration des tâches (Config Server + Spring Cloud Bus)

---

## ⚙️ Intégration rapide

### Ajouter les dépendances nécessaires:

Cette librairie est publiée sur **GitHub Packages**: Même en open source, **GitHub impose une authentification** pour accéder aux dépendances Maven.  
Voici comment l'intégrer dans votre projet Gradle.

```groovy
repositories {
    mavenCentral()
    // Dépôt GitHub Packages de r3edge-cloud-registry
    maven {
        url = uri("https://maven.pkg.github.com/dsissoko/r3edge-cloud-registry")
        credentials {
            username = ghUser
            password = ghKey
        }
    }
    mavenLocal()
}

dependencies {
    ...
    
    implementation "com.r3edge:r3edge-task-dispatcher:0.2.2"
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Les dépendances suivante sont optionnelles mais nécessaires pour bénéficier de l'implémentation jobrunr
    // Pour bénéficier de la configuration automatique d'une datasource pour jobrunr
    runtimeOnly 'com.h2database:h2'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    // Pour bénéficier de l'implémantion jobrunr
    implementation 'org.jobrunr:jobrunr-spring-boot-3-starter:8.0.1'
    
    ...
}
```

### Déclarez vos tâches dans la configuration yaml de votre microservice Spring boot:

```yaml
jobrunr:
  enabled: true                                       # Active JobRunr dans l'application
  jobs:
    default-number-of-retries: 0  
  background-job-server:
    poll-interval-in-seconds: 5                       # Intervalle de polling pour les jobs en arrière-plan (1 seconde)
    enabled: true                                     # Lance le serveur pour exécuter les jobs en arrière-plan
  dashboard:
    enabled: true                                     # Active le dashboard web (par défaut sur http://localhost:8000)
    port: 8101
    servlet-path: /tasks-dashboard
r3edge:
  tasks:
    skip-late-tasks: false                            # si True alors les tâches dont le parametre At est dépassé ne sont pas exécutés
    definitions:
      - id: testJobRunrFireAndForgetDatacollectOK     # id unique   
        strategy: jobrunr                             # inmemory, jobrunr, hazelcast
        handler: jobrunrOK                            # nom du handler de tâche à exécuter
        enabled: true                                 # si false, alors la tâche ne sera pas exécutée, true par défaut
        #at: 2025-07-31T16:03:00Z                     # ou 2025-07-31T16:03:00+02:00 Pour une tâche Fire and Forget mais différé au moment spécifié (format  ISO 8601)
        #cron: "0,30 * * * * *"                       # Exécute toutes les 30 secondes: le motif cron (format cron expression 6 champs) permet une planification de la tâche
        meta:                                         # Map(<String, String>) à utiliser pour les paramètres du handler
          message: "fire and forget OK sous JobRunr"
          market: Kucoin
          pair: BTC
          timeframe: 1mn
          
```

> (cron et at ne sont pas à déclarer sur la même tâche)


### Implémenter le Handler:

```java
package com.example.demo;

import java.util.Map;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobRunrDataCollectHandler implements TaskHandler {

    @Override
    public String getName() {
        return "jobrunrOK";
    }

    @Override
    public void execute(TaskDescriptor task) {
        Map<String, String> meta = task.getMeta();
        // Au choix : un logger agnostique
        //Logger log = LoggerFactory.getLogger(this.getClass());
        // ou un logger adpaté à la srategy mais qui rend le handler dépendant de l'infra choisie
        Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));      
        String msg = (meta == null)
                ? "(no meta)"
                : (meta.get("message") != null && !meta.get("message").isEmpty()
                    ? meta.get("message")
                    : "(no message)");
        log.info("Start...");
        log.info("Exécution JobRunrDataCollectHandler");
        log.info("Done msg={}", msg);
    }
}
```

> ⚠️ En environnement distribué (multi-instance), la version in memory ne gère pas de verrouillage.  
> À vous de gérer la synchronisation des exécutions dans vos `TaskHandler` avec l'outil de votre choix (ex. [ShedLock](https://github.com/lukas-krecan/ShedLock) ou [Mini Lock](https://github.com/dsissoko/r3edge-mini-lock)).

### Lancer le microservice:



```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

:: Spring Boot ::               (v3.5.4)

2025-08-01T23:31:01.714+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] com.example.demo.Demo3Application        : Starting Demo3Application
2025-08-01T23:31:01.719+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] com.example.demo.Demo3Application        : Running with Spring Boot v3.5.4, Spring v6.2.9
2025-08-01T23:31:01.722+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] com.example.demo.Demo3Application        : No active profile set, falling back to 1 default profile: "default"
2025-08-01T23:31:04.688+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] com.example.demo.DemoConfig              : Configuration terminée
2025-08-01T23:31:04.749+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.c.InMemoryFireAndForgetExecutor  : ✅ Bean InMemoryExecutorConfig initialisé
2025-08-01T23:31:04.756+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.i.j.JobRunrFireAndForgetExecutor : ✅ Bean JobRunrFireAndForgetExecutor initialisé
2025-08-01T23:31:04.763+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.core.InMemoryScheduledExecutor   : ✅ Bean InMemoryScheduledExecutor initialisé
2025-08-01T23:31:04.766+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.i.j.JobRunrScheduledExecutor     : ✅ Bean JobRunrScheduledExecutor initialisé
2025-08-01T23:31:05.491+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] c.r.t.dispatcher.core.TaskDispatcher     : 🔄 Démarrage du service de dispatch.
2025-08-01T23:31:05.523+02:00 DEBUG 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.core.TaskDescriptorsProperties   : Tasks configuration chargée avec 9 tasks
2025-08-01T23:31:05.747+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] c.r.t.d.i.j.JobRunrFireAndForgetExecutor : ✅ Tâche testJobRunrFireAndForgetDatacollectOK mise en file JobRunr (Fire & Forget)
2025-08-01T23:31:05.782+02:00  INFO 19796 --- [demo-jobrunr] [restartedMain] com.example.demo.Demo3Application        : Started Demo3Application in 5.268 seconds (process running for 6.592)
2025-08-01T23:31:11.500+02:00  INFO 19796 --- [demo-jobrunr] [roundjob-worker] c.e.demo.JobRunrDataCollectHandler       : Start...
2025-08-01T23:31:11.502+02:00  INFO 19796 --- [demo-jobrunr] [roundjob-worker] c.e.demo.JobRunrDataCollectHandler       : Exécution JobRunrDataCollectHandler
2025-08-01T23:31:11.502+02:00  INFO 19796 --- [demo-jobrunr] [roundjob-worker] c.e.demo.JobRunrDataCollectHandler       : Done msg=fire and forget OK sous JobRunr
```

 - Au démarrage vos tâches sont prises en charge directement (exécution ou planification)
 - Au refresh (si cloudbus et config server correctement configurés), les tâches sont rechargées.
 
#### ⚙️ Comportement au redémarrage ou après refresh de configuration

##### 🟢 Tâche avec `cron` :

| Implémentation | Comportement au redémarrage           | Comportement au refresh            |
|----------------|----------------------------------------|------------------------------------|
| `Default`      | ✔ Replanifiée                         | ✔ Replanifiée                     |
| `JobRunr`      | ✔ Non dupliquée (persistée)           | ✔ Non dupliquée (persistée)       |

##### 🔵 Tâche Fire & Forget (sans `cron`) :

| Implémentation | Comportement au redémarrage                            | Comportement au refresh                       |
|----------------|--------------------------------------------------------|-----------------------------------------------|
| `Default`      | ✔ Relancée                                             | ✔ Relancée si `redispatchedOnRefresh: true`  |
| `JobRunr`      | ✘ Non relancée si déjà exécutée (persistée)           | ✔ Relancée si `redispatchedOnRefresh: true`  |

##### 🗑️ Tâche supprimée du YAML :

- Toute tâche précédemment dispatchée mais absente de la nouvelle config est **automatiquement annulée**.

| Implémentation | Comportement au redémarrage                | Comportement au refresh                  |
|----------------|--------------------------------------------|------------------------------------------|
| `Default`      | ✔ Annulée automatiquement                 | ✔ Annulée automatiquement               |
| `JobRunr`      | ✔ Déplanifiée si cron                     | ✔ Déplanifiée si cron                   |

---

## 📦 Stack de référence

✅ Cette librairie a été conçue et testée avec les versions suivantes :

- Java 17+
- Spring Boot 3.x
- JobRunr 6.x
- Hazelcast 5.x *(en option, support à venir)*
- Spring Cloud Config Server *(pour le support du rafraîchissement dynamique, optionnel)*
- Spring Cloud Bus *(si vous souhaitez synchroniser les mises à jour de configuration)*

---

## 🗺️ Roadmap

### 🔧 À venir
- Intégration Hazelcast comme stratégie distribuée

### 🧠 En réflexion
- Support Annotation @Job de JobRunr

---

📫 Maintenu par [@dsissoko](https://github.com/dsissoko) – contributions bienvenues.

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)