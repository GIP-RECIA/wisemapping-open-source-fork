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

extended-uportal.header.component-path=
extended-uportal.header.props.template-api-path=
extended-uportal.header.props.fname=

extended-uportal.footer.component-path=
extended-uportal.footer.props.template-api-path=
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
