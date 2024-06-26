# chord-src v0.4
Jchord-src-0.1/
│
├── src/
│   └── bdgl/
│       └── jchord/
│            ├── Chord.java
│            ├── ChordException.java
│            ├── ChordKey.java
│            ├── ChordNode.java
│            ├── Finger.java
│            ├── NodeList.java
│            ├── Hash.java
│            ├── FingerTableEntry.java
│            └── Main.java
└── data/
    ├── file1.txt
    ├── file2.txt
    └── ...


bdgl.jchord
├── Chord.java
├── ChordException.java
├── ChordKey.java
├── ChordNode.java
├── Finger.java
├── FingerTable.java
├── FingerTableEntry.java
├── Hash.java
├── NodeList.java
└── Main.java

bdgl
├── Client.java
├── Serveur.java

# v0.3
command : ``java -cp "C:\Users\LENOVO\Downloads\jchord-src-0.1\out\production\jchord-src-0.1;C:\Users\LENOVO\AppData\Local\JetBrains\IntelliJ IDEA Community Edition 2023.3.3\lib\idea_rt.jar" kmaru.jchord.Main
``
Voici un exemple de mode d'utilisation des commandes avec des exemples en markdown :

### Commandes disponibles :

1. **add** *[nodeId]* *[key]* *[value]* : Ajoute une paire clé-valeur au nœud spécifié.
   Exemple : `add 0 key1 value1`

2. **read** *[nodeId]* *[key]* : Lit la valeur associée à la clé spécifiée à partir du nœud donné.
   Exemple : `read 0 key1`

3. **remove** *[nodeId]* *[key]* : Supprime la valeur associée à la clé spécifiée à partir du nœud donné.
   Exemple : `remove 0 key1`

4. **create** *[nodeId]* : Crée un nouveau nœud avec l'ID spécifié.
   Exemple : `create 6`

5. **drop** *[nodeId]* : Supprime le nœud avec l'ID spécifié.
   Exemple : `drop 6`

6. **list** : Affiche la liste des nœuds dans le réseau.

7. **exit** : Quitte l'application.

### Exemples :

#### Ajouter une paire clé-valeur :
```
add 0 key1 value1
```

#### Lire une valeur à partir d'un nœud :
```
read 0 key1
```

#### Supprimer une valeur à partir d'un nœud :
```
remove 0 key1
```

#### Créer un nouveau nœud :
```
create 6
```

#### Supprimer un nœud existant :
```
drop 6
```

#### Afficher la liste des nœuds dans le réseau :
```
list
```

#### Quitter l'application :
```
exit
```

Ces commandes permettent de gérer les opérations sur le réseau Chord, telles que l'ajout, la lecture, la suppression des données, la création et la suppression des nœuds, ainsi que l'affichage de la liste des nœuds dans le réseau.


```The provided code is a simple implementation of a Chord distributed hash table. It allows adding, reading, and removing data, as well as creating and dropping nodes. The code uses a `ChordAPI` class to interact with the Chord network.

            Here's a brief explanation of the code:

            1. The `initializeChord()` method initializes the Chord network by creating a specified number of nodes (`NUM_OF_NODES`). Each node is represented by a `ChordNode` object.

            2. The `handleCommand()` method processes user input commands. It supports the following commands:
            - `add`: Adds a key-value pair to a specified node.
                    - `read`: Reads the value associated with a specified key from a specified node.
            - `remove`: Removes a key-value pair from a specified node.
                    - `create`: Creates a new node in the Chord network.
            - `drop`: Drops a specified node from the Chord network.
            - `list`: Lists all nodes in the Chord network.
                    - `exit`: Stops the Chord network.

            3. The `main()` method initializes the Chord network, reads user input commands, and processes them using the `handleCommand()` method.

                    The code also includes a `BufferedReader` to read user input from the console. The `handleCommand()` method processes the input commands and performs the corresponding operations on the Chord network.

            To run the code, you need to compile and run the `ChordAPI` class. The `ChordAPI` class should be in the same directory as the `ChordNode` and `Hash` classes. The `ChordNode` and `Hash` classes are part of the Chord implementation and should be in the same directory as the `ChordAPI` class.

            Here's an example of how to run the code:

```bash
            javac ChordAPI.java
            java ChordAPI