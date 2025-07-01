# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Librairie Spring Boot pour le dispatch de tâches typées définies dans un fichier YAML.  
Les tâches sont exécutées ou planifiées automatiquement au démarrage du service, avec un support optionnel pour l'exécution exclusive via un mécanisme de lock distribué (ex: ShedLock).

## ✅ Fonctionnalités

- Déclaration simple des tâches via YAML
- Dispatch automatique au démarrage
- Support des tâches immédiates ou planifiées (cron)
- Association de chaque type à un handler Spring
- Support optionnel du lock distribué
- Idempotence à l'exécution
- 🔥 Mise à jour dynamique à chaud des tâches (hot-reload)

## 🧱 Définition d'une tâche (YAML)

Les tâches sont définies sous la clé `r3edge.tasks.definitions` :

```yaml
r3edge:
  tasks:
    definitions:
      - id: cleanup-temp
        type: cleanup
        cron: "0 0 * * *"
        enabled: true
        hotReload: true
```

| Champ       | Obligatoire | Description                                                                |
|-------------|-------------|----------------------------------------------------------------------------|
| `id`        | ✅           | Identifiant unique de la tâche                                             |
| `type`      | ✅           | Type logique associé à un handler                                          |
| `cron`      | ❌           | Expression cron pour planification (sinon, exécution immédiate)            |
| `enabled`   | ❌           | Activation explicite (`true` par défaut)                                   |
| `hotReload` | ❌           | Autorise la mise à jour dynamique à chaud (`false` par défaut)             |

## 🧩 Handlers

Chaque type est associé à un bean Spring qui implémente l'interface `TaskHandler<T>`.

```java
@Component
public class CleanupTaskHandler implements TaskHandler<CleanupTaskDefinition> {
    @Override
    public void handle(CleanupTaskDefinition task) {
        // logique métier ici
    }
}
```

## 🔐 Exécution exclusive

Un mécanisme de lock distribué (ex: ShedLock, Hazelcast...) peut être activé pour garantir l'exécution exclusive d'une tâche, même en environnement multi-instance.

## 🔁 Reload dynamique (hot update)

La librairie supporte la **mise à jour à chaud du YAML** via `Spring Cloud Bus` ou un événement `EnvironmentChangeEvent`.

### 🔄 Comportement au reload :

| Cas                              | Effet déclenché                                       |
|----------------------------------|--------------------------------------------------------|
| 🆕 Nouvelle tâche                | Dispatch immédiat ou planification                    |
| 🗑️ Tâche supprimée              | Marquée comme désactivée (`enabled=false`)            |
| ✏️ Cron modifié                 | Replanification automatique                           |
| 🚫 `enabled: false` explicite   | Tâche désactivée                                      |
| ✅ `enabled: true`              | Réactivation si précédemment désactivée               |
| ⏸️ Tâche identique              | Ignorée (aucune action)                               |

⚠️ Le champ `type` (handler) ne peut pas être modifié dynamiquement.

## 🚀 Intégration

Ajouter la dépendance (exemple avec Gradle) :

```groovy
dependencies {
    implementation "com.r3edge:task-dispatcher:1.0.0"
}
```

## 🧪 Tests

Les handlers sont testables indépendamment.  
Un utilitaire permet de simuler un dispatch manuel dans vos tests unitaires sans activer le scheduler.

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)
