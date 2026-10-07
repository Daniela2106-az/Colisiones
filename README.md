# Simulador de Físicas y Colisiones en Java

Aplicación de escritorio desarrollada en **Java** que simula un entorno interactivo en 2D con un motor de físicas y detección de colisiones en tiempo real. 

## 🚀 Características Principales
* **Motor de Físicas (`PhysicsEngine`):** Control de movimiento, trayectorias y comportamiento dinámico de los objetos en pantalla.
* **Sistema de Colisiones (`CollisionEngine`):** Gestión de *hitboxes* (`HitboxType`) para detectar choques precisos entre los elementos móviles y los obstáculos.
* **Interfaz Gráfica Interactiva:** Implementación basada en componentes de escritorio (`GamePanel` y `AppWindow`) con soporte para renderizado de elementos personalizados (como personajes y obstáculos).
* **Gestión de Entidades:** Arquitectura orientada a objetos que separa la lógica de los activos (`GameObject`, `Obstacle`, `CharacterType`).

---

## 📂 Estructura del Proyecto

El código fuente se encuentra organizado de la siguiente manera:

```text
src/
├── main/
│   ├── java/
│   │   ├── Main.java             # Punto de entrada de la aplicación
│   │   ├── AppWindow.java        # Ventana principal de la interfaz gráfica
│   │   ├── GamePanel.java        # Panel de renderizado y bucle del juego
│   │   ├── GameObject.java       # Clase base para entidades móviles
│   │   ├── CharacterType.java    # Tipos y configuración de personajes
│   │   ├── Obstacle.java         # Definición de obstáculos estáticos
│   │   ├── PhysicsEngine.java    # Motor de cálculo físico
│   │   └── CollisionEngine.java  # Lógica de detección de colisiones
│   └── resources/
│       ├── carro.png             # Recurso visual del vehículo
│       └── barbie.png            # Recurso visual del personaje
