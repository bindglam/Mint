# 💴 Mint

[![CodeFactor](https://www.codefactor.io/repository/github/bindglam/mint/badge)](https://www.codefactor.io/repository/github/bindglam/mint)

Mint는 당신의 마인크래프트 서버에 여러가지 화폐와 경제 시스템을 추가할 수 있도록 도와주는 Paper 플러그인입니다.

Mint is a Paper plugin that helps you add various currencies and economic systems to your Minecraft server.

## 🚀 주요 기능
* **다중 화폐 지원**: 여러개의 화폐를 동시에 사용할 수 있습니다.
* **입출금 내역**: 플레이어의 모든 금융 활동은 입출금 내역에 보존되고, 이는 어떠한 서버, 플러그인도 우회할 수 없도록 고안되었습니다.
* **최고의 안정성**: 서버가 갑자기 꺼져도 데이터가 보존되는 안정성을 가지고 있습니다.
* **성능 중심**: 이 모든 시스템은 비동기로 작동하여, 서버의 TPS에는 영향이 전혀 가지 않습니다.
* **경량화**: 대부분의 서버에서 필요로할만한 기능들만 추가하여, 매우 경량화되어 있습니다.
* **데이터베이스 지원**: MySQL, SQLite 같은 데이터베이스를 지원합니다.

## 🚀 Key Features
* **Multi-Currency Support**: Supports the simultaneous use of multiple currencies.
* **Transaction Logs**: All financial activities are recorded in transaction logs, designed to ensure that no server process or plugin can bypass them.
* **Superior Stability**: Ensures data integrity even in the event of a sudden server crash or shutdown.
* **Performance-Oriented**: The entire system operates asynchronously, ensuring zero impact on server TPS.
* **Lightweight**: Optimized to include only the essential features required by most servers, keeping the plugin extremely lightweight.
* **Database Support**: Supports standard databases such as MySQL and SQLite.

## 🧑🏻‍💻 API

build.gradle.kts
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.bindglam.Mint:api:<VERSION>")
}
```

## Build

이 프로젝트는 Gradle로 빌드됩니다.

This project can be built using Gradle.

```bash
./gradlew build
```

## License

이 프로젝트는 [LICENSE](LICENSE) 파일에서 명시된 라이선스를 따릅니다.

This project follows the license specified in the [LICENSE](LICENSE) file.
