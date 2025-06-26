# Patrones de Diseño en HotelBooking

Este proyecto utiliza varios patrones de diseño clásicos de software, especialmente gracias a la arquitectura de Spring Boot y Java. Aquí te explico los principales, con palabras simples:

---

## 1. MVC (Modelo-Vista-Controlador)
- **Modelo:** Son las clases que representan los datos (por ejemplo, `Habitacion`, `Usuario`).
- **Vista:** Es el frontend (HTML, CSS, JS) que ve el usuario.
- **Controlador:** Son las clases que reciben las peticiones del usuario y deciden qué hacer (por ejemplo, `HabitacionController`).

---

## 2. Repository
- Permite acceder a la base de datos usando objetos Java, sin escribir SQL.
- Ejemplo: `HabitacionRepository` te deja buscar, guardar o borrar habitaciones fácilmente.

---

## 3. Service
- Separa la lógica de negocio de los controladores y los repositorios.
- Ejemplo: `ReservaService` contiene las reglas para crear o cancelar reservas.

---

## 4. DTO (Data Transfer Object)
- Son clases que sirven para transferir datos entre el backend y el frontend, sin exponer directamente las entidades.
- Ejemplo: `UsuarioDTO` envía solo la información necesaria del usuario.

---

## 5. Singleton
- Los servicios y repositorios de Spring son instancias únicas (singleton) en toda la aplicación.

---

## 6. Factory (por Spring)
- Spring se encarga de crear e inyectar las instancias de las clases automáticamente.



