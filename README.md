## Chat Bidireccional con Sockets en Java

Este proyecto implementa un sistema de comunicación vía chat bidireccional utilizando sockets en Java. Está compuesto por dos programas principales: un servidor y un cliente. La aplicación permite la conexión de múltiples clientes al servidor, ofreciendo funcionalidades básicas de chat en tiempo real, gestión de usuarios y manejo de desconexiones.

#### Funcionalidades Principales

1. **Servidor**:
   - Acepta conexiones de múltiples clientes.
   - Gestiona una lista de usuarios conectados.
   - Transmite mensajes de un cliente a todos los demás clientes conectados (broadcast).
   - Actualiza y notifica la lista de usuarios conectados cada vez que un usuario se conecta o desconecta.
   - Permite el cambio de nickname por parte de los clientes.

2. **Cliente**:
   - Se conecta al servidor especificando la IP y el puerto.
   - Envía y recibe mensajes del servidor.
   - Permite cambiar el nickname.
   - Gestiona la desconexión del servidor con el comando `/quit`.

#### Estructura del Proyecto

##### Servidor (`Server.java`)

- **`Server`**: Clase principal que inicializa el servidor y acepta conexiones de clientes.
  - `run()`: Método principal que escucha nuevas conexiones y las gestiona mediante `ConnectionHandler`.
  - `broadcast(String message)`: Envía un mensaje a todos los clientes conectados.
  - `shutdown()`: Apaga el servidor y cierra todas las conexiones.
  
- **`ConnectionHandler`**: Clase interna que maneja cada conexión de cliente.
  - `run()`: Gestiona la comunicación con el cliente, incluyendo el envío y recepción de mensajes.
  - `sendMessage(String message)`: Envía un mensaje al cliente específico.
  - `shutdown()`: Cierra la conexión del cliente y actualiza la lista de usuarios conectados.

##### Cliente (`Client.java`)

- **`Client`**: Clase principal que conecta al cliente con el servidor y gestiona la comunicación.
  - `run()`: Establece la conexión con el servidor y lanza un hilo para gestionar la entrada del usuario.
  - `shutdown()`: Cierra la conexión con el servidor y los recursos asociados.
  
- **`InputHandler`**: Clase interna que maneja la entrada del usuario desde la consola.
  - `run()`: Lee los mensajes del usuario y los envía al servidor. Gestiona el comando `/quit` para desconectar el cliente.

#### Ejecución del Proyecto

1. **Servidor**:
   - Compilar y ejecutar `Server.java`.
   - El servidor estará atento a las conexiones en el puerto 9999.

   ```bash
   javac Server.java
   java Server
   ```

2. **Cliente**:
   - Compilar y ejecutar `Client.java`.
   - Se solicitará la IP del servidor y el puerto (ejemplo: `127.0.0.1` y `9999`).

   ```bash
   javac Client.java
   java Client
   ```

#### Ejemplo de Uso

1. **Iniciar el Servidor**:
   - El servidor empieza a escuchar en el puerto 9999 y espera conexiones de clientes.

   ```bash
   java Server
   ```

2. **Conectar un Cliente**:
   - Ejecutar el cliente y proporcionar la IP y el puerto del servidor.

   ```bash
   java Client
   ```
   - Ingresar el nickname cuando sea solicitado.
   - Enviar mensajes y cambiar el nickname utilizando el comando `/nick <nuevo_nick>`.
   - Desconectarse utilizando el comando `/quit`.

#### Manejo de Errores

- Si el servidor no está disponible, el cliente mostrará un mensaje indicando que no se pudo conectar.
- El servidor notifica a todos los clientes cuando un usuario se desconecta y actualiza la lista de usuarios conectados.

#### Mejoras Futuras

- Implementar cifrado en las comunicaciones para asegurar la privacidad de los mensajes.
- Añadir soporte para salas de chat privadas.
- Mejorar la interfaz de usuario para hacerlo más amigable.
- Implementar autenticación de usuarios.

#### Contribuciones

Las contribuciones a este proyecto son bienvenidas. Por favor, abra un "issue" para discutir cualquier cambio importante antes de enviar un pull request.

#### Licencia

Este proyecto está licenciado bajo la Licencia MIT. Vea el archivo `LICENSE` para más detalles.

---

¡Gracias por revisar este proyecto! Si tiene alguna pregunta o sugerencia, no dude en ponerse en contacto.
