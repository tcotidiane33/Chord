package bdgl.jchord;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

public class Main {
    public static final String HASH_FUNCTION = "SHA-1";
    public static final int KEY_LENGTH = 160;
    public static final int NUM_OF_NODES = 5;
    private static final Chord chord = new Chord();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // Rediriger la sortie standard vers le fichier result.log
        PrintStream out = System.out;
        out = new PrintStream("result.log");

        
        initializeChord();
        loadAndDisplayData();
        handleUserCommands();
    }

    private static void loadAndDisplayData() {
        // Implémentation pour charger et afficher les données
        // Cette méthode pourrait charger les données à partir d'une source (fichiers, base de données, etc.)
        // et les afficher dans la console ou l'interface utilisateur
        System.out.println("Chargement et affichage des données...");

        // Exemple : Charger les données à partir d'une source et les afficher
        // Supposons que vous ayez une méthode loadDataFromSource() qui charge les données
        // à partir d'une source et une méthode displayData(List<Data> data) qui affiche les données
        // Vous devrez remplacer ces méthodes par les vôtres
        List<Data> data = loadDataFromSource();
        displayData(data);
    }

    // Méthode fictive pour charger les données à partir d'une source
    private static List<Data> loadDataFromSource() {
        // Ici, vous pouvez mettre la logique pour charger les données à partir d'une source
        // telle qu'un fichier, une base de données, etc.
        // Cette méthode doit retourner une liste de données
        // Remplacez le contenu par votre propre logique
        List<Data> data = new ArrayList<>();
        data.add(new Data("Donnée 1"));
        data.add(new Data("Donnée 2"));
        data.add(new Data("Donnée 3"));
        return data;
    }

    // Méthode fictive pour afficher les données
    private static void displayData(List<Data> data) {
        // Ici, vous pouvez mettre la logique pour afficher les données dans la console
        // ou l'interface utilisateur
        // Remplacez le contenu par votre propre logique
        for (Data d : data) {
            System.out.println(d);
        }
    }

    // Classe fictive représentant des données
    static class Data {
        private String value;

        public Data(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    private static void initializeChord() throws Exception {
        Hash.setFunction(Hash.HashFunction.valueOf("SHA1")); // Correction ici
        Hash.setKeyLength(KEY_LENGTH);
        String host = "localhost";
        int port = 9000;
        for (int i = 0; i < NUM_OF_NODES; i++) {
            URL url = new URL("http", host, port + i, "");
            try {
                chord.createNode(url.toString());
            } catch (ChordException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static void loadNodes() throws Exception {
        System.out.println("Chargement de données sur les nœuds...");
        List<Map<String, String>> partitions = partitionData();
        for (int i = 0; i < partitions.size(); i++) {
            ChordNode node = chord.getNode(i % NUM_OF_NODES);
            if (node != null) {
                for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                    node.addData(entry.getKey(), entry.getValue());
                }
                System.out.println("Données chargées sur le nœud " + node.getNodeId() + ": " + partitions.get(i).size() + " éléments.");
            } else {
                System.out.println("Nœud introuvable pour la partition " + i);
            }
        }

        System.out.println("Chargement de données terminé.");
        int totalBytes = getTotalBytes(partitions);
        System.out.println("Nombre total d'octets stockés sur tous les nœuds : " + totalBytes);
    }


    private static void handleUserCommands() throws Exception {
        try {
            while (true) {
                printHelp();
                System.out.print("Entrez une commande : ");
                String command = scanner.nextLine().trim().toLowerCase();
                switch (command) {
                    case "create":
                        System.out.println("Commande Create sélectionnée.");
                        System.out.print("Entrez l'identifiant du nouveau nœud : ");
                        String newNodeId = scanner.next();
                        createNode(Integer.parseInt(newNodeId));
                        break;
                    case "drop":
                        System.out.println("Commande Drop sélectionnée.");
                        dropNode();
                        break;
                    case "list":
                        System.out.println("Commande List sélectionnée.");
                        listNodes();
                        break;
                    case "read":
                        System.out.println("Commande Read sélectionnée.");
                        readNode();
                        break;
                    case "load":
                        System.out.println("Commande Load sélectionnée.");
                        loadNodes();
                        break;
                    case "partition":
                        System.out.println("Commande Partition sélectionnée.");
                        displayPartitions(partitionData());
                        break;
                    case "exit":
                        System.out.println("Sortie du programme.");
                        System.exit(0);
                    default:
                        System.out.println("Commande non reconnue.");
                }
            }
        } finally {
            scanner.close();
        }
    }


    private static List<Map<String, String>> fetchPartitions() {
        // Implémentation fictive pour récupérer les partitions
        // Cette méthode pourrait interagir avec votre système Chord pour récupérer les partitions
        // ou simplement simuler des données pour les partitions
        return null; // Placeholder, remplacez par l'implémentation réelle
    }

    private static void displayPartitions(List<Map<String, String>> maps) {
        // Affichage des partitions
        if (maps == null || maps.isEmpty()) {
            System.out.println("Aucune partition à afficher.");
        } else {
            System.out.println("Partitions :");
            for (int i = 0; i < maps.size(); i++) {
                System.out.println("Partition " + (i + 1) + ":");
                Map<String, String> partition = maps.get(i);
                for (Map.Entry<String, String> entry : partition.entrySet()) {
                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
                System.out.println(); // Ajoute une ligne vide entre les partitions
            }
        }
    }


    private static void listNodes() {
        // Implémentation pour lister les nœuds
        // Cette méthode pourrait obtenir la liste des nœuds disponibles dans le système Chord
        // et les afficher dans la console ou l'interface utilisateur
        System.out.println("Liste des nœuds :");
        List<String> nodeList = NodeList.getNodeList(); // Supposons que vous ayez une classe NodeList avec une méthode statique getNodeList()
        for (String node : nodeList) {
            System.out.println(node);
        }
    }
    private static void createNode(int idx) {
        try {
            if (idx >= 0 && idx < NUM_OF_NODES) {
                URL url = urlBuilder(idx);
                chord.createNode(url.toString());
                System.out.println("Nœud créé avec succès.");
            } else {
                System.out.println("ID de nœud invalide.");
            }
        } catch (ChordException e) {
            System.out.println("Échec de la création du nœud.");
            e.printStackTrace();
        }
    }

    private static URL urlBuilder(int idx) {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            int port = 9000 + idx;
            return new URL("http", host, port, "");
        } catch (Exception e) {
            System.out.println("Impossible de construire l'URL.");
            e.printStackTrace();
            return null;
        }
    }

    

    private static void dropNode() {
        ChordNode node = null;
        node = null;
        if (node != null) {
            closeConnections(node);
            releaseResources(node);
            ChordNode pred = node.getPredecessor();
            ChordNode succ = node.getSuccessor();
            if (pred != null) {
                pred.setSuccessor(succ);
            }
            if (succ != null) {
                succ.setPredecessor(pred);
            }
            node.getFingerTable().clear();
            Map<String, String> nodeData = node.getAllData();
            for (Map.Entry<String, String> entry : nodeData.entrySet()) {
                chord.addData(entry.getKey(), entry.getValue());
            }
            chord.removeNode(node);
            System.out.println("Nœud supprimé avec succès.");
        } else {
            System.out.println("Nœud introuvable !");
        }
    }

    private static int getTotalBytes(List<Map<String, String>> partitions) {
        int totalBytes = 0;
        for (Map<String, String> partition : partitions) {
            for (String value : partition.values()) {
                totalBytes += value.getBytes().length;
            }
        }
        return totalBytes;
    }

    private static void readNode() {
        System.out.print("Entrez l'index du nœud à lire : ");
        int index = scanner.nextInt();
        ChordNode node = chord.getNode(index);
        if (node != null) {
            System.out.print("Voulez-vous afficher la liste des fichiers (List) ou le contenu d'un fichier spécifique (File) ? ");
            String option = scanner.next().trim().toLowerCase();
            if (option.equals("list")) {
                listFiles(node);
            } else if (option.equals("file")) {
                System.out.print("Entrez le nom du fichier à lire : ");
                String fileName = scanner.next().trim();
                node.readFile(node, fileName);
            } else {
                System.out.println("Option non valide.");
            }
        } else {
            System.out.println("Nœud introuvable.");
        }
    }

    private static void listFiles(ChordNode node) {
        if (node != null) {
            List<String> files = node.getFiles();
            if (files != null) {
                System.out.printf("Liste des fichiers sur le nœud %s :%n", node.getNodeId());
                if (!files.isEmpty()) {
                    for (String file : files) {
                        System.out.println(file);
                    }
                } else {
                    System.out.println("Aucun fichier trouvé sur ce nœud.");
                }
            } else {
                System.out.println("Aucun fichier trouvé sur ce nœud.");
            }
        } else {
            System.out.println("Nœud introuvable.");
        }
    }

    private static List<Map<String, String>> partitionData() {
        List<Map<String, String>> partitions = new ArrayList<>();

        // Générer un nombre aléatoire de partitions
        Random random = new Random();
        int numPartitions = random.nextInt(5) + 1; // Nombre aléatoire entre 1 et 5 partitions

        // Générer des données aléatoires pour chaque partition
        for (int i = 0; i < numPartitions; i++) {
            Map<String, String> partition = new HashMap<>();
            int numRecords = random.nextInt(5) + 1; // Nombre aléatoire entre 1 et 5 enregistrements par partition
            for (int j = 0; j < numRecords; j++) {
                String key = "key" + j;
                String value = "value" + j;
                partition.put(key, value);
            }
            partitions.add(partition);
        }

        return partitions;
    }

    private static void printHelp() {
        String helpMessage = "====== Available commands ======\n" +
                "=================================\n" +
                "create: Create a new node.\n" +
                "=================================\n" +
                "drop: Drop a node.\n" +
                "=================================\n" +
                "list: List all nodes.\n" +
                "=================================\n" +
                "read: Read data from a node.\n" +
                "=================================\n" +
                "load: Load data onto nodes.\n" +
                "=================================\n" +
                "partition: Display data partitions.\n" +
                "=================================\n" +
                "exit: Exit the program.\n" +
                "=================================";
        System.out.println(helpMessage);
    }

    private static void closeConnections(ChordNode node) {
        ChordNode successor = node.getSuccessor();
        if (successor != null) {
            // Logique pour fermer la connexion avec le successeur
            // Exemple : ConnectionManager.closeConnection(node, successor);
            System.out.println("Fermeture de la connexion avec le successeur.");
        }

        ChordNode predecessor = node.getPredecessor();
        if (predecessor != null) {
            // Logique pour fermer la connexion avec le prédécesseur
            // Exemple : ConnectionManager.closeConnection(node, predecessor);
            System.out.println("Fermeture de la connexion avec le prédécesseur.");
        }

        // Autres fermetures de connexions nécessaires selon votre architecture
    }

    private static void releaseResources(ChordNode node) {
        List<String> files = node.getFiles();
        if (files != null && !files.isEmpty()) {
            for (String file : files) {
                // Logique pour fermer chaque fichier
                // Exemple : FileManager.closeFile(file);
                System.out.println("Fermeture du fichier : " + file);
            }
        }

        // Autres libérations de ressources nécessaires selon votre architecture
    }


}

