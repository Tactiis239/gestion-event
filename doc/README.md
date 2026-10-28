# Documentation

| Fichier | Contenu |
|---|---|
| [`cahier-des-charges.md`](cahier-des-charges.md) | Sujet, description, cahier des charges, conception UML (étape 1) |
| [`cahier-des-charges.pdf`](cahier-des-charges.pdf) | Version imprimable du cahier des charges |
| [`uml/`](uml/) | Diagrammes UML : sources PlantUML (`.puml`) et images (`.png`) |

## Diagrammes UML

| Diagramme | Source |
|---|---|
| Cas d'utilisation | `uml/cas-utilisation.puml` |
| Classes | `uml/classes.puml` |
| Séquence : inviter une personne | `uml/sequence-invitation.puml` |

## Régénérer les images

Depuis le dossier `doc/` (Graphviz requis) :

```
java -jar plantuml.jar -tpng uml/*.puml
```
