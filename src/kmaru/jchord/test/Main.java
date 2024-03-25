package kmaru.jchord.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Hash;

public class Main {

    public static final String FONCTION_HASH = "SHA-1";
    public static final int LONGUEUR_CLE = 160;
    public static final int NOMBRE_DE_NOEUDS = 5;
    private static Chord chord = new Chord();

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream("result.log");

        String hote = InetAddress.getLocalHost().getHostAddress();
        int port = 9000;

        Hash.setFunction(FONCTION_HASH);
        Hash.setKeyLength(LONGUEUR_CLE);

        // Créer les nœuds initiaux
        createNodes(hote, port);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Entrez une commande ( - Create - | - Drop - | - List - | Load | - Read - | - Exit - ) : ");
            String commande = scanner.nextLine().trim();

            switch (commande.toLowerCase()) {
                case "create":
                    createNode();
                    break;
                case "drop":
                    dropNode();
                    break;
                case "list":
                    listNodes();
                    break;
                case "read":
                    readNode();
                    break;
                case "load":
                    loadNodes();
                    break;
                case "exit":
                    scanner.close();
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int index = Integer.parseInt(reader.readLine().trim());
        URL url = new URL("http", InetAddress.getLocalHost().getHostAddress(), 9000 + index, "");
        chord.createNode(url.toString());
        System.out.println("Nœud créé avec succès à l'index " + index + ".");
    }

    private static void dropNode() throws Exception {
        System.out.print("Entrez l'index du nœud à supprimer : ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int index = Integer.parseInt(reader.readLine().trim());
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
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int index = Integer.parseInt(reader.readLine().trim());
    System.out.print("Voulez-vous afficher la liste des fichiers (List) ou le contenu d'un fichier spécifique (File) ? ");
    String option = reader.readLine().trim().toLowerCase();
    if (option.equals("list")) {
        listFiles(chord.getNode(index));
        //listFiles(node);

    } else if (option.equals("file")) {
        System.out.print("Entrez le nom du fichier à lire : ");
        String fileName = reader.readLine().trim();
        readFile(chord.getNode(index), fileName);
    } else {
        System.out.println("Option non valide.");
    }
}

private static void listFiles(ChordNode node) {
    List<String> files = node.getFiles();
    if (files != null) {
        System.out.printf("Liste des fichiers sur le nœud : %s%n", node.getNodeUrl());
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
}


private static void readFile(ChordNode node, String fileName) {
    if (node != null) {
        String content = node.readFile(fileName);
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


    private static void readNode(Chord chord, int index) {
        ChordNode node = chord.getNode(index);
        if (node != null) {
            System.out.println("Contenu du nœud " + index + " : ");
            for (Map.Entry<String, String> entry : node.getAllData().entrySet()) {
                System.out.println("Clé : " + entry.getKey() + ", Valeur : " + entry.getValue());
            }
        } else {
            System.out.println("Nœud introuvable à l'index spécifié.");
        }
    }

    private static void loadNodes() throws Exception {
        List<Map<String, String>> partitions = partitionData(); // Partitionnez vos données ici
        for (int i = 0; i < partitions.size(); i++) {
            ChordNode node = chord.getNode(i % NOMBRE_DE_NOEUDS); // Assure une répartition équilibrée
            if (node != null) {
                for (Map.Entry<String, String> entry : partitions.get(i).entrySet()) {
                    node.addData(entry.getKey(), entry.getValue());
                }
            } else {
                System.out.println("Nœud introuvable pour la partition " + i);
                // Gérez les cas où un nœud est inaccessible ici
            }
        }
        System.out.println("Données chargées avec succès sur les nœuds.");
    }

    // Exemple de méthode de partitionnement de données (à adapter à vos besoins)
    private static List<Map<String, String>> partitionData() {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    partitions.add(partition);
                }
            }
        }
        return partitions;
    }
}
