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

Para ejecutar el proyecto, es necesario tener un contenedor de MySQL corriendo. El siguiente comando ejecuta un contenedor de MySQL:

```bash
docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=eci-care -p 3306:3306 -d mysql:latest
```

## Licencia 📄

[![License: CC BY-SA 4.0](https://licensebuttons.net/l/by-sa/4.0/88x31.png)](https://creativecommons.org/licenses/by-sa/4.0/deed.es)

Este proyecto está bajo la licencia de Creative Commons Atribución-CompartirIgual 4.0 Internacional (CC BY-SA 4.0) - Ver el archivo [LICENSE](LICENSE) para más detalles.
