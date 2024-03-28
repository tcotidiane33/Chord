import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

public class Main {

    public static final String FONCTION_HASH = "SHA-1";
    public static final int LONGUEUR_CLE = 160;
    public static final int NOMBRE_DE_NOEUDS = 5;
    private static Chord chord = new Chord();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        initializeChord();
        loadAndDisplayData();
        handleUserCommands();
    }

    private static void initializeChord() throws Exception {
        Hash.setFunction(FONCTION_HASH);
        Hash.setKeyLength(LONGUEUR_CLE);

        String host = "localhost";
        int port = 9000;

        for (int i = 0; i < NOMBRE_DE_NOEUDS; i++) {
            URL url = new URL("http", host, port + i, "");
            try {
                chord.createNode(url.toString());
            } catch (ChordException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static void loadAndDisplayData() throws Exception {
        List<Map<String, String>> partitions = partitionData();
        int totalBytes = 0;

        System.out.println("Chargement de données sur les nœuds...");

        for (int i = 0; i < partitions.size(); i++) {
            ChordNode node = chord.getNode(i % NOMBRE_DE_NOEUDS);
            if (node != null) {
                Map<String, String> partition = partitions.get(i);
                for (Map.Entry<String, String> entry : partition.entrySet()) {
                    node.addData(entry.getKey(), entry.getValue());
                    totalBytes += entry.getKey().getBytes().length + entry.getValue().getBytes().length;
                }
                System.out.println("Données chargées sur le nœud " + node.getNodeId() + ": " + partition.size() + " éléments.");
            } else {
                System.out.println("Nœud introuvable pour la partition " + i);
            }
        }

        System.out.println("Chargement de données terminé.");
        System.out.println("Nombre total d'octets stockés sur tous les nœuds : " + totalBytes);
    }


    private static void handleUserCommands() throws Exception {
        while (true) {
            System.out.print("Entrez une commande ($/>Create - |$/>Drop - |$/>List - |$/>Load - |$/>Read - |$/>Partition - |$/>Exit ) : ");
            String commande = scanner.nextLine().trim().toLowerCase();


            switch (commande.toLowerCase()) {
                case "create":
                    System.out.println("Commande Create sélectionnée.");
                    createNode();
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
                    scanner.close();
                    System.out.println("Sortie du programme.");
                    System.exit(0);
                default:
                    System.out.println("Commande non reconnue.");
            }
        }
    }

    private static void createNodes(String host, int port) throws Exception {
        for (int i = 0; i < NOMBRE_DE_NOEUDS; i++) {
            URL url = new URL("http", host, port + i, "");
            try {
                chord.createNode(url.toString());
            } catch (ChordException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static void createNode() throws Exception {
        System.out.print("Entrez l'index du nœud à créer : ");
        int index = Integer.parseInt(scanner.nextLine().trim());
        URL url = new URL("http", InetAddress.getLocalHost().getHostAddress(), 9000 + index, "");
        chord.createNode(url.toString());
        System.out.println("Nœud créé avec succès à l'index " + index + ".");
    }

    private static void dropNode() throws Exception {
        System.out.print("Entrez l'index du nœud à supprimer : ");
        int index = Integer.parseInt(scanner.nextLine().trim());
        chord.dropNode(index);
        System.out.println("Nœud supprimé avec succès à l'index " + index + ".");
    }

    private static void listNodes() {
        System.out.println("Liste des nœuds :");
        int size = chord.size();
        for (int i = 0; i < size; i++) {
            ChordNode node = chord.getNode(i);
            if (node != null) {
                System.out.println("Index : " + i + ", Node ID : " + node.getNodeId());
            } else {
                System.out.println("Index : " + i + ", Aucun nœud trouvé.");
            }
        }
    }

    private static void readNode() throws Exception {
        System.out.print("Entrez l'index du nœud à lire : ");
        int index = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Voulez-vous afficher la liste des fichiers (List) ou le contenu d'un fichier spécifique (File) ? ");
        String option = scanner.nextLine().trim().toLowerCase();
        if (option.equals("list")) {
            listFiles(chord.getNode(index));
        } else if (option.equals("file")) {
            System.out.print("Entrez le nom du fichier à lire : ");
            String fileName = scanner.nextLine().trim();
            readFile(chord.getNode(index), fileName);
        } else {
            System.out.println("Option non valide.");
        }
    }




private static void listFiles(ChordNode node) {
    if (node != null) {
        List<String> files = node.getFiles();
        if (files != null) {
            System.out.printf("List of files on the node: %s%n", node.getNodeUrl());
            if (!files.isEmpty()) {
                for (String file : files) {
                    System.out.println(file);
                }
            } else {
                System.out.println("No file found on this node.");
            }
        } else {
            System.out.println("No file found on this node.");
        }
    } else {
        System.out.println("Node not found.");
    }
}
private static void readFile(ChordNode node, String fileName) {
    if (node != null) {
        String content = node.readFile(node, fileName);
        if (content != null) {
            System.out.println("Contenu du fichier " + fileName + " sur le nœud : " + node.getNodeId());
            System.out.println(content);
        } else {
            System.out.println("Le fichier " + fileName + " n'a pas été trouvé sur le nœud : " + node.getNodeId());
        }
    } else {
        System.out.println("Nœud introuvable.");
    }
}

private static void loadNodes() throws Exception {
    System.out.println("Chargement de données sur les nœuds...");
    List<Map<String, String>> partitions = partitionData();
    for (int i = 0; i < partitions.size(); i++) {
        ChordNode node = chord.getNode(i % NOMBRE_DE_NOEUDS);
        if (node != null) {
            int dataSize = 0;
            for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                node.addData(entry.getKey(), entry.getValue());
                dataSize += entry.getValue().getBytes().length;
            }
            System.out.println("Données chargées sur le nœud " + node.getNodeUrl() + ": " + partitions.get(i).size() + " éléments.");
        } else {
            System.out.println("Nœud introuvable pour la partition " + i);
            boolean loaded = false;
            for (int j = i + 1; j < NOMBRE_DE_NOEUDS; j++) {
                ChordNode nextNode = chord.getNode(j);
                if (nextNode != null) {
                    int dataSize = 0;
                    for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                        nextNode.addData(entry.getKey(), entry.getValue());
                        dataSize += entry.getValue().getBytes().length;
                    }
                    System.out.println("Données chargées sur le nœud " + nextNode.getNodeUrl() + ": " + partitions.get(i).size() + " éléments.");
                    loaded = true;
                    break;
                }
            }
            if (!loaded) {
                System.out.println("Aucun nœud disponible pour charger les données.");
            }
        }
    }
    System.out.println("Chargement de données terminé.");
    int totalBytes = getTotalBytes(partitions);
    System.out.println("Nombre total d'octets stockés sur tous les nœuds : " + totalBytes);
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

    private static void displayDataPartitions(List<Map<String, String>> partitions) {
        System.out.println("Partitions et leur contenu :");
        int totalBytes = 0;
        for (int i = 0; i < partitions.size(); i++) {
            Map<String, String> partition = partitions.get(i);
            System.out.println("Partition " + (i + 1) + ":");
            for (Map.Entry<String, String> entry : partition.entrySet()) {
                System.out.println("Clé : " + entry.getKey() + ", Valeur : " + entry.getValue());
                totalBytes += entry.getValue().getBytes().length;
            }
        }
        System.out.println("Nombre total d'octets stockés dans toutes les partitions : " + totalBytes);
    }

private static List<Map<String, String>> partitionData() throws IOException {
    List<Map<String, String>> partitions = new ArrayList<>();
    File folder = new File("data");
    File[] files = folder.listFiles();
    if (files != null) {
        for (File file : files) {
            if (file.isFile()) {
                Map<String, String> partition = new HashMap<>();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            partition.put(parts[0], parts[1]);
                        }
                    }
                }
                partitions.add(partition);
            }
        }
    }
    return partitions;
}
// Dans la méthode loadNodes(), gestion des nœuds null
private static void loadNodesData() throws Exception {
    System.out.println("Chargement de données sur les nœuds...");
    List<Map<String, String>> partitions = partitionData(); // Partitionnez vos données ici
    for (int i = 0; i < partitions.size(); i++) {
        ChordNode node = chord.getNode(i % NOMBRE_DE_NOEUDS); // Assure une répartition équilibrée
        if (node != null) {
            int dataSize = 0;
            for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                node.addData(entry.getKey(), entry.getValue());
                dataSize += entry.getValue().getBytes().length;
            }
            System.out.println("Données chargées sur le nœud " + node.getNodeUrl() + ": " + partitions.get(i).size() + " éléments.");
        } else {
            System.out.println("Nœud introuvable pour la partition " + i);
            // Recherchez un autre nœud disponible pour charger les données
            boolean loaded = false;
            for (int j = i + 1; j < NOMBRE_DE_NOEUDS; j++) {
                ChordNode nextNode = chord.getNode(j);
                if (nextNode != null) {
                    int dataSize = 0;
                    for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                        nextNode.addData(entry.getKey(), entry.getValue());
                        dataSize += entry.getValue().getBytes().length;
                    }
                    System.out.println("Données chargées sur le nœud " + nextNode.getNodeUrl() + ": " + partitions.get(i).size() + " éléments.");
                    loaded = true;
                    break;
                }
            }
            if (!loaded) {
                System.out.println("Aucun nœud disponible pour charger les données.");
                // Vous pouvez choisir d'effectuer d'autres actions comme notifier l'utilisateur
            }
        }
    }
    System.out.println("Chargement de données terminé.");
    int totalBytes = getTotalBytes(partitions);
    System.out.println("Nombre total d'octets stockés sur tous les nœuds : " + totalBytes);
}
// Dans la méthode displayPartitions(), affichage correct des clés et des valeurs
    private static void displayPartitions(List<Map<String, String>> partitions) {
    System.out.println("Partitions et leur contenu :");
    int totalBytes = 0;
    for (int i = 0; i < partitions.size(); i++) {
        Map<String, String> partition = partitions.get(i);
        System.out.println("Partition " + (i + 1) + ":");
        for (Map.Entry<String, String> entry : partition.entrySet()) {
            System.out.println("Clé : " + entry.getKey() + ", Valeur : " + entry.getValue());
            // Calculer la taille de chaque valeur en octets et l'ajouter à la taille totale
            totalBytes += entry.getValue().getBytes().length;
        }
    }
    System.out.println("Nombre total d'octets stockés dans toutes les partitions : " + totalBytes);
}

}
