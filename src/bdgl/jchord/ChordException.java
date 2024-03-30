package bdgl.jchord;

/**
 * Exception personnalisée utilisée dans le projet Chord.
 */
public class ChordException extends Exception {

    /**
     * Crée une nouvelle instance de ChordException sans message d'erreur.
     */
    public ChordException() {
        super("An error occurred in the Chord network.");
    }

    /**
     * Crée une nouvelle instance de ChordException avec le message d'erreur spécifié.
     * @param message Le message d'erreur décrivant l'exception.
     */
    public ChordException(String message) {
        super(message);
    }

    /**
     * Crée une nouvelle instance de ChordException avec le message d'erreur spécifié et la cause sous-jacente.
     * @param message Le message d'erreur décrivant l'exception.
     * @param cause La cause sous-jacente de cette exception.
     */
    public ChordException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Crée une nouvelle instance de ChordException avec la cause sous-jacente.
     * @param cause La cause sous-jacente de cette exception.
     */
    public ChordException(Throwable cause) {
        super("An error occurred in the Chord network.", cause);
    }
}
