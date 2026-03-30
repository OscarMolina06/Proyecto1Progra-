# Proyecto 1: Sistema de Consulta de Padrón Electoral

Este proyecto consiste en un sistema distribuido de consulta de datos del Padrón Electoral de Costa Rica. Permite buscar información de ciudadanos (Nombre, Apellidos y Ubicación) a través de múltiples protocolos de red y una interfaz gráfica amigable.

---

##  Integrantes del Grupo
* **Oscar Molina** 
* **Travis Rivera**
* **Rhoswen Mora**
* **Sheerry Avalos**
* **Britany Pineda**
---

##  Funcionamiento del Sistema

El sistema opera bajo una arquitectura de **Capas (N-Tier)**, lo que separa la base de datos física de la forma en que el usuario interactúa con ella.

###  Componentes Principales:
1. **Servidor HTTP (Puerto 9090):** Permite consultas mediante cualquier navegador web usando parámetros URL.
2. **Servidor TCP (Puerto 5555):** Un canal de comunicación de bajo nivel, rápido y seguro, que procesa comandos de texto crudo.
3. **Interfaz Gráfica (Swing):** Un cliente nativo que permite al usuario digitar la cédula, elegir el formato (JSON/XML) y ver la respuesta de inmediato.
4. **Lógica de Negocio:** Procesa los archivos de texto planos (`PADRON_COMPLETO.txt` y `distelec.txt`) realizando búsquedas lineales y cruce de datos con Mapas (HashMaps).

---

##  Cómo utilizar el sistema

### 1. Ejecución
* Abre el proyecto en **NetBeans**.
* Asegúrate de que los archivos `.txt` estén en la carpeta `resources/`.
* Ejecuta la clase `Main.java`.

### 2. Consultas vía Web (HTTP)
Puedes copiar la URL generada en la interfaz o escribir manualmente en el navegador:
`http://localhost:9090/padron?cedula=101240037&format=json`

### 3. Consultas vía Terminal (TCP)
Conéctate mediante Telnet o Hercules al puerto `5555` y envía:
`GET|101240037|XML`

---

##  Tecnologías Utilizadas
* **Lenguaje:** Java 17+
* **Concurrencia:** Java ExecutorService (Pool de Hilos para múltiples clientes).
* **Formatos de Salida:** JSON y XML (Serialización manual).
* **Protocolos:** TCP/IP y HTTP 1.1.
* **Interfaz:** Java Swing.

---

##  Estructura del Proyecto
* `Datos`: Repositorios para lectura de archivos.
* `DTO`: Objetos de transferencia de datos (Data Transfer Objects).
* `Entidad`: Clases modelo (Persona, Dirección).
* `Logica`: Servicio principal de búsqueda.
* `Presentacion`: Servidores y GUI.
* `Util`: Serializadores de formato.
