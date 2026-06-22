# Processus de traitement SAV

## Étapes générales
Le traitement d'un dossier SAV suit les étapes suivantes :
1. création du dossier par le client ;
2. analyse automatique initiale par l'IA ;
3. revue du dossier par un agent SAV ;
4. demande d'informations complémentaires si nécessaire ;
5. diagnostic technique ;
6. réparation, remplacement ou rejet ;
7. clôture du dossier.

## Informations nécessaires
Avant toute décision, l'agent doit vérifier :
- le produit concerné ;
- la marque ;
- le modèle ;
- le numéro de série ;
- la date d'achat ;
- la date de fin de garantie ;
- la description exacte du problème ;
- l'historique du dossier.

## Statuts utilisés
Les statuts principaux sont :
- CREATED : dossier créé ;
- IN_REVIEW : dossier en analyse ;
- WAITING_CUSTOMER : informations client manquantes ;
- ASSIGNED_TO_TECHNICIAN : dossier assigné à un technicien ;
- IN_REPAIR : réparation en cours ;
- RESOLVED : problème résolu ;
- CLOSED : dossier clôturé ;
- REJECTED : demande rejetée.

## Recommandation IA
L'IA doit toujours proposer des actions exploitables par l'agent SAV. Elle ne doit pas décider seule du remplacement, du remboursement ou du rejet. Ces décisions restent humaines.