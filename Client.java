import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese la IP del servidor: ");
            String serverIp = consoleReader.readLine();
            System.out.print("Ingrese el puerto del servidor: ");
            int serverPort = Integer.parseInt(consoleReader.readLine());

            try {
                client = new Socket(serverIp, serverPort); // Conecta al servidor en el puerto especificado
                out = new PrintWriter(client.getOutputStream(), true); // Inicializa el PrintWriter para enviar mensajes
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Inicializa el BufferedReader para recibir mensajes

                InputHandler inHandler = new InputHandler();
                Thread t = new Thread(inHandler); // Crea un hilo para manejar la entrada del usuario
                t.start();

                String inMessageString;
                while ((inMessageString = in.readLine()) != null) {
                    System.out.println(inMessageString); // Imprime mensajes recibidos del servidor
                }
            } catch (UnknownHostException e) {
                System.out.println("Servidor no encontrado. Por favor, verifique la IP y el puerto.");
            } catch (IOException e) {
                System.out.println("No se pudo conectar al servidor. Por favor, intente nuevamente más tarde.");
            }
        } catch (IOException e) {
            System.out.println("Error leyendo la entrada del usuario.");
        } finally {
            shutdown(); // Asegura que el cliente se apague correctamente
        }
    }

    // Método para cerrar la conexión del cliente
    public void shutdown() {
        done = true;
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // Ignorar
        }
    }

    // Clase interna que maneja la entrada del usuario desde la consola
    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String messageString = inReader.readLine();
                    if (messageString.equals("chao")) {
                        out.println(messageString); // Envía el mensaje de desconexión al servidor
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(messageString); // Envía mensajes normales al servidor
                    }
                }
            } catch (IOException e) {
                shutdown(); // Apaga el cliente en caso de una excepción
            }
        }
    }

    // Método principal para iniciar el cliente
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
