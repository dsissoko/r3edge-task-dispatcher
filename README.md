# spring-task-dispatcher | ![Logo](logo_ds.png)
Librairie Spring Boot pour le dispatch de tâches typées définies dans un fichier YAML.  
Les tâches sont exécutées ou planifiées automatiquement au démarrage du service, avec un support optionnel pour l'exécution exclusive via un mécanisme de lock distribué (ex: ShedLock).

## ✅ Fonctionnalités

- Déclaration des tâches via YAML
- Dispatch automatique au démarrage
- Support des tâches immédiates ou planifiées (cron)
- Association par type à un handler Spring
- Support optionnel du lock distribué
- Idempotence à l'exécution
- 🔥 Mise à jour dynamique à chaud des tâches (hot-reload)

## 🧱 Définition d'une tâche (YAML)

```yaml
tasks:
  - id: cleanup-temp
    type: cleanup
    cron: "0 0 * * *"
    enabled: true
```

| Champ     | Obligatoire | Description                                      |
|-----------|-------------|--------------------------------------------------|
| `id`      | ✅           | Identifiant unique de la tâche                   |
| `type`    | ✅           | Type logique associé à un handler                |
| `cron`    | ❌           | Expression cron de planification                 |
| `enabled` | ❌           | Activation explicite de la tâche (`true` par défaut) |

## 🧩 Handlers

Chaque type est associé à un bean Spring qui implémente l'interface `TaskHandler<T>`.  
Le dispatcher utilise ce mapping pour déléguer l'exécution.

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

Si activé, un mécanisme de verrou distribué (ex: ShedLock, Hazelcast...) empêche qu’une même tâche soit exécutée sur plusieurs instances simultanément.

## 🔁 Reload dynamique (hot update)

La librairie supporte la **mise à jour à chaud du YAML** contenant les tâches grâce à l’intégration avec **Spring Cloud Bus** (ou un `EnvironmentChangeEvent` local).

### 🔄 Comportement au reload :
- 🆕 Nouvelle tâche dans le YAML → ajoutée et planifiée
- 🗑️ Tâche supprimée → désactivée automatiquement (`enabled=false`)
- ✏️ Cron modifié → tâche replanifiée
- 🚫 `enabled: false` explicite → tâche désactivée
- ✅ `enabled: true` → tâche activée si absente ou désactivée
- ⏸️ Tâche identique → ignorée (idempotence)

⚠️ Le handler (`type`) ne peut pas être modifié dynamiquement.

## 🚀 Intégration

Ajouter la dépendance en `compileOnly` si la lib est utilisée dans un module OSS.

```groovy
dependencies {
    implementation "com.r3edge:task-dispatcher:1.0.0"
}
```

## 🧪 Test

Les handlers sont testables individuellement.  
Un utilitaire de simulation de dispatch est fourni pour les tests unitaires sans scheduler réel.

