# BackEnd EciCare

Repositorio BackEnd de la aplicación EciCare de la Escuela Colombiana de Ingeniería

![Build](https://github.com/ByteProgramming1/backend-ecicare/actions/workflows/build.yml/badge.svg)

## Descripción 📖

Este es el repositorio del Backend de la aplicación EciCare de la Escuela Colombiana de Ingeniería. EciCare es una plataforma innovadora diseñada para promover hábitos saludables y bienestar entre la comunidad universitaria. El backend está estructurado como un monorepo que contiene los siguientes microservicios:

- **Microservicio de Retos**: Gestiona los desafíos de salud y bienestar que los usuarios pueden aceptar y completar.
- **Microservicio de Módulos**: Administra los diferentes módulos de salud disponibles en la aplicación.
- **Microservicio de Premios**: Controla el sistema de recompensas para incentivar la participación de los usuarios.

Cada microservicio está diseñado para funcionar de manera independiente, pero se integran para proporcionar una experiencia completa y cohesiva a los usuarios de EciCare.

## Requisitos 📋

- [Git](https://git-scm.com/) - Control de versiones
- [Maven](https://maven.apache.org/) - Gestor de dependencias
- [Java](https://www.oracle.com/java/technologies/downloads/#java21) - Lenguaje de programación
- [Docker](https://www.docker.com/) - Motor de contenedores

> [!IMPORTANT]
> Es necesario tener instalado Git, Maven, Docker y Java 21 para poder ejecutar el proyecto.

## Ejecución 🚀

1. Clona el repositorio:
```bash
git clone https://github.com/ByteProgramming1/ecicare-backend-new.git
cd ecicare-backend-new
```
2. Construye y corre los contenedores:
```bash
docker-compose up -d
```
3. Utilice el siguiente comando para obtener un shell bash dentro del contenedor de la aplicación:
```bash
docker-compose exec app bash
```
4. Ejecute:
```bash
mvn clean verify
mvn springboot:run
```

## Licencia 📄

[![License: CC BY-SA 4.0](https://licensebuttons.net/l/by-sa/4.0/88x31.png)](https://creativecommons.org/licenses/by-sa/4.0/deed.es)

Este proyecto está bajo la licencia de Creative Commons Atribución-CompartirIgual 4.0 Internacional (CC BY-SA 4.0) - Ver el archivo [LICENSE](LICENSE) para más detalles.
