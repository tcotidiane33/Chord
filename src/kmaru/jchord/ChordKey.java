package kmaru.jchord;

public class ChordKey {
    private String key;

    public ChordKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChordKey chordKey = (ChordKey) o;
        return key.equals(chordKey.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "ChordKey{" +
                "key='" + key + '\'' +
                '}';
    }

    public boolean isBetween(ChordKey startKey, ChordKey endKey) {
        // Comparer cette clé avec startKey et endKey
        // Si cette clé est supérieure ou égale à startKey et inférieure à endKey (dans l'ordre circulaire),
        // alors elle est entre les deux
        return this.getKey().compareTo(startKey.getKey()) >= 0 && this.getKey().compareTo(endKey.getKey()) < 0;
    }

    public ChordKey createNextKey() {
        // Implémentez la logique pour générer la clé suivante dans la séquence de clés
        // Cela dépendra de la manière dont vous définissez la séquence de clés dans votre système Chord
        // Par exemple, si vous utilisez une séquence linéaire ou un anneau, vous devez générer la clé suivante dans cet ordre

        // Ici, nous supposons que vous avez une méthode getNextKey() qui génère la clé suivante dans la séquence
        // Utilisez cette méthode pour obtenir la clé suivante
        return getNextKey();
    }

    private ChordKey getNextKey() {
        // Supposez que vous avez une implémentation qui retourne la clé suivante dans votre séquence de clés
        // Cela pourrait être basé sur une séquence linéaire, un anneau ou toute autre structure que vous utilisez dans votre système Chord

        // Exemple simple : incrémentation de la valeur de la clé actuelle
        // Notez que ceci est un exemple fictif et vous devez adapter cette logique à votre propre implémentation
        int currentValue = Integer.parseInt(key); // Supposez que la valeur de la clé est stockée sous forme d'entier dans key
        int nextValue = currentValue + 1; // Générer la valeur suivante dans la séquence
        return new ChordKey(String.valueOf(nextValue)); // Retourner une nouvelle instance de ChordKey avec la valeur suivante
    }

    public ChordKey createStartKey(int i) {
        // Générer une clé en fonction de l'index i
        String keyString = "start_key_" + i; // Exemple de génération de clé à partir de l'index i
        // Supposons que ChordKey ait un constructeur prenant une chaîne en tant que paramètre pour créer une clé
        return new ChordKey(keyString);
    }

}
