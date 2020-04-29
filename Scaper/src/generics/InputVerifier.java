package generics;


import java.util.function.UnaryOperator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Validates user input
 * 
 * @author George Tiersma
 */
public class InputVerifier
{
    // The smallest vertex size that the terrain and populations can be
    final int MIN_MESH_SIZE = 2;
    
    final int MAX_TERRAIN_SIZE = 1000;
    final int MAX_POPULATION_SIZE = 100;
    
    // The greatest number of digits that a spinner is allowed to have
    final int MAX_SPINNER_DIGIT_AMOUNT = 5;
    
    // Format that limits the allowed characters to digits and the negative (-)
    // symbol
    UnaryOperator<TextFormatter.Change> numericStyle;
    
    /**
     * CONSTRUCTOR
     */
    public InputVerifier()
    {
        numericStyle = content ->
        {
            TextFormatter.Change chanster = null;

            if (content.getText().matches("-?[0-9]*"))
            {
                chanster = content;
            }

            return chanster;
        };
    }
    
    /**
     * Formats a text field for numeric values
     * 
     * @param fieldster The text field
     */
    public void formatNumericTextField(TextField fieldster)
    {
        TextFormatter<String> formster = new TextFormatter<>(numericStyle);
        
        fieldster.setTextFormatter(formster);
    }
    
    /**
     * Formats a spinner for numeric values
     * 
     * @param spinster The spinner
     */
    public void formatNumericSpinner(Spinner spinster)
    {
        TextFormatter<String> formster = new TextFormatter<>(numericStyle);
        
        spinster.getEditor().setTextFormatter(formster);
    }
    
    /**
     * Checks if a given vertex size is valid for the terrain or a population
     * 
     * @param maxSize The largest allowed size
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    private boolean isMeshSizeValid(int maxSize, int size)
    {
        boolean valid = false;
        
        // If the size is not too big...
        if (size <= maxSize)
        {
            // ...and it is not too small...
            if (size >= MIN_MESH_SIZE)
            {
                // ...it is valid.
                valid = true;
            }
        }
        
        return valid;
    }
    
    /**
     * Checks if a given vertex size is valid for a population
     * 
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    public boolean isPopulationSizeValid(int size)
    {
        return isMeshSizeValid(MAX_POPULATION_SIZE, size);
    }
    
    /**
     * Checks if a value of a spinner is valid. Even though spinners may already
     * be formated with a regex, expressions applied to spinners are only able
     * to limit the type of characters, not the position of the characters or
     * the length of the string, so further validation is necessary.
     * 
     * @param mustBePositive Whether or not the spinner's value must be positive
     * @param value The value in the spinner in the form of a string
     * 
     * @return Whether or not the spinner's value is valid
     */
    public boolean isSpinnerValueValid(boolean mustBePositive, String value)
    {
        boolean valid = false;
        
        // As long as the spinner's value is not blank...
        if (!value.equals(""))
        {
            // Regex that forces the number to be within a certain digit count
            String regex = "\\d{0," + MAX_SPINNER_DIGIT_AMOUNT + "}";
        
            // If the value must be positive...
            if (mustBePositive)
            {
                // ...the regex must not allow 0.
                regex = "[1-9]" + regex;
            }
            // ...otherwise...
            else
            {
                // ...have the regex accept a single negative symbol at the
                // start of the string.
                regex = "-?" + regex;
            }
        
            // As long as the value is approved by the regex...
            if (value.matches(regex))
            {
                // ...it is valid.
                valid = true;
            }
        }
        // ...otherwise, if the value is blank, valid remains false.
        
        return valid;
    }
    
    /**
     * Checks if a given vertex size is valid for the terrain
     * 
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    public boolean isTerrainSizeValid(int size)
    {
        return isMeshSizeValid(MAX_TERRAIN_SIZE, size);
    }
}
