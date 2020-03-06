package org.pitest.pitclipse.core;

@SuppressWarnings("checkstyle:LineLength")
public enum Mutators {

    CONDITIONALS_BOUNDARY("CONDITIONALS_BOUNDARY", "Conditionals Boundary", "Replaces the relational operators <, <=, >, >=", true),
    INCREMENTS("INCREMENTS", "Increments", "Mutates increments, decrements and assignment increments and decrements of local variables (stack variables). Replaces increments with decrements and vice versa", true),
    INVERT_NEGS("INVERT_NEGS", "Invert Negatives", "Inverts negation of integer and floating point numbers", true),
    MATH("MATH", "Math", "Replaces binary arithmetic operations for either integer or floating-point arithmetic with another operation", true),
    NEGATE_CONDITIONALS("NEGATE_CONDITIONALS", "Negate Conditionals", "Mutates all conditionals found", true),
    RETURN_VALS("RETURN_VALS", "Return Values", "Mutates the return values of method calls. Depending on the return type of the method another mutation is used", true),
    VOID_METHOD_CALLS("VOID_METHOD_CALLS", "Void Method Call", "Removes method calls to void methods", true),
    CONSTRUCTOR_CALLS("CONSTRUCTOR_CALLS", "Constructor Call", "Replaces constructor calls with null values", false),
    EMPTY_RETURNS("EMPTY_RETURNS", "Empty Returns", "Replaces return values with an 'empty' value", false),
    FALSE_RETURNS("FALSE_RETURNS", "False Returns", "Replaces primitive and boxed boolean return values with false", false),
    INLINE_CONSTS("INLINE_CONSTS", "Inline Constant", "Mutates inline constants. An inline constant is a literal value assigned to a non-final variable", false),
    NULL_RETURNS("NULL_RETURNS", "Null Returns", "Replaces return values with null. Method that can be mutated by the EMPTY_RETURNS mutator or that are directly annotated with NotNull are not mutated", false),
    NON_VOID_METHOD_CALLS("NON_VOID_METHOD_CALLS", "Non Void Method Call", "Removes method calls to non void methods. Their return value is replaced by the Java Default Value for that specific type", false),
    PRIMITIVE_RETURNS("PRIMITIVE_RETURNS", "Primite Returns", "Replaces int, short, long, char, float and double return values with 0", false),
    REMOVE_CONDITIONALS("REMOVE_CONDITIONALS", "Remove Conditionals", "Removes all conditionals statements such that the guarded statements always execute", false),
    REMOVE_INCREMENTS("REMOVE_INCREMENTS", "Remove Increments", "Removes local variable increments", false),
    TRUE_RETURNS("TRUE_RETURNS", "True Returns", "Replaces primitive and boxed boolean return values with true", false),
    EXPERIMENTAL_ARGUMENT_PROPAGATION("EXPERIMENTAL_ARGUMENT_PROPAGATION", "Experimentation Argument Propagation", "Replaces method call with one of its parameters of matching type", false),
    EXPERIMENTAL_BIG_INTEGER("EXPERIMENTAL_BIG_INTEGER", "Experimental Big Integer", "Swaps big integer methods", false),
    EXPERIMENTAL_NAKED_RECEIVER("EXPERIMENTAL_NAKED_RECEIVER", "Experimental Naked Receiver", "Replaces method call with a naked receiver", false),
    EXPERIMENTAL_MEMBER_VARIABLE("EXPERIMENTAL_MEMBER_VARIABLE", "Experimental Member Variable", "Removes assignments to member variables. Can even remove assignments to final members. The members will be initialized with their Java Default Value", false),
    EXPERIMENTAL_SWITCH_MUTATOR("EXPERIMENTAL_SWITCH", "Experimental Switch", "Finds the first label within a switch statement that differs from the default label. Mutates the switch statement by replacing the default label (wherever it is used) with this label. ALl the other labels are replaced by the default one", false),
    ABS("ABS", "Negation", "Replaces any use of a numeric variable (local variable, field, array cell) with its negation", false),
    AOR("AOR", "Arithmetic Operator Replacement", "Like the Math mutator, replaces binary arithmetic operations for either integer or floating-point arithmetic with another operation", false),
    AOD("AOD", "Arithmetic Operator Deletion", "Replaces an arithmetic operation with one of its members", false),
    CRCR("CRCR", "Constant Replacement", "Like the Inline Constant mutator, mutates inline constant", false),
    OBBN("OBBN", "Bitwise Operator", "Mutates bitwise and (&) and or (|)", false),
    ROR("ROR", "Relational Operator Replacement", "Replaces a relational operator with another one", false),
    UOI("UOI", "Unary Operator Insertion", "Inserts a unary operator (increment or decrement) to a variable call. Affects local variables, array variables, fields and parameters", false);
    
    private final String id;
    
    private final String name;
    
    private final String description;
    
    private final boolean activeByDefault;
    
    private Mutators(String id, String name, String description, boolean activeByDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activeByDefault = activeByDefault;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActiveByDefault() {
        return activeByDefault;
    }
    
}
