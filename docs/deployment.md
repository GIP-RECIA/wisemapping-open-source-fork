# Déploiement

- [Déploiement](#déploiement)
  - [Snapshot](#snapshot)
  - [Release](#release)

## Snapshot

1. Vérifier la configuration `wisemapping-frontend/packages/config/index.ts`
2. [Compiler et déployer](development.md#build)

## Release

1. Stash les modifications de `wisemapping-frontend`
2. Vérifier la configuration `wisemapping-frontend/packages/config/index.ts`
3. Stash les modifications de `wisemapping-open-source`
4. [Commiter le changement de version](https://github.com/GIP-RECIA/wisemapping-open-source/commit/5b8d5725014d481231f7ac2ad49054fab81001f3)
5. Taguer le commit
6. [Compiler et déployer](development.md#build)
7. [Faire un commit pour la prochaine version](https://github.com/GIP-RECIA/wisemapping-open-source/commit/0d390072ad1e876b6cdaec0da976b381fcc04121)
