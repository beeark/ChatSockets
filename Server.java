import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999); // Inicia el servidor en el puerto 9999
            pool = Executors.newCachedThreadPool(); // Pool de hilos para manejar múltiples conexiones
            while (!done) {
                Socket client = server.accept(); // Acepta nuevas conexiones de clientes
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler); // Agrega la nueva conexión a la lista de conexiones
                pool.execute(handler); // Ejecuta el handler de la nueva conexión en un nuevo hilo
            }
        } catch (Exception e) {
            shutdown(); // Apaga el servidor en caso de una excepción
        }
    }

    // Método para enviar un mensaje a todos los clientes conectados
    public synchronized void broadcast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    // Método para apagar el servidor y cerrar todas las conexiones
    public synchronized void shutdown() {
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            // Ignorar
        }
    }

    // Clase interna que maneja la conexión de un cliente específico
    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        private boolean active;

        public ConnectionHandler(Socket client) {
            this.client = client;
            this.active = true;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true); // Inicializa el PrintWriter para enviar mensajes
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Inicializa el BufferedReader para recibir mensajes
                out.println("Ingrese su Nick para chatear");
                nickname = in.readLine(); // Lee el nickname del cliente
                System.out.println(nickname + " Online!");
                broadcast(nickname + " Online en el chat!");
                broadcastConnectedUsers();

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/nick")) {
                        // Cambia el nickname del cliente
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " Cambió su Nick a: " + messageSplit[1]);
                            System.out.println(nickname + " Renombrado " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Nick cambiado exitosamente a " + nickname);
                        } else {
                            out.println("No se proporcionó nickname");
                        }
                    } else if (message.startsWith("chao")) {
                        // Desconecta al cliente
                        broadcast(nickname + " Se ha desconectado");
                        shutdown();
                    } else {
                        // Envía un mensaje normal
                        broadcast(nickname + ": " + message);
                    }
                }
            } catch (IOException e) {
                // Ignorar
            } finally {
                shutdown(); // Asegura que el handler se apague al finalizar el run
            }
        }

        // Método para enviar un mensaje al cliente
        public void sendMessage(String message) {
            if (active) {
                out.println(message);
            }
        }

        // Método para cerrar la conexión del cliente
        public void shutdown() {
            if (active) {
                active = false;
                try {
                    in.close();
                    out.close();
                    if (!client.isClosed()) {
                        client.close();
                    }
                    // Remover este ConnectionHandler de la lista de conexiones
                    connections.remove(this);
                    // Imprimir en pantalla quién se retiró y quiénes están conectados
                    System.out.println(nickname + " Se ha desconectado.");
                    broadcastConnectedUsers();
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }

        // Método para enviar la lista de usuarios conectados
        public void broadcastConnectedUsers() {
            StringBuilder userList = new StringBuilder("Usuarios conectados: ");
            for (ConnectionHandler ch : connections) {
                if (ch != null && ch.nickname != null) {
                    userList.append(ch.nickname).append(" ");
                }
            }
            String userListString = userList.toString();
            broadcast(userListString);
            System.out.println(userListString);
        }
    }

    // Método principal para iniciar el servidor
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
