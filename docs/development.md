# Développement

- [Développement](#développement)
  - [Prérequis](#prérequis)
  - [Initialisation](#initialisation)
  - [Dev](#dev)
  - [Build](#build)
  - [Start](#start)

## Prérequis

- nvm
- sdkman

## Initialisation

```bash
./scripts/init.sh
```

Compléter les propriétés suivantes dans le `app.properties` :

```properties
database.url=
database.username=
database.password=

site.homepage=/wisemapping/
site.static.js.url=/wisemapping/static

security.type=cas

security.cas.authorizedDomainNames=

security.cas.service.login=
security.cas.service.logout=
security.cas.url.prefix=

extendedUportalHeader.componentPath=
extendedUportalHeader.serviceName=Carte Mentale
extendedUportalHeader.contextApiUrl=
extendedUportalHeader.signOutUrl=
extendedUportalHeader.defaultOrgLogoPath=
extendedUportalHeader.defaultAvatarPath=
extendedUportalHeader.defaultOrgIconPath=
extendedUportalHeader.favoriteApiUrl=
extendedUportalHeader.layoutApiUrl=
extendedUportalHeader.organizationApiUrl=
extendedUportalHeader.portletApiUrl=
extendedUportalHeader.userInfoApiUrl=
extendedUportalHeader.userInfoPortletUrl=
extendedUportalHeader.sessionApiUrl=
extendedUportalHeader.templateApiPath=
extendedUportalHeader.switchOrgPortletUrl=
extendedUportalHeader.favoritesPortletCardSize=small
extendedUportalHeader.gridPortletCardSize=auto
extendedUportalHeader.hideActionMode=never
extendedUportalHeader.showFavoritesInSlider=true
extendedUportalHeader.returnHomeTitle=Aller à l\'accueil
extendedUportalHeader.returnHomeTarget=_self
extendedUportalHeader.iconType=nine-square

extendedUportalFooter.componentPath=
extendedUportalFooter.templateApiPath=
```

## Dev

1. Personnaliser le path et le port de Jetty (`wise-webapp/pom.xml`)

```xml
<plugin>
    ...
    <artifactId>jetty-maven-plugin</artifactId>
    ...
    <configuration>
        <webApp>
            <contextPath>/wisemapping</contextPath>
        </webApp>
        ...
        <httpConnector>
            <port>8090</port>
        </httpConnector>
        ...
    </configuration>
    <executions>
        <execution>
            <id>run-forked</id>
            ...
            <configuration>
                ...
                <jvmArgs>-Ddatabase.base.url=${project.build.directory} -Djetty.port=8090</jvmArgs>
            </configuration>
        </execution>
        ...
    </executions>
</plugin>
```

2. Personnaliser le path et le nom de l'application niveau frontend (`wisemapping-frontend/packages/config/index.ts`)

```js
const URI = '/wisemapping';
const APP_NAME = 'Carte Mentale';
```

**Backend**

1. [Compiler le back](#build)
2. [Lancer le serveur jetty](#start)

**Frontend**

```bash
yarn workspace @wisemapping/webapp start
```

## Build

Utilitaire de compilation et de déploiement du front et du back.

```bash
./scripts/build.sh
```

## Start

Lancement après compilation.

```shell
./scripts/start.sh
```
