# r3edge-task-dispatcher | ![Logo](logo_ds.png)

Une librairie Spring Boot simple pour définir des tâches dans un fichier YAML  
et les associer à des handlers typés exécutés automatiquement au démarrage ou via un hot-reload.

---

## ✅ Fonctionnalités (branche feat/minimal-task-dispatcher)

- 🧾 Définition déclarative des tâches dans `application.yml`
- 🔁 Dispatch automatique au démarrage de l’application
- 🧩 Association de chaque type à un handler Spring (`TaskHandler`)
- ♻️ Reload dynamique des tâches via `/actuator/busrefresh`
- 🧼 Design minimaliste, sans dépendance au scheduling natif

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
| `id`         | ✅           | Identifiant unique de la tâche                          |
| `type`       | ✅           | Type logique lié à un handler                           |
| `enabled`    | ❌           | Activation explicite (`true` par défaut)                |
| `hotReload`  | ❌           | Autorise la mise à jour dynamique (`false` par défaut)  |

---

## 🧩 Handlers

Chaque type logique est lié à un bean Spring qui implémente `TaskHandler`.

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

---

## 🔁 Reload dynamique

Lorsqu’un événement `EnvironmentChangeEvent` est déclenché (via Spring Cloud Bus ou autre),  
les tâches peuvent être mises à jour à chaud.

| Cas de modification        | Comportement                                  |
|----------------------------|-----------------------------------------------|
| Nouvelle tâche             | Dispatch immédiat                             |
| Suppression d’une tâche    | Marquée comme désactivée                      |
| Modification de `enabled`  | Activée ou désactivée dynamiquement           |
| Tâche identique            | Ignorée                                       |

⚠️ Le champ `type` (le handler) ne peut pas être modifié dynamiquement.

---

## 🚀 Intégration

Ajoutez la dépendance dans votre `build.gradle` :

```groovy
dependencies {
    implementation "com.r3edge:task-dispatcher:0.0.1"
}
```

---

[![Build and Test - r3edge-task-dispatcher](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml/badge.svg)](https://github.com/dsissoko/r3edge-task-dispatcher/actions/workflows/cicd_code.yml)
