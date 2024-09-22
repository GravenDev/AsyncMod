# Étape 1: Construire l'application avec Gradle
FROM gradle:8.8-jdk22-alpine AS build

# Activer les fonctionnalités preview de Java 22 pour Gradle
ENV JAVA_TOOL_OPTIONS="--enable-preview"

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers Gradle et de projet dans le conteneur
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Télécharger les dépendances pour les mettre en cache
RUN ./gradlew dependencies --no-daemon

# Copier le reste des fichiers du projet dans le conteneur
COPY . .

# Compiler et construire le projet en JAR exécutable
RUN ./gradlew bootJar --no-daemon

# Étape 2: Créer l'image pour exécuter l'application
FROM eclipse-temurin:22-jdk AS runtime

# Activer les fonctionnalités preview lors de l'exécution

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Créer un volume pour externaliser le fichier de configuration
VOLUME /app/config

# Copier l'artefact JAR depuis l'étape de build
COPY --from=build /app/build/libs/*.jar app.jar

# Démarrer l'application
ENTRYPOINT ["java", "--enable-preview", "-jar", "/app/app.jar", "--spring.config.location=file:/app/config/application.properties"]
