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
    // The maximum number of digits that the input text for certain TextBoxes
    // can have
    private final byte MAX_DISPLACEMENT_STRENGTH_DIGIT_COUNT = 4;
    private final byte MAX_POPULATION_SIZE_DIGIT_COUNT = 3;
    private final byte MAX_TERRAIN_SIZE_DIGIT_COUNT = 4;
    // The maximum number of digits that spinners are allowed to have
    private final byte MAX_SPINNER_DIGIT_AMOUNT = 5;
    
    // The value used to represent a failure with parsing or validation
    private final short PARSE_FAIL = -1;
    
    // The smallest vertex size that the terrain and populations can be
    private final short MIN_MESH_SIZE = 2;
    // The least displaced the vertices in the terrain and populations can be
    private final short MIN_DISPLACEMENT_STRENGTH = 0;
    
    // Format that limits the allowed characters to digits and the negative (-)
    // symbol
    private UnaryOperator<TextFormatter.Change> numericStyle;
    
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
     * Gets the value that is used to indicate that a value has failed to parse
     * or validate
     * 
     * @return Indication value for a failed parse or validation
     */
    public byte getParseFailValue()
    {
        return PARSE_FAIL;
    }
    
    /**
     * Gets whether or not a number (in the form of a string) has a negative
     * sign in the wrong position
     * 
     * @param number The number in the form of a string to check for a misplaced
     *               dash
     * 
     * @return Whether or not a negative sign is misplaced in the number
     */
    public boolean hasMisplacedDash(String number)
    {
        // The position of the character in the string to begin searching for a
        // dash. Since the first position (0) is allowed to have a dash, it is
        // skipped.
        final byte STARTING_POSITION = 1;
        
        boolean foundMisplacedDash = false;
        
        int characterAmount = number.length();
        
        // For each character (excluding the first one)...
        for (int i = STARTING_POSITION; i < characterAmount; i++)
        {
            // ...if that character is a dash...
            if (number.charAt(i) == '-')
            {
                // ...the number has a misplaced dash.
                foundMisplacedDash = true;
                
                // Exit the loop
                i = characterAmount;
            }
        }
        
        return foundMisplacedDash;
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
        
        // As long as the spinner's value is not blank or just a negative
        // sign...
        if (!value.equals("") && !value.equals("-"))
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
        
            // Get if it is valid
            valid = value.matches(regex);
        }
        // ...otherwise, if the value is blank, valid remains false.
        
        return valid;
    }
    
    /**
     * Parses a certain value 
     * 
     * @param maxDigits The maximum number of digits the value is allowed to
     *                  have
     * @param minValue The minimum value that the parsing value is allowed to be
     * @param value The value to be parsed
     * 
     * @return The String value parsed into the form of a short. If any of the
     *         parsing validation failed, a -1 is returned.
     */
    private short parse(short maxDigits, short minValue, String value)
    {
        short parsedValue = PARSE_FAIL;
        
        // If the value is does not have too many digits or a negative symbol in
        // a strange place...
        if (value.length() <= maxDigits && !hasMisplacedDash(value))
        {
            // ...the string should be able to be parsed into a short, so parse
            // it.
            parsedValue = Short.parseShort(value);
            
            // If the parsed value is too small...
            if (parsedValue < minValue)
            {
                // ...set it back to a parsing-fail value.
                parsedValue = PARSE_FAIL;
            }
        }
        
        return parsedValue;
    }
    
    /**
     * Parses a given string of a displacement strength value into a short
     * 
     * @param strength The displacement strength to be parsed
     * 
     * @return The displacement strength parsed into the form of a short. If any
     *         of the parsing validation failed, a -1 is returned.
     */
    public short parseDisplacementStrength(String strength)
    {
        return parse(MAX_DISPLACEMENT_STRENGTH_DIGIT_COUNT,
                MIN_DISPLACEMENT_STRENGTH, strength);
    }
    
    /**
     * Parses a given string of a population width or height value into a short
     * 
     * @param size The population's size to be parsed
     * 
     * @return The size parsed into the form of a short. If any of the parsing
     *         validation failed, a -1 is returned.
     */
    public short parsePopulationSize(String size)
    {
        return parse(MAX_POPULATION_SIZE_DIGIT_COUNT, MIN_MESH_SIZE, size);
    }
    
    /**
     * Parses a given string of a terrain width or depth value into a short
     * 
     * @param size The terrain's size to be parsed
     * 
     * @return The size parsed into the form of a short. If any of the parsing
     *         validation failed, a -1 is returned.
     */
    public short parseTerrainSize(String size)
    {
        return parse(MAX_TERRAIN_SIZE_DIGIT_COUNT, MIN_MESH_SIZE, size);
    }
}
